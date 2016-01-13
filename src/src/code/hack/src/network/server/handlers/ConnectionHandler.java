package code.hack.src.network.server.handlers;

import code.hack.src.network.connection.Session;
import lib.cliche.src.CLIException;
import lib.cliche.src.Command;

/**
 * Created by Lasen on 10/01/16.
 * Handler for disconnecting from a server.
 */
public class ConnectionHandler extends CommandHandler
{
  public ConnectionHandler( Session session )
  {
    super( session );
  }

  @Command( description = "Attempt to disconnect to the current server" )
  public void disconnect() throws CLIException
  {
    session.getRequestingServer().endSession( session );
  }
}
