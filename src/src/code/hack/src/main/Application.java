package code.hack.src.main;


import code.hack.src.network.manager.NetworkManager;
import lib.cliche.src.*;

import java.io.IOException;

/**
 * Created by Lasen on 27/08/2015.
 * Main Application
 */
public class Application
{
  /*
  * V A R I A B L E S
  */
  private static String localhost = "126.1.21.6";
  public static Shell commandShell;
  public static String prompt = "localhost";
  private static NetworkManager networkManager;

  public static void main( String[] args ) throws IOException, InterruptedException, MaxAttemptsException
  {
    init();
    commandShell.commandLoop();
  }

  /*
  * S E T T E R S
  */
  private static void init()
  {
    networkManager = new NetworkManager();
    commandShell = ShellFactory.createConsoleShell( prompt, "CommandHack V1.0 \n", new Application() );
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
  public void connect( final String ip )
  {
    commandShell.setSession( networkManager.createConnection( localhost, ip ) );
    addToOutput( "Created a new connection with " + ip );
  }

  @Command( description = "Attempt to disconnect to the current server" )
  public void disconnect() throws CLIException
  {
    commandShell.clearSession();
    addToOutput( "Disconnected from server" );
  }


}
