/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package lib.cliche.src;

import code.hack.src.util.Fn;
import lib.cliche.src.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

/**
 * Console IO subsystem.
 * This is also one of special command handlers and is responsible
 * for logging (duplicating output) and execution of scripts.
 *
 * @author ASG
 */
public class ConsoleIO implements Input, Output, ShellManageable
{
  final ShellFrame shellFrame;

  public ConsoleIO( BufferedReader in, PrintStream out, PrintStream err, final ShellFrame shellFrame )
  {
    this.in = in;
    this.out = out;
    this.err = err;
    this.shellFrame = shellFrame;
  }

  public ConsoleIO( final ShellFrame shellFrame )
  {
    this( new BufferedReader( new InputStreamReader( System.in ) ),
            System.out, System.err, shellFrame );
  }

  private BufferedReader in;
  private PrintStream out;
  private PrintStream err;

  private int lastCommandOffset = 0;

  public String readCommand( final List<String> path )
  {
    return readCommand( path, true );
  }

  public String readCommand( List<String> path, final boolean showPrompt )
  {
    try
    {
      String prompt = showPrompt ? Strings.joinStrings( path, false, '/' ) + ">" : Fn.EMPTY_STRING;
      switch ( inputState )
      {
        case USER:
          return readUsersCommand( prompt );
        case SCRIPT:
          String command = readCommandFromScript( prompt );
          if ( command != null )
          {
            return command;
          }
          else
          {
            closeScript();
            return readUsersCommand( prompt );
          }
      }
      return readUsersCommand( prompt );
    }
    catch ( IOException ex )
    {
      throw new Error( ex );
    }
  }

  private static final String USER_PROMPT_SUFFIX = "";
  private static final String FILE_PROMPT_SUFFIX = "$ ";

  private enum InputState
  {
    USER, SCRIPT
  }

  private InputState inputState = InputState.USER;

  private String readUsersCommand( String prompt ) throws IOException
  {
    String completePrompt = prompt + USER_PROMPT_SUFFIX;
    print( completePrompt );
    lastCommandOffset = completePrompt.length();

    String command = in.readLine();
    if ( log != null )
    {
      log.println( command );
    }
    return command;
  }

  private BufferedReader scriptReader = null;

  private String readCommandFromScript( String prompt ) throws IOException
  {
    String command = scriptReader.readLine();
    if ( command != null )
    {
      String completePrompt = prompt + FILE_PROMPT_SUFFIX;
      print( completePrompt );
      lastCommandOffset = completePrompt.length();
    }
    return command;
  }

  private void closeScript() throws IOException
  {
    if ( scriptReader != null )
    {
      scriptReader.close();
      scriptReader = null;
    }
    inputState = InputState.USER;
  }

//  @Command( description = "Reads commands from file" )
//  public void runScript(
//          @Param( name = "filename", description = "Full file name of the script" )
//          String filename
//  ) throws FileNotFoundException
//  {
//
//    scriptReader = new BufferedReader( new InputStreamReader( new FileInputStream( filename ) ) );
//    inputState = InputState.SCRIPT;
//  }


  public void outputHeader( String text )
  {
    outputHeader( text, null );
  }

  public void outputHeader( final String text, final String path )
  {
    if ( text!= null )
    {
      println( text );
    }
    if ( path != null )
    {
      print( path );
    }
  }

  public void output( Object obj, OutputConversionEngine oce )
  {
    if ( obj == null )
    {
      return;
    }
    else
    {
      obj = oce.convertOutput( obj );
    }

    if ( obj.getClass().isArray() )
    {
      int length = Array.getLength( obj );
      for ( int i = 0; i < length; i++ )
      {
        output( Array.get( obj, i ), 0, oce );
      }
    }
    else if ( obj instanceof Collection )
    {
      for ( Object elem : (Collection) obj )
      {
        output( elem, 0, oce );
      }
    }
    else
    {
      output( obj, 0, oce );
    }
  }

  private void output( Object obj, int indent, OutputConversionEngine oce )
  {
    if ( obj == null )
    {
      return;
    }
    obj = oce.convertOutput( obj );

    for ( int i = 0; i < indent; i++ )
    {
      print( "\t" );
    }

    if ( obj == null )
    {
      println( "(null)" );
    }
    else if ( obj.getClass().isPrimitive() || obj instanceof String )
    {
      println( obj );
    }
    else if ( obj.getClass().isArray() )
    {
      println( "Array" );
      int length = Array.getLength( obj );
      for ( int i = 0; i < length; i++ )
      {
        output( Array.get( obj, i ), indent + 1, oce );
      }
    }
    else if ( obj instanceof Collection )
    {
      println( "Collection" );
      for ( Object elem : (Collection) obj )
      {
        output( elem, indent + 1, oce );
      }
    }
    else if ( obj instanceof Throwable )
    {
      println( obj ); // class and its message
      ( (Throwable) obj ).printStackTrace( out );
    }
    else
    {
      println( obj );
    }
  }

  private void print( Object x )
  {
    out.print( x );
    if ( log != null )
    {
      log.print( x );
    }
  }

  private void println( Object x )
  {
    shellFrame.addToOutput( (String) x );
    out.println( x );
    if ( log != null )
    {
      log.println( x );
    }
  }

  private void printErr( Object x )
  {
    err.print( x );
    if ( log != null )
    {
      log.print( x );
    }
  }

  private void printlnErr( Object x )
  {
    err.println( x );
    if ( log != null )
    {
      log.println( x );
    }
  }

  public void outputException( String input, TokenException error )
  {
    int errIndex = error.getToken().getIndex() + lastCommandOffset;
    while ( errIndex-- > 0 )
    {
      printErr( "-" );
    }
    for ( int i = 0; i < error.getToken().getString().length(); i++ )
    {
      printErr( "^" );
    }
    printlnErr( "" );
    printlnErr( error );
  }

  public void outputException( Throwable e )
  {
    printlnErr( e );
    if ( e.getCause() != null )
    {
      printlnErr( e.getCause() );
    }
  }

  private PrintStream log = null;

  private boolean isLoggingEnabled()
  {
    return log != null;
  }

  private int loopCounter = 0;

  public void cliEnterLoop()
  {
    if ( isLoggingEnabled() )
    {
      loopCounter++;
    }
  }

  public void cliLeaveLoop()
  {
    if ( isLoggingEnabled() )
    {
      loopCounter--;
    }
//    if ( loopCounter < 0 )
//    {
//      disableLogging();
//    }
  }

//  @Command( description = "Sets up logging, which duplicates all subsequent output in a file" )
//  public void enableLogging(
//          @Param( name = "fileName", description = "Name of the logfile" ) String filename
//  ) throws FileNotFoundException
//  {
//
//    log = new PrintStream( filename );
//    loopCounter = 0;
//  }

//  @Command( description = "Turns off logging" )
//  public String disableLogging()
//  {
//    if ( log != null )
//    {
//      log.close();
//      log = null;
//      return "Logging disabled";
//    }
//    else
//    {
//      return "Logging is already disabled";
//    }
}
