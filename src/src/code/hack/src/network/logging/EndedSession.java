package code.hack.src.network.logging;

import code.hack.src.network.connection.Session;

/**
 * Created by Lasen on 05/12/15.
 * Log for ending a session.
 */
public class EndedSession extends Log
{
  public EndedSession( Session session )
  {
    super( session );
  }

  public String getLogMessage()
  {
    return "Session ended with " + session.getRequestingIp();
  }
}
