package code.hack.src.network.manager;

import code.hack.src.network.Users.Account;
import code.hack.src.network.common.Server;

import java.util.ArrayList;

/**
 * Created by Lasen on 30/09/2015.
 * The sessions facilitates communication between the Shell and a Server
 */
public class Session
{
  private final String sessionId;
  private final String requestingIp;
  private final Server requestedServer;
  private Account account;

  ArrayList<Object> handlers = new ArrayList<>();


  public Session( final String requestingIp, final Server requestedServer )
  {
    this.requestingIp = requestingIp;
    this.requestedServer = requestedServer;
    final String string = requestingIp + requestedServer.getIp();
    sessionId = String.valueOf( string.hashCode() );
  }

  /*
  * G E T T E R S
  */
  public ArrayList getHandlers()
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
  public void addHandler( final Object handler )
  {
    handlers.add( handler );
  }

  public void addHandlers( final ArrayList<Object> handlers )
  {
    this.handlers.add( handlers );
  }

  public void updateHandlers()
  {
    handlers.clear();
    addHandler( requestedServer.getHandler( this ) );
  }
}
