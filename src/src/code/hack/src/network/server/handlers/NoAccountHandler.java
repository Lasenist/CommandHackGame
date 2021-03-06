package code.hack.src.network.server.handlers;

import code.hack.src.network.connection.Session;
import lib.cliche.src.Command;
import lib.cliche.src.Question;
import lib.cliche.src.Response;

import java.util.ArrayList;

/**
 * Created by Lasen on 02/10/2015.
 * Handler handed to the session when no account has been logged into on a server.
 */
public class NoAccountHandler extends CommandHandler
{
  public static final String LOGIN = "login";

  /*
  * V A R I A B L E S
  */
  private String username;
  private String password;

  /*
  * C O N S T R U C T O R
  */
  public NoAccountHandler( final Session session )
  {
    super( session );
  }

  /*
  * C O M M A N D S
  */
  @Command( description = "Attempt to login to the server" )
  public Response login()
  {
    Response response = new Response();

    if ( username == null && password == null )
    {
      response.setResponse( Response.REQUEST_INPUT );
      final ArrayList<Question> questions = new ArrayList<>();
      questions.add( new Question( "setUsername", "Username" ) );
      questions.add( new Question( "setPassword", "Password" ) );
      response.addToSetters( questions, "login" );
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
