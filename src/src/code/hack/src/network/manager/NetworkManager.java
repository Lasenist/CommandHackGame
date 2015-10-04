package code.hack.src.network.manager;

import code.hack.src.network.common.Server;

/**
 * Created by Lasen on 30/09/2015.
 * Manages access to servers and connections to servers
 */
public class NetworkManager
{
  public NetworkManager()
  {
  }

  public Session createConnection( final String requestingIp, final String requestedIp )
  {
    final Server server = new Server( requestedIp );
    return server.connect( requestingIp );
  }


}
