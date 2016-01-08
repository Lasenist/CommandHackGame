package code.hack.src.network.connection;

import code.hack.src.network.server.Server;
import code.hack.src.network.server.handlers.CommandHandler;
import code.hack.src.network.users.Account;
import code.hack.src.util.FileUtil;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Lasen on 30/09/2015.
 * The sessions facilitates communication between the Shell and a Server
 */
public class Session
{
  /*
  * V A R I A B L E S
  */
  private final String sessionId;
  private final String requestingIp;
  private final Server requestedServer;
  private ArrayList<Server> proxies;
  private Account account;
  ArrayList<CommandHandler> handlers = new ArrayList<>();
  private ArrayList<String> currentPath = new ArrayList<>();

  /*
  * C O N S T R U C T O R
  */
  public Session( final String requestingIp, final Server requestedServer )
  {
    this.requestingIp = requestingIp;
    this.requestedServer = requestedServer;
    final String ip = requestedServer.getIp();
    final String string = requestingIp + ip;
    sessionId = String.valueOf( string.hashCode() );
    currentPath.add( ip );
    proxies = new ArrayList<>();
  }

  /*
  * G E T T E R S
  */
  public ArrayList<CommandHandler> getHandlers()
  {
    return handlers;
  }

  public String getRequestingIp()
  {
    return requestingIp;
  }

  public String getSessionId()
  {
    return sessionId;
  }

  public Account getAccount()
  {
    return account;
  }

  public Server getRequestedServer()
  {
    return requestedServer;
  }

  public ArrayList<String> getCurrentPathAsArray()
  {
    return currentPath;
  }

  public String getCurrentPath()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( "/" );

    for ( int i = 0; i < currentPath.size(); i++ )
    {
      sb.append( currentPath.get( i ) );
      if ( i != currentPath.size() )
      {
        sb.append( "/" );
      }
    }
    return sb.toString();
  }

  public ArrayList getProxies()
  {
    return proxies;
  }

  /*
  * S E T T E R S
  */
  public void setAccount( final Account account )
  {
    this.account = account;
  }

  /*
  * M E T H O D S
  */
  public void addHandler( final CommandHandler handler )
  {
    handlers.add( handler );
  }

  public void addHandlers( final ArrayList<CommandHandler> handlers )
  {
    this.handlers.addAll( handlers );
  }

  public void clearCurrentPath()
  {
    currentPath = new ArrayList<>();
    currentPath.add( requestedServer.getIp() );
  }

  public void setCurrentPath( final String path )
  {
    final String[] folders = FileUtil.getFoldersInPath( path );
    currentPath = new ArrayList<>();
    Collections.addAll( currentPath, folders );
  }

  public void addToCurrentPath( final String folder )
  {
    currentPath.add( folder );
  }

  public void setProxyChain( final ArrayList<Server> proxies )
  {
    this.proxies = proxies;
  }
}
