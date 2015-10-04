package code.hack.src.network.logging;


/**
 * Created by Lasen on 02/10/2015.
 * An object for holding the type of log and the servers message.
 * This is so servers can interpret different log types differently for added customisation.
 */
public class StoredLog
{
  private final Log log;
  private final String message;

  public StoredLog( final Log log, final String message )
  {
    this.log = log;
    this.message = message;
  }

  public Log getLog()
  {
    return log;
  }

  public String getMessage()
  {
    return message;
  }
}
