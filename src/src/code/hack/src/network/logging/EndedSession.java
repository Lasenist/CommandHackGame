package code.hack.src.network.logging;

import code.hack.src.network.connection.Session;
import code.hack.src.util.Fn;

/**
 * Created by Lasen on 05/12/15.
 * Log for ending a session.
 */
public class EndedSession extends Log
{
  final private boolean forced;

  public EndedSession( final Session session, final boolean forced )
  {
    super( session );
    this.forced = forced;
  }

  public String getLogMessage()
  {
    final String type = forced ? " forcibly" : Fn.EMPTY_STRING;
    return "Session" + type + " ended with " + session.getRequestingIp();
  }
}
