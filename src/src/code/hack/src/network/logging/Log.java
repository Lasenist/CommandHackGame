package code.hack.src.network.logging;

import code.hack.src.network.connection.Session;

/**
 * Created by Lasen on 02/10/2015.
 * An abstract class to make sure every Logging object has a message to give to the user.
 */
public abstract class Log
{
  /*
  * V A R I A B L E S
  */
  protected final Session session;

  /*
  * C O N S T R U C T O R
  */
  public Log( final Session session )
  {
    this.session=session;
  }

  /*
  * M E T H O D S
  */
  public abstract String getLogMessage();
}
