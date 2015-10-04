package code.hack.src.network.common.handlers;

import code.hack.src.network.common.Server;
import code.hack.src.network.manager.Session;
import lib.cliche.src.Command;
import lib.cliche.src.Response;

/**
 * Created by Lasen on 02/10/2015.
 * Handler handed to the session when no account has been logged into on a server.
 */
public class NoAccountHandler
{
  /*
  * V A R I A B L E S
  */
  private String username;
  private String password;

  private Session session;
  private Server server;

  /*
  * C O N S T R U C T O R
  */
  public NoAccountHandler( final Session session )
  {
    this.session = session;
    server = session.getRequestedServer();
  }

  /*
  * C O M M A N D S
  */
  @Command( description = "Attempt to login to the server" )
  public Response login()
  {
    Response response = new Response( Response.FINISHED );

    if ( username == null && password == null )
    {
      response.setResponse( Response.REQUEST_INPUT );
      response.addToSetters( "setUsername", "Username:" );
      response.addToSetters( "setPassword", "Password:" );
    }
    else
    {
      response = server.login( session, username, password );
      reset();
    }
    return response;
  }

  private void reset()
  {
    username = null;
    password = null;
  }

  /*
  * S E T T E R S
  */
  @Command( visible = false )
  public void setUsername( final String username )
  {
    this.username = username;
  }

  @Command( visible = false )
  public void setPassword( final String password )
  {
    this.password = password;
  }

}
