package code.hack.src.network.server.handlers;

import code.hack.src.network.connection.Session;
import code.hack.src.network.server.Server;

/**
 * Created by Lasen on 28/10/15.
 * Abstract class to hold session and server details, as all handlers should have these.
 */
public abstract class CommandHandler
{
  /*
  * V A R I A B L E S
  */
  protected Session session;
  protected Server server;

  /*
  * C O N S T R U C T O R
  */
  public CommandHandler( final Session session )
  {
    this.session=session;
    this.server=session.getRequestedServer();
  }

  /*
  * G E T T E R S
  */
  public Session getSession()
  {
    return session;
  }
}
