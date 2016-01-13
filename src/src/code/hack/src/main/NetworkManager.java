package code.hack.src.main;

import code.hack.src.network.connection.Session;
import code.hack.src.network.server.Server;
import code.hack.src.util.NetworkUtil;
import lib.cliche.src.CLIException;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Lasen on 30/09/2015.
 * Manages access to servers and connections to servers
 */
public class NetworkManager
{
  /*
  * V A R I A B L E S
  */
  final HashMap<String, Server> servers = new HashMap<>();

  /*
  * C O N S T R U C T O R
  */
  public NetworkManager( final Server playerServer )
  {
    servers.put( playerServer.getIp(), playerServer );
    servers.put( "129.4.3.78", new Server( "129.4.3.78", true ) );
    servers.put( "126.1.56.201", new Server( "126.1.56.201", true ) );
    servers.put( "103.1.142.7", new Server( "103.1.142.7" ) );
  }

  /*
  * M E T H O D S
  */
  public Session createConnection( final Server requestingServer, final String requestedIp ) throws CLIException
  {
    Session session = null;
    if ( !Objects.equals( requestingServer.getIp(), requestedIp ) )
    {
      final Server server = getServer( requestedIp );
      if ( server != null )
      {
        session = server.connect( requestingServer );
      }
    }
    return session;
  }

  public Server getServer( final String ip ) throws CLIException
  {
    Server server = null;
    if ( NetworkUtil.isValidIp( ip ) )
    {
      server = servers.get( ip );
      if ( server == null )
      {
        throw CLIException.invalidServer( ip );
      }
    }
    return server;
  }

  public boolean isProxyEnabled( final String ip ) throws CLIException
  {
    return getServer( ip ).isProxyEnabled();
  }


}
