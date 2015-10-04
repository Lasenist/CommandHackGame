package code.hack.src.network.common;

import code.hack.src.network.Users.Account;
import code.hack.src.network.common.handlers.NoAccountHandler;
import code.hack.src.network.logging.*;
import code.hack.src.network.manager.Session;
import lib.cliche.src.Command;
import lib.cliche.src.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lasen on 23/09/2015.
 * A basic server to connect to.
 */
public class Server
{

  protected enum Permission
  {
    ADMIN
  }

  /*
  * V A R I A B L E S
  */
  protected String ip;
  protected ArrayList<StoredLog> logs = new ArrayList<>();
  protected ArrayList<Account> accounts = new ArrayList<>();
  protected ArrayList<Session> sessions = new ArrayList<>();

  /*
  * C O N S T R U C T O R
  */
  public Server( final String ip )
  {
    this.ip = ip;
    accounts.add( new Account( "admin", "password", Permission.ADMIN ) );
  }

  /*
  * G E T T E R S
  */
  public String getIp()
  {
    return ip;
  }

  /*
   * M E T H O D S
   */
  public Session connect( final String requestingIp )
  {
    final Session newSession = new Session( requestingIp, this );
    addSession( newSession );
    return newSession;
  }

  private void addSession( final Session session )
  {
    addLog( new SessionCreated( session ) );
    session.addHandler( getHandler( session ) );
    sessions.add( session );
  }

  private void addLog( final Log log )
  {
    final String logPrefix = "[ " + new SimpleDateFormat( "HH:mm" ).format( new Date() ) + " ]: ";
    logs.add( new StoredLog( log, logPrefix + log.getLogMessage() ) );
  }

  public Object getHandler( final Session session )
  {
    Object handler = null;
    if ( session.getAccount() == null )
    {
      handler = new NoAccountHandler( session );
    }
    else
    {
      //return everything else. Permission eligibility will be checked per invoke, for maximum cool points
    }
    return handler;
  }

  /**
   * Attempts to find an account with the username and password.
   *
   * @param username The username of the account
   * @param password The password of the account
   * @return The account found, or null if there isn't any.
   */
  private Account getAccount( final String username, final String password )
  {
    Account foundAccount = null;
    for ( Account account : accounts )
    {
      if ( account.getUsername().equals( username ) && account.getPassword().equals( password ) )
      {
        foundAccount = account;
        break;
      }
    }
    return foundAccount;
  }

  private boolean isLoginAttemptsReachedMax( final Session session )
  {
    final int maxAttempts = 3;
    int count = 0;
    for ( StoredLog log : logs )
    {
      if ( log.getLog() instanceof LoginAttempt )
      {
        final LoginAttempt loginAttempt = (LoginAttempt) log.getLog();

        if ( ! loginAttempt.isSuccessful() && loginAttempt.getSession() == session )
        {
          count++;
        }
      }
    }
    return count >= maxAttempts;
  }

  /*
  * C O M M A N D S
  */
  @Command( description = "Begin a login attempt" )
  public Response login( final Session session, final String username, final String password )
  {
    final Account account = getAccount( username, password );
    Response response = new Response( Response.FINISHED );
    boolean isSuccessful;

    if ( account != null )
    {
      isSuccessful = true;
      session.setAccount( account );
      addLog( new LoggedInAccount( session, account ) );
      response.setResponse( Response.UPDATE_SESSION_HANDLER );
      response.setMessage( "Logged in with account" + account.getUsername() );
    }
    else
    {
      isSuccessful = false;
      response.setMessage( "Invalid credentials" );
    }

    addLog( new LoginAttempt( session, isSuccessful ) );

    if ( ! isSuccessful && isLoginAttemptsReachedMax( session ) )
    {
      response.setMessage( "Too many invalid attempts. Please wait." );
    }

    return response;
  }
}
