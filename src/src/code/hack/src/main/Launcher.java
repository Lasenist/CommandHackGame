package code.hack.src.main;


import code.hack.src.application.Application;
import code.hack.src.application.programs.connectionmanager.ConnectionManager;
import code.hack.src.application.tools.passwordcracker.PasswordCracker;
import code.hack.src.network.connection.Session;
import code.hack.src.network.server.Server;
import lib.cliche.src.CLIException;
import lib.cliche.src.Command;
import lib.cliche.src.Shell;
import lib.cliche.src.ShellFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Lasen on 27/08/2015.
 * Main Application
 */
public class Launcher
{
  /*
  * V A R I A B L E S
  */
  public static String localhost = "126.1.21.6";
  private static Shell commandShell;
  private static NetworkManager networkManager;
  private static ArrayList<Object> memory;

  public static void main( String[] args ) throws IOException, InterruptedException
  {
    init();
    commandShell.commandLoop();
  }

  /*
  * S E T T E R S
  */
  private static void init()
  {
    memory = new ArrayList<>();
    networkManager = new NetworkManager();
    commandShell = ShellFactory.createConsoleShell( localhost, "CommandShell V1.0 \n", new Launcher() );

    memory.add( new ConnectionManager( commandShell, networkManager ) );
    memory.add( new PasswordCracker( commandShell ) );
  }

  /*
  * M E T H O D S
  */

  /**
   * Adds text to the command shell.
   * <p>
   * Similar to returning a String from a command, except this automatically
   * creates a empty line below the new text.
   *
   * @param feedback the text you want to display to the user.
   */
  private void addToOutput( final String feedback )
  {
    commandShell.addToOutput( feedback );
  }


  /*
  * C O M M A N D S
  */

  /**
   * Attempt to connect to a server.
   *
   * @param ip the ip address of the server
   */
  @Command( description = "Attempt to connect to the given ip address" )
  public void connect( final String ip ) throws Exception
  {
    final Session newSession = networkManager.createConnection( localhost, ip );

    if ( newSession != null )
    {
      commandShell.setSession( newSession );
      addToOutput( "Created a new connection with " + ip );
    }
    else
    {
      addToOutput( "Unknown or invalid ip address" );
    }
  }

  @Command( description = "Attempt to disconnect to the current server" )
  public void disconnect() throws CLIException
  {
    if ( commandShell.getSession() != null )
    {
      commandShell.clearSession();
      addToOutput( "Disconnected from server" );
    }
    else
    {
      addToOutput( "You're not connected to anything. This command wont normally be available atm but I'm too busy " +
              "working on less trivial problems");
    }
  }

  @Command( description = "View available software to run" )
  public void run()
  {
    memory.stream()
            .filter( o -> o instanceof Application )
            .forEach( o -> addToOutput( ( (Application) o ).getName() ) );
  }

  @Command( description = "Run software you have access to")
  public void run( final String applicationName )
  {
    for ( final Object o : memory )
    {
      if ( o instanceof Application )
      {
        final Application app = (Application) o;
        if ( ( app.getName().equals( applicationName ) ) )
        {
          addToOutput( "Attempting to launch " + app.getName() + "..." );
          app.init();
        }
      }
    }
  }

  @Command( description = "View current proxy chain", abbrev = "vp", name = "viewProxies")
  public void viewProxies()
  {
    for ( final Server server : commandShell.getProxies() )
    {
      addToOutput( server.getIp() );
    }
  }


}
