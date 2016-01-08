package code.hack.src.main;

import code.hack.src.network.connection.Session;
import code.hack.src.network.server.Server;
import code.hack.src.util.NetworkUtil;

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
  public NetworkManager()
  {
    servers.put( "129.4.3.78", new Server( "129.4.3.78", true ) );
    servers.put( "126.1.56.201", new Server( "126.1.56.201", true ) );
    servers.put( "103.1.142.7", new Server( "103.1.142.7" ) );
  }

  /*
  * M E T H O D S
  */
  public Session createConnection( final String requestingIp, final String requestedIp )
  {
    Session session = null;
    if ( NetworkUtil.isValidIp( requestedIp ) && !Objects.equals( requestingIp, requestedIp ) )
    {
      final Server server = servers.get( requestedIp );
      if ( server != null )
      {
        session = server.connect( requestingIp );
      }
    }
    return session;
  }

  public Server getServer( final String ip )
  {
    Server server = null;
    if ( NetworkUtil.isValidIp( ip ) )
    {
      server = servers.get( ip );
    }
    return server;
  }

  public boolean isProxyEnabled( final String ip )
  {
    return servers.get( ip ).isProxyEnabled();
  }


}
