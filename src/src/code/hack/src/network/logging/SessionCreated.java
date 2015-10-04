package code.hack.src.network.logging;

import code.hack.src.network.manager.Session;

/**
 * Created by Lasen on 02/10/2015.
 * Log object for notifying a session has been created
 */
public class SessionCreated implements Log
{
  private final Session session;

  public SessionCreated( final Session session )
  {
    this.session = session;
  }

  @Override
  public String getLogMessage()
  {
    return "New session created with " + session.getRequestingIp();
  }
}
