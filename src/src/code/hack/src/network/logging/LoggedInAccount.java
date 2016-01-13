package code.hack.src.network.logging;

import code.hack.src.network.connection.Session;
import code.hack.src.network.users.Account;

/**
 * Created by Lasen on 02/10/2015.
 * A log object to show that an account has just been logged in by some user
 */
public class LoggedInAccount extends Log
{
  /*
  * V A R I A B L E S
  */
  private Account account;

  /*
  * C O N S T R U C T O R
  */
  public LoggedInAccount( final Session session, final Account account )
  {
    super( session );
    this.account = account;
  }

  /*
  * M E T H O D S
  */
  public String getLogMessage()
  {
    return "The session of " + session.getRequestingIp() + " logged in with account " + account.getUsername();
  }
}
