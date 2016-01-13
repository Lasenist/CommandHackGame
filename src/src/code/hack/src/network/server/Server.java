package code.hack.src.network.server;

import code.hack.src.Files.BaseFolder;
import code.hack.src.Files.File;
import code.hack.src.Files.FolderFile;
import code.hack.src.Files.exceptions.DuplicateFileException;
import code.hack.src.network.connection.Session;
import code.hack.src.network.logging.*;
import code.hack.src.network.server.enums.ServerStateEnum;
import code.hack.src.network.server.handlers.CommandHandler;
import code.hack.src.network.server.handlers.FileManagementHandler;
import code.hack.src.network.server.handlers.NoAccountHandler;
import code.hack.src.network.users.Account;
import code.hack.src.util.FileUtil;
import code.hack.src.util.Fn;
import lib.cliche.src.Response;

import javax.swing.*;
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
  protected String description;
  protected boolean online;
  protected ServerStateEnum serverState;
  protected FolderFile logs;
  protected ArrayList<Account> accounts = new ArrayList<>();
  protected ArrayList<Session> sessions = new ArrayList<>();
  protected BaseFolder baseFolder = new BaseFolder( "C:" );
  protected boolean proxyEnabled;

  protected final Account autoLogger = new Account( "autoLogger" );

  /*
  * C O N S T R U C T O R
  */
  public Server( final String ip, final String description, final boolean proxyEnabled )
  {
    this.ip = ip;
    this.description = description;
    this.proxyEnabled = proxyEnabled;
    final Account admin = new Account( "admin", "password" );
    accounts.add( admin );
    logs = new FolderFile( "logs", admin, admin, new Date(), new Date() );
    try
    {
      baseFolder.addFolder( logs );
    }
    catch ( DuplicateFileException e )
    {
      e.printStackTrace();
    }
  }

  public Server( final String ip, final boolean proxyEnabled )
  {
    this( ip, Fn.EMPTY_STRING, proxyEnabled );
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

  public BaseFolder getBaseFolder()
  {
    return baseFolder;
  }

  public String getDescription()
  {
    return description;
  }

  /*
     * M E T H O D S
     */
  public Session connect( final Server requestingServer )
  {
    final Session newSession = new Session( requestingServer, this );
    newSession.addHandler( new NoAccountHandler( newSession ) );
    addSession( newSession );
    requestingServer.addSession( newSession );
    return newSession;
  }

  public void disconnect( final Session session, final boolean forced )
  {
    removeSession( session, forced );
  }

  private void addSession( final Session session )
  {
    addLog( new SessionCreated( session ) );
    sessions.add( session );
  }

  private void removeSession( final Session session, final boolean forced )
  {
    sessions.remove( session );
    addLog( new EndedSession( session, forced ) );
  }

  private void addLog( final Log log )
  {
    final String logPrefix = "[ " + new SimpleDateFormat( "HH:mm" ).format( new Date() ) + " ]: ";
    logs.addLog( new StoredLog( log, logPrefix + log.getLogMessage(), autoLogger ) );
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
    for ( File file : logs.getContents() )
    {
      if ( file instanceof StoredLog )
      {
        final StoredLog log = (StoredLog) file;
        if ( log.getLog() instanceof LoginAttempt )
        {
          final LoginAttempt loginAttempt = (LoginAttempt) log.getLog();

          if ( ! loginAttempt.isSuccessful() && loginAttempt.getSession() == session )
          {
            count++;
          }
        }

      }

    }
    return count >= maxAttempts;
  }

  private FolderFile getFolder( final String path )
  {
    return FileUtil.getFolderFromPath( baseFolder, path );
  }

  public boolean hasAccounts()
  {
    return accounts.size() != 0 || accounts != null;
  }

  public void traceSession( final Session session )
  {
    final ActiveTracerActionListener tracerActionListener = new ActiveTracerActionListener( session );
    final Timer traceTimer = new Timer( session.getProxies().size() * 10000, tracerActionListener );
    tracerActionListener.setTimer( traceTimer );
    traceTimer.start();
  }

  public boolean hasSession( final Session session )
  {
    return sessions.contains( session );
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
        session.addToCurrentPath( getBaseFolder().getName() );
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
    final ArrayList<String> output = new ArrayList<>();

    final FolderFile files = FileUtil.getFolderFromPath( baseFolder, session.getCurrentPath() );

    if ( files != null )
    {
      for (  File file : files.getContents() )
      {
        if ( file instanceof FolderFile )
        {
          output.add( " [FOLDER]    " + file.getName() );
        }
        else
        {
          output.add( "    [LOG]   " + file.getName() );
        }

      }
    }
    else
    {
      output.add( "-- Empty Folder --" );
    }

    response.setMessage( output );
    return response;
  }

  //Used by the disconnect command
  public void endSession( final Session session )
  {
    session.getRequestingServer().disconnect( session, false );
    session.getRequestedServer().disconnect( session, false );
  }
}
