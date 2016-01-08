package code.hack.src.application.programs.connectionmanager;

import code.hack.src.application.Application;
import code.hack.src.main.Launcher;
import code.hack.src.main.NetworkManager;
import code.hack.src.network.connection.Session;
import code.hack.src.network.server.Server;
import code.hack.src.network.server.handlers.NoAccountHandler;
import code.hack.src.util.CommandUtil;
import code.hack.src.util.NetworkUtil;
import lib.cliche.src.CLIException;
import lib.cliche.src.Command;
import lib.cliche.src.Response;
import lib.cliche.src.Shell;

import java.awt.*;
import java.util.ArrayList;


/**
 * Created by Lasen on 21/11/15.
 * Application for allowing the user to configure proxies to use in their next connection.
 */
public class ConnectionManager extends Application
{
  /*
  * V A R I A B L E S
  */
  final private static String NAME = "Connection Manager";
  final private static String VERSION = "1.0";
  final private static int FILE_SIZE = 512;
  final private static int RAM_SIZE = 128;
  final protected NetworkManager networkManager;
  protected String endCurrentSession;

  private ProxyPanel[]
          panels = new ProxyPanel[]
                  {
                          new ProxyPanel( this ),
                          new ProxyPanel( this ),
                          new ProxyPanel( this ),
                          new ProxyPanel( this ),
                          new ProxyPanel( this ),
                          new ProxyPanel( this ),
                          new ProxyPanel( this ),
                          new ProxyPanel( this )
                  };

  /*
  * C O N S T R U C T O R
  */
  public ConnectionManager( final Shell shell, final NetworkManager networkManager )
  {
    super( NAME, VERSION, FILE_SIZE, RAM_SIZE, shell );
    this.networkManager = networkManager;
  }

  /*
  * G E T T E R S
  */
  public NetworkManager getNetworkManager()
  {
    return networkManager;
  }

  /*
  * M E T H O D S
  */
  public void launch()
  {
    frame.setPreferredSize( new Dimension( 350, 820 ) );

    for ( int i = 0; i < panels.length; i++ )
    {
      panel.add( panels[i], getConstraints( i ) );
    }
    updateUI();
    super.launch();
  }

  public void close() throws CLIException
  {
    super.close();
    frame = null;
  }

  @Command( visible = false )
  public void setEndCurrentSession( final String answer )
  {
    endCurrentSession = answer;
  }

  @Command( name = "addProxy", abbrev="ap", description = "Add a proxy server to your connection chain" )
  public Response addProxy( final String ip ) throws Exception
  {
    Response response = new Response();

    final Session session = shell.getSession();
    if ( endCurrentSession == null )
    {
      if ( networkManager.isProxyEnabled( ip ) )
      {
        final Server server = networkManager.getServer( ip );

        if ( server.hasAccounts() && session != null && false ) //TODO: UI for this so setting to false for now
        {
          response.setResponse( Response.REQUEST_INPUT );
          response.addToSetters( "setEndCurrentSession", "Utilising proxy requires login details.\n" +
                  "End current session to input now? y/n" );
        }
        else
        {
          addProxy( server );
        }
      }
      else
      {
        shell.addToOutputWithPath( "\n" +
                "That server does not have proxy capabilities.\nContact your system admin if this is in " +
                        "error." );
      }
    }
    else
    {
      if ( CommandUtil.isYesOrNo( endCurrentSession ) )
      {
        if ( CommandUtil.isYes( endCurrentSession ) )
        {
          shell.clearSession();
          shell.setSession( networkManager.createConnection( Launcher.localhost, ip ) );
          shell.processLine( NoAccountHandler.LOGIN );

          if ( session.getAccount() != null )
          {
            addProxy( session.getRequestedServer() );
          }
        }
        else
        {
          response.setMessage( "Adding proxy action cancelled." );
        }
      }
      else
      {
        throw CLIException.invalidValue( "addProxy" );
      }
    }
    endCurrentSession = null;
    return response;
  }

  @Command( name = "removeProxy", abbrev="rp", description = "Remove a proxy server in your connection chain" )
  public void removeProxy( final String ip )
  {
    if ( NetworkUtil.isValidIp( ip ) )
    {
      for ( int i = 0; i < shell.getProxies().size(); i++ )
      {
        ArrayList<Server> proxies = shell.getProxies();
        if ( proxies.get( i ).getIp().equals( ip ) )
        {
          proxies.remove( i );
          shell.addToOutputWithPath( "\n" +  "Successfully removed proxy " + ip + " from chain" );
        }
      }
    }
    else
    {
      //TODO:Error Handling
      shell.addToOutputWithPath( "\n" +  "Invalid ip" );
    }
    updateUI();
  }

  private void addProxy( final Server server )
  {
    shell.addProxy( server );
    shell.addToOutputWithPath( "\n" + "Successfully added proxy " + server.getIp() + " to the chain." );
    updateUI();
  }

  private void updateUI()
  {
    final ArrayList<Server> proxies = shell.getProxies();
    boolean isFirstEmpty=true;
    for( int i = 0; i < 8; i++ )
    {
      if ( i < proxies.size() )
      {
        panels[i].updatePanel( proxies.get( i ) );
      }
      else
      {
        panels[i].updatePanel( null, isFirstEmpty );
        isFirstEmpty = false;
      }
    }
    repaint();
  }

  private GridBagConstraints getConstraints( final int y )
  {
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor=GridBagConstraints.CENTER;
    gbc.fill=GridBagConstraints.BOTH;
    gbc.weightx = 0.1;
    gbc.weighty = 0.1;
    gbc.gridx=0;
    return gbc;
  }
}
