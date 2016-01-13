package code.hack.src.main;

import code.hack.src.application.programs.connectionmanager.ConnectionManager;
import code.hack.src.application.programs.serverindex.ServerIndex;
import code.hack.src.application.tools.passwordcracker.PasswordCracker;
import code.hack.src.network.connection.Session;
import code.hack.src.network.server.Server;
import code.hack.src.util.NetworkUtil;
import lib.cliche.src.CLIException;
import lib.cliche.src.Command;
import lib.cliche.src.Shell;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Lasen on 08/01/16.
 * Object for the players server
 */
public class PlayerServer extends Server
{
  final Shell shell;
  final ArrayList<String> homePath;
  final HashMap<String,Server> serverIndex;

  public PlayerServer( final String ip, final Shell shell )
  {
    super( ip );
    this.shell = shell;
    baseFolder.addFolder( new ConnectionManager( shell, this ) );
    baseFolder.addFolder( new PasswordCracker( shell ) );
    baseFolder.addFolder( new ServerIndex( shell, this ) );
    homePath = new ArrayList<>();
    serverIndex = new HashMap<>();
    homePath.add( ip );

    //TODO: Remove the below - dummy data
    try
    {
      serverIndex.put( "129.4.3.78", Launcher.networkManager.getServer( "129.4.3.78" ) );
      serverIndex.put( "126.1.56.201", Launcher.networkManager.getServer( "126.1.56.201" ) );
    }
    catch ( CLIException e )
    {
      e.printStackTrace();
    }

  }

  public HashMap<String, Server> getServerIndex()
  {
    return serverIndex;
  }

  public void disconnect( final Session session, final boolean forced )
  {
    super.disconnect( session, forced );
    try
    {
      shell.clearSession();
      shell.updateHandlersFromSession();
      shell.setPath( homePath );
      shell.addToOutput( forced ? "Forcibly disconnected from server" : "Disconnected from server" );
    }
    catch ( CLIException e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Attempt to connect to a server.
   *
   * @param ip the ip address of the server
   */
  @Command( description = "Attempt to connect to the given IP address" )
  public void connect( final String ip ) throws Exception
  {
    final Session session = shell.getSession();
    if ( session != null )
    {
      endSession( session );
    }

    if ( NetworkUtil.isValidIp( ip ) )
    {
      if ( serverIndex.get( ip ) != null )
      {
        final Session newSession = Launcher.networkManager.createConnection( this, ip );
        if ( newSession != null )
        {
          shell.setSession( newSession );
          shell.addToOutput( "Created a new connection with " + ip );
        }
      }
      else
      {
        shell.addToOutput( "Unknown IP address" );
      }
    }
    else
    {
      shell.addToOutput( "Invalid IP address" );
    }
  }
}
