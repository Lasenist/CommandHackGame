package code.hack.src.network.logging;

import code.hack.src.network.manager.Session;

/**
 * Created by Lasen on 02/10/2015.
 * An object to keep track of login attempts from users
 */
public class LoginAttempt implements Log
{
  private Session session;
  private boolean isSuccessful;

  public LoginAttempt( final Session session, final boolean isSuccessful )
  {
    this.session = session;
    this.isSuccessful = isSuccessful;
  }

  public Session getSession()
  {
    return session;
  }

  public boolean isSuccessful()
  {
    return isSuccessful;
  }

  public String getLogMessage()
  {
    final String outcome = isSuccessful ? "successful" : "unsuccessful";
    return "A login attempt from " + session.getRequestingIp() + " was " + outcome;
  }
}
