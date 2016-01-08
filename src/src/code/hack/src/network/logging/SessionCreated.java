package code.hack.src.network.logging;

import code.hack.src.network.connection.Session;

/**
 * Created by Lasen on 02/10/2015.
 * Log object for notifying a session has been created
 */
public class SessionCreated extends Log
{
  /*
  * C O N S T R U C T O R
  */
  public SessionCreated( final Session session )
  {
    super( session );
  }

  /*
  * M E T H O D S
  */
  public String getLogMessage()
  {
    return "New session created with " + session.getRequestingIp();
  }
}
