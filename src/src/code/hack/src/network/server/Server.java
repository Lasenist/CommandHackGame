package code.hack.src.network.server;

import code.hack.src.Files.BaseFolder;
import code.hack.src.Files.File;
import code.hack.src.Files.FolderFile;
import code.hack.src.network.connection.Session;
import code.hack.src.network.logging.*;
import code.hack.src.network.server.enums.ServerStateEnum;
import code.hack.src.network.server.handlers.CommandHandler;
import code.hack.src.network.server.handlers.FileManagementHandler;
import code.hack.src.network.server.handlers.NoAccountHandler;
import code.hack.src.network.users.Account;
import code.hack.src.util.FileUtil;
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
  /*
  * V A R I A B L E S
  */
  protected String ip;
  protected boolean online;
  protected ServerStateEnum serverState;
  protected ArrayList<StoredLog> logs = new ArrayList<>();
  protected ArrayList<Account> accounts = new ArrayList<>();
  protected ArrayList<Session> sessions = new ArrayList<>();
  protected BaseFolder folders = new BaseFolder( "C" );
  protected boolean proxyEnabled;

  /*
  * C O N S T R U C T O R
  */
  public Server( final String ip, final boolean proxyEnabled )
  {
    this.ip = ip;
    this.proxyEnabled = proxyEnabled;
  }

  public Server( final String ip )
  {
    this( ip, false );
  }

  /*
  * G E T T E R S
  */
  public String getIp()
  {
    return ip;
  }

  public boolean isProxyEnabled()
  {
    return proxyEnabled;
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

  public void disconnect( final Session session )
  {
    removeSession( session );
  }

  private void addSession( final Session session )
  {
    addLog( new SessionCreated( session ) );
    session.addHandler( new NoAccountHandler( session ) );
    sessions.add( session );
  }

  private void removeSession( final Session session )
  {
    sessions.remove( session );
    addLog( new EndedSession( session ) );
  }

  private void addLog( final Log log )
  {
    final String logPrefix = "[ " + new SimpleDateFormat( "HH:mm" ).format( new Date() ) + " ]: ";
    logs.add( new StoredLog( log, logPrefix + log.getLogMessage() ) );
  }

  public ArrayList<CommandHandler> getHandlers( final Session session )
  {
    ArrayList<CommandHandler> handler = new ArrayList<>();
    if ( session.getAccount() == null )
    {
      handler.add( new NoAccountHandler( session ) );
    }
    else
    {
      handler.add( new FileManagementHandler( session ) );
      //return everything else. Permission eligibility will be checked per invoke, for maximum cool points
    }
    return handler;
  }

  /**
   * Attempts to find an account with the username and password.
   *
   * @param username The username of the account
   * @param password The password of the account
   * @param isAccountLookup method being called for validation, or purely a lookup for an account name
   * @return The account found, or null if there isn't any.
   */
  public Account getAccount( final String username, final String password, final boolean isAccountLookup )
  {
    Account foundAccount = null;
    for ( Account account : accounts )
    {
      if ( account.getUsername().equals( username ) && ( account.getPassword().equals( password ) || isAccountLookup ) )
      {
        foundAccount = account;
        break;
      }
    }
    return foundAccount;
  }

  public Account getAccount( final String username, final String password )
  {
    return getAccount( username, password, false );

  }

  public Account getAccount( final String username )
  {
    return getAccount( username, null, true );
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

  private FolderFile getFolder( final String path )
  {
    return FileUtil.getFolderFromPath( folders, path );
  }

  public boolean hasAccounts()
  {
    return accounts.size() != 0 || accounts != null;
  }

  public void traceSession( final Session session )
  {
    boolean tracking = true;
    while( sessions.contains( session ) && tracking )
    {
      try
      {
        Thread.sleep( session.getProxies().size() * 10000 );
        disconnect( session );
        tracking = false;
      }
      catch ( InterruptedException e )
      {
        e.printStackTrace();
      }
    }
  }

  /*
  * C O M M A N D S
  */
  public Response login( final Session session, final String username, final String password )
  {
    Response response = new Response();
    if ( session.getAccount() == null )
    {
      final Account account = getAccount( username, password );
      boolean isSuccessful;

      if ( account != null )
      {
        isSuccessful = true;
        session.setAccount( account );
        response.setResponse( Response.UPDATE );
        response.setMessage( "Logged in with account " + account.getUsername() );
        session.addHandler( new FileManagementHandler( session ) );
      }
      else
      {
        isSuccessful = false;
        response.setMessage( "Invalid credentials" );
      }

      addLog( new LoginAttempt( session, isSuccessful ) );

      if ( isSuccessful )
      {
        addLog( new LoggedInAccount( session, account ) );
      }

      if ( ! isSuccessful && isLoginAttemptsReachedMax( session ) )
      {
        response.setMessage( "Too many invalid attempts. Please wait." );
      }
    }
    else
    {
      response.setMessage( "You're already logged in" );
    }
    return response;
  }

  public Response cd( final Session session, final String path )
  {
    //TODO: Fix being able to back out of your ip address.
    final Response r = new Response();
    String newPath = FileUtil.getNewPath( session.getCurrentPath(), path );
    final FolderFile file = getFolder( newPath );

    if ( file != null )
    {
      session.setCurrentPath( newPath );
      r.setResponse( Response.UPDATE );
    }
    else
    {
      r.setMessage( "Invalid Path" );
    }
    return r;
  }

  public Response ls( final Session session )
  {
    Response response = new Response();
    StringBuilder sb = new StringBuilder();

    for (  File file : FileUtil.getFolderFromPath( folders, session.getCurrentPath() ).getContents() )
    {
      sb.append( file.getName() ).append( "\n" );
    }
    response.setMessage( sb.toString() );
    return response;
  }

}
