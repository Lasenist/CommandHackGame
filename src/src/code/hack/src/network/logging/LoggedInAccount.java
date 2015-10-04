package code.hack.src.network.logging;

import code.hack.src.network.Users.Account;
import code.hack.src.network.manager.Session;

/**
 * Created by Lasen on 02/10/2015.
 * A log object to show that an account has just been logged in by some user
 */
public class LoggedInAccount implements Log
{
  private Session session;
  private Account account;

  public LoggedInAccount( final Session session, final Account account )
  {
    this.session = session;
    this.account = account;
  }

  public String getLogMessage()
  {
    return "The session " + session.getSessionId() + "logged in with account " + account.getUsername();
  }
}
