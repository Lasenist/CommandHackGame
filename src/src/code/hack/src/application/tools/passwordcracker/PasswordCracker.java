package code.hack.src.application.tools.passwordcracker;

import code.hack.src.application.tools.HackingApplication;
import code.hack.src.network.server.handlers.NoAccountHandler;
import lib.cliche.src.CLIException;
import lib.cliche.src.Shell;

import java.awt.*;

/**
 * Created by Lasen on 24/12/15.
 * Hacking application for the login command.
 */
public class PasswordCracker extends HackingApplication<NoAccountHandler>
{
  /*
  * V A R I A B L E S
  */
  private CrackerPanel panel = new CrackerPanel( this, handler );

  /*
  * C O N S T R U C T O R
  */
  public PasswordCracker( final Shell shell )
  {
    super( "Password Cracker", "1.0", 512, 128, shell );
  }

  /*
  * M E T H O D S
  */
  public void launch()
  {
    frame.setPreferredSize( new Dimension( 350, 200 ) );
    frame.add( panel );
    panel.updatePanel();
    shell.addHandlerListener( this );
    checkForHandler();
    super.launch();
  }

  public void close() throws CLIException
  {
    shell.removeHandlerListener( this );
    super.close();
  }

  private void checkForHandler()
  {
    for ( final Object o : shell.getHandlers() )
    {
      if ( isInstanceOfHandler( o ) )
      {
        setHandler( (NoAccountHandler) o );
      }
    }
  }

  public void handlerAdded( final Object handler )
  {
    if ( isInstanceOfHandler( handler ) )
    {
      setHandler( (NoAccountHandler) handler );
    }
  }

  public void handlerRemoved( final Object handler )
  {
    if ( isInstanceOfHandler( handler ) )
    {
      this.handler = null;
    }
  }

  public void setHandler( NoAccountHandler noAccountHandler )
  {
    super.setHandler( noAccountHandler );
    panel.setHandler( noAccountHandler );
    repaint();
  }

  public boolean isInstanceOfHandler( final Object o )
  {
    return o instanceof NoAccountHandler;
  }
}
