package code.hack.src.network.logging;

import code.hack.src.network.connection.Session;

/**
 * Created by Lasen on 02/10/2015.
 * An object to keep track of login attempts from users
 */
public class LoginAttempt extends Log
{
  /*
  * V A R I A B L E S
  */
  private boolean isSuccessful;

  /*
  * C O N S T R U C T O R
  */
  public LoginAttempt( final Session session, final boolean isSuccessful )
  {
    super( session );
    this.isSuccessful = isSuccessful;
  }

  /*
  * G E T T E R S
  */
  public Session getSession()
  {
    return session;
  }

  public boolean isSuccessful()
  {
    return isSuccessful;
  }

  /*
  * M E T H O D S
  */
  public String getLogMessage()
  {
    final String outcome = isSuccessful ? "successful" : "unsuccessful";
    return "A login attempt from " + session.getRequestingIp() + " was " + outcome;
  }
}
