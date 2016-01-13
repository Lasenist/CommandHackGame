/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

/*
 *   Introducing the asg.cliche (http://cliche.sourceforge.net/)
 * Cliche is to be a VERY simple reflection-based command line shell
 * to provide simple CLI for simple applications.
 * The name formed as follows: "CLI Shell" --> "CLIShe" --> "Cliche".
 */

package lib.cliche.src;

import code.hack.src.application.Application;
import code.hack.src.listeners.HandlerListener;
import code.hack.src.main.PlayerServer;
import code.hack.src.network.connection.Session;
import code.hack.src.network.server.Server;
import code.hack.src.network.server.handlers.CommandHandler;
import code.hack.src.network.server.handlers.ConnectionHandler;
import code.hack.src.util.FileUtil;
import code.hack.src.util.Fn;
import lib.cliche.src.util.ArrayHashMultiMap;
import lib.cliche.src.util.MultiMap;
import lib.cliche.src.util.Strings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Shell is the class interacting with user.
 * Provides the command loop.
 * All logic lies here.
 *
 * @author ASG
 */
public class Shell
{

  final ShellFrame shellFrame;
  public static String PROJECT_HOMEPAGE_URL = "http://cliche.sourceforge.net";

  private Output output;
  private Input input;
  private String appName;

  private Server playerServer;
  private Session session;
  private ArrayList<Server> proxies;
  private ArrayList<HandlerListener> handlerListeners;

  public static class Settings
  {
    private final Input input;
    private final Output output;
    private final MultiMap<String, Object> auxHandlers;
    private final boolean displayTime;

    public Settings( Input input, Output output, MultiMap<String, Object> auxHandlers, boolean displayTime )
    {
      this.input = input;
      this.output = output;
      this.auxHandlers = auxHandlers;
      this.displayTime = displayTime;
    }

    public Settings createWithAddedAuxHandlers( MultiMap<String, Object> addAuxHandlers )
    {
      MultiMap<String, Object> allAuxHandlers = new ArrayHashMultiMap<>( auxHandlers );
      allAuxHandlers.putAll( addAuxHandlers );
      return new Settings( input, output, allAuxHandlers, displayTime );
    }

  }

  public Settings getSettings()
  {
    return new Settings( input, output, auxHandlers, displayTime );
  }

  public void setSettings( Settings s )
  {
    input = s.input;
    output = s.output;
    displayTime = s.displayTime;
    for ( String prefix : s.auxHandlers.keySet() )
    {
      for ( Object handler : s.auxHandlers.get( prefix ) )
      {
        addAuxHandler( handler, prefix );
      }
    }
  }

  /**
   * Shell's constructor
   * You probably don't need this one, see methods of the ShellFactory.
   *
   * @param commandTable CommandTable to store commands
   * @param path         Shell's location: list of path elements.
   * @see lib.cliche.src.ShellFactory
   */
  public Shell( CommandTable commandTable, List<String> path )
  {
    this.commandTable = commandTable;
    this.path = path;
    this.proxies = new ArrayList<>();
    this.handlerListeners = new ArrayList<>();
    shellFrame = new ShellFrame( this );
    addAuxHandler( new ConsoleIO( shellFrame ), "!" );
    playerServer = new PlayerServer( "126.1.21.6", this );
    addMainHandler( playerServer );
  }

  private CommandTable commandTable;

  /**
   * @return the CommandTable for this shell.
   */
  public CommandTable getCommandTable()
  {
    return commandTable;
  }

  private OutputConversionEngine outputConverter = new OutputConversionEngine();

  /**
   * Call this method to get OutputConversionEngine used by the Shell.
   *
   * @return a conversion engine.
   */
  public OutputConversionEngine getOutputConverter()
  {
    return outputConverter;
  }

  private InputConversionEngine inputConverter = new InputConversionEngine();

  /**
   * Call this method to get InputConversionEngine used by the Shell.
   *
   * @return a conversion engine.
   */
  public InputConversionEngine getInputConverter()
  {
    return inputConverter;
  }

  public Session getSession()
  {
    return session;
  }
  public ArrayList<Server> getProxies(){ return proxies; }

  private MultiMap<String, Object> auxHandlers = new ArrayHashMultiMap<>();
  private List<Object> allHandlers = new ArrayList<>();


  /**
   * Method for registering command hanlers (or providers?)
   * You call it, and from then the Shell has all commands declare in
   * the handler object.
   * <p>
   * This method recognizes if it is passed ShellDependent or ShellManageable
   * and calls corresponding methods, as described in those interfaces.
   *
   * @param handler Object which should be registered as handler.
   * @param prefix  Prefix that should be prepended to all handler's command names.
   * @see lib.cliche.src.ShellDependent
   * @see lib.cliche.src.ShellManageable
   */
  public void addMainHandler( Object handler, String prefix )
  {
    if ( handler == null )
    {
      throw new NullPointerException();
    }
    allHandlers.add( handler );

    addDeclaredMethods( handler, prefix );
    inputConverter.addDeclaredConverters( handler );
    outputConverter.addDeclaredConverters( handler );

    if ( handler instanceof ShellDependent )
    {
      ( (ShellDependent) handler ).cliSetShell( this );
    }

    for ( final HandlerListener listener : handlerListeners )
    {
      listener.handlerAdded( handler );
    }
  }

  public void addMainHandler( final Object handler )
  {
    addMainHandler( handler, Fn.EMPTY_STRING );
  }

  public void removeHandler( final Object handler )
  {
    allHandlers.remove( handler );
    removeDeclaredMethods( handler );

    for ( final HandlerListener listener : handlerListeners )
    {
      listener.handlerRemoved( handler );
    }
  }

  /**
   * This method is very similar to addMainHandler, except ShellFactory
   * will pass all handlers registered with this method to all this shell's subshells.
   *
   * @param handler Object which should be registered as handler.
   * @param prefix  Prefix that should be prepended to all handler's command names.
   * @see lib.cliche.src.Shell#addMainHandler(java.lang.Object, java.lang.String)
   */
  public void addAuxHandler( Object handler, String prefix )
  {
    if ( handler == null )
    {
      throw new NullPointerException();
    }
    auxHandlers.put( prefix, handler );
    allHandlers.add( handler );

    addDeclaredMethods( handler, prefix );
    inputConverter.addDeclaredConverters( handler );
    outputConverter.addDeclaredConverters( handler );

    if ( handler instanceof ShellDependent )
    {
      ( (ShellDependent) handler ).cliSetShell( this );
    }
  }

  private void addDeclaredMethods( Object handler, String prefix ) throws SecurityException
  {
    for ( Method m : handler.getClass().getMethods() )
    {
      Command annotation = m.getAnnotation( Command.class );
      if ( annotation != null )
      {
        commandTable.addMethod( m, handler, prefix );
      }
    }
  }

  private void removeDeclaredMethods( final Object handler )
  {
    for ( Method m : handler.getClass().getMethods() )
    {
      Command annotation = m.getAnnotation( Command.class );
      if ( annotation != null )
      {
        commandTable.removeMethod( m );
      }
    }
  }

  private Throwable lastException = null;

  /**
   * Returns last thrown exception
   */
//  @Command( description = "Returns last thrown exception" ) // Shell is self-manageable, isn't it?
//  public Throwable getLastException()
//  {
//    return lastException;
//  }

  private List<String> path;

  /**
   * @return list of path elements, as it was passed in constructor
   */
  public List<String> getPath()
  {
    return path;
  }

  /**
   * Function to allow changing path at runtime; use with care to not break
   * the semantics of sub-shells (if you're using them) or use to emulate
   * tree navigation without subshells
   *
   * @param path New path
   */
  public void setPath( List<String> path )
  {
    this.path = path;
    shellFrame.updatePath();
  }

  /**
   * Runs the command session.
   * Create the Shell, then run this method to listen to the user,
   * and the Shell will invoke Handler's methods.
   *
   * @throws java.io.IOException when can't readLine() from input.
   */
  public void commandLoop() throws IOException
  {
    shellFrame.initUI();
    for ( Object handler : allHandlers )
    {
      if ( handler instanceof ShellManageable )
      {
        ( (ShellManageable) handler ).cliEnterLoop();
      }
    }
    String command = "";
    while ( ! command.trim().equals( "exit" ) )
    {
      shellFrame.repaint();
//      try
//      {
//        if ( false )
//        {
//          command = input.readCommand( path );
//          processLine( command );
//        }
//      }
//      catch ( TokenException te )
//      {
//        lastException = te;
//        output.outputException( command, te );
//      }
//      catch ( CLIException clie )
//      {
//        lastException = clie;
//        if ( ! command.trim().equals( "exit" ) )
//        {
//          output.outputHeader( lastException.getMessage() );
//        }
//      }
    }
    for ( Object handler : allHandlers )
    {
      if ( handler instanceof ShellManageable )
      {
        ( (ShellManageable) handler ).cliLeaveLoop();
      }
    }
  }

  private void outputHeader( String header, Object[] parameters )
  {
    shellFrame.addToOutput( String.format( header, parameters ) );
  }

  private static final String HINT_FORMAT = "This is %1$s, running on Cliche Shell\n" +
          "For more information on the Shell, enter ?help";

  /**
   * You can operate Shell linewise, without entering the command loop.
   * All output is directed to shell's Output.
   *
   * @param line Full command line
   * @throws lib.cliche.src.CLIException This may be TokenException
   * @see lib.cliche.src.Output
   */
  public void processLine( String line ) throws CLIException, InvocationTargetException, IllegalAccessException
  {
    if ( line.trim().equals( "?" ) )
    {
      output.output( String.format( HINT_FORMAT, appName ), outputConverter );
    }
    else
    {
      List<Token> tokens = Token.tokenize( line );
      if ( tokens.size() > 0 )
      {
        String discriminator = tokens.get( 0 ).getString();
        processCommand( discriminator, tokens );
      }
    }
  }

  private void processCommand( String discriminator, List<Token> tokens ) throws
          CLIException, InvocationTargetException, IllegalAccessException
  {
    assert discriminator != null;
    assert ! discriminator.equals( "" );

    ShellCommand commandToInvoke = commandTable.lookupCommand( discriminator, tokens, shellFrame.isAnsweringQuestions() );

    Class[] paramClasses = commandToInvoke.getMethod().getParameterTypes();
    Object[] parameters = inputConverter.convertToParameters( tokens, paramClasses,
            commandToInvoke.getMethod().isVarArgs() );

    outputHeader( commandToInvoke.getHeader(), parameters );

    long timeBefore = Calendar.getInstance().getTimeInMillis();
    Object invocationResult = commandToInvoke.invoke( parameters );

    if ( invocationResult instanceof Response )
    {
      final Response r = (Response) invocationResult;

      switch ( r.getResponse() )
      {
        case Response.FINISHED:
          break;

        case Response.REQUEST_INPUT:
          shellFrame.setInputRequest( r.getInputRequest() );
          break;

        case Response.UPDATE:
          updateHandlersFromSession();
          break;
      }

      if ( r.getMessage() != null )
      {
        addAllToOutput( r.getMessage() );
      }
    }

    if ( session != null )
    {
      updatePathFromSession();
    }

    long timeAfter = Calendar.getInstance().getTimeInMillis();

    if ( invocationResult != null && ! ( invocationResult instanceof Response ) )
    {
      if ( invocationResult instanceof ArrayList )
      {
        final ArrayList list = (ArrayList) invocationResult;
        for ( final Object string : list )
        {
          shellFrame.addToOutput( (String) string );
        }
      }
    }
    if ( displayTime )
    {
      final long time = timeAfter - timeBefore;
      if ( time != 0L )
      {
        output.output( String.format( TIME_MS_FORMAT_STRING, time ), outputConverter );
      }
    }
  }

  private List<Token> requestInput( final String question, final String commandToInvokeName ) throws CLIException
  {
    //Ask user the question
    addToOutput( question );
    //Get input from user
    final String line = input.readCommand( path, false );
    //Construct the line to tokenize
    return Token.tokenize( commandToInvokeName + Fn.SPACE + line );
  }

  private static final String TIME_MS_FORMAT_STRING = "time: %d ms";

  private boolean displayTime = false;

  /**
   * Turns command execution time display on and off
   *
   * @param displayTime true if do display, false otherwise
   */
//  @Command( description = "Turns command execution time display on and off" )
//  public void setDisplayTime(
//          @Param( name = "do-display-time", description = "true if do display, false otherwise" )
//          boolean displayTime )
//  {
//    this.displayTime = displayTime;
//  }

  /**
   * Hint is some text displayed before the command loop and every time user enters "?".
   */
  public void setAppName( String appName )
  {
    this.appName = appName;
  }

  public String getAppName()
  {
    return appName;
  }

  public List<Object> getHandlers()
  {
    return allHandlers;
  }

  public void addToOutput( final String value )
  {
//    output.outputHeader( value );
    shellFrame.addToOutput( value );
  }

  public void addAllToOutput( final ArrayList<String> values )
  {
    for ( final String string : values )
    {
      shellFrame.addToOutput( string );
    }
  }

  public void addToOutputWithPath( final String value )
  {
    output.outputHeader( value, Strings.joinStrings( path, false, '/' ) + ">" );
  }

  public void setSession( final Session session )
  {
    this.session = session;
    shellFrame.getFrame().requestFocus();
    final ArrayList<Server> currentProxies = new ArrayList<>( proxies );
    session.setProxyChain( currentProxies );
    final ArrayList<String> list = new ArrayList<>();
    list.add( session.getRequestedServer().getIp() );
    path = list;

    if ( this.session != null )
    {
      final ConnectionHandler connectionHandler = new ConnectionHandler( session );
      addMainHandler( connectionHandler );
      session.addHandler( connectionHandler );
    }

    updateHandlersFromSession();
    updatePathFromSession();
  }

  public void clearSession() throws CLIException
  {
    for ( Object o : session.getHandlers() )
    {
      removeHandler( o );
    }
    session = null;
  }

  public void updateHandlersFromSession()
  {
    if ( session != null )
    {
      for ( CommandHandler handler : session.getHandlers() )
      {
        if( !allHandlers.contains( handler ) )
        {
          addMainHandler( handler );
        }
      }
    }
  }

  private void updatePathFromSession()
  {
    if ( session != null )
    {
      setPath( session.getCurrentPathAsArray() );
    }
  }

  public void addProxy( final Server server )
  {
    if ( !proxies.contains( server ) && server.isProxyEnabled() )
    {
      proxies.add( server );
    }
  }

  public void addHandlerListener( final HandlerListener listener )
  {
    handlerListeners.add( listener );
  }

  public void removeHandlerListener( final HandlerListener listener )
  {
    handlerListeners.remove( listener );
  }


  @Command( description = "Show a list of all applications available to run" )
  public void run()
  {
    final ArrayList<Application> applications = getAllAvailableApplications();

    if ( applications.isEmpty() )
    {
      addToOutput( "No applications found" );
    }
    else
    {
      for ( final Application application : applications )
      {
        addToOutput( application.getName() );
      }
    }
  }

  @Command( description = "Attempt to run an application your computer has access to" )
  public void run( final String applicationName )
  {
    boolean applicationFound = false;
    for ( final Application application : getAllAvailableApplications() )
    {
      if ( application.getName().equals( applicationName ) )
      {
        applicationFound = true;
        if ( !application.isrunning() )
        {
          application.init();
          addToOutput( "Attempting to launch application " + application.getName() );
        }
        else
        {
          addToOutput( application.getName() + " is already running" );
        }
      }
    }

    if ( !applicationFound )
    {
      addToOutput( "Application not found" );
    }
  }

  private ArrayList<Application> getAllAvailableApplications()
  {
    final ArrayList<Application> applications = FileUtil.getAllApplications( playerServer.getBaseFolder() );
    final Session session = getSession();
    if ( session != null && session.getAccount() != null )
    {
      applications.addAll( FileUtil.getAllApplications( session.getRequestedServer().getBaseFolder() ) );
    }
    return applications;
  }
}
