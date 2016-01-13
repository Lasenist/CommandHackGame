package code.hack.src.application.tools;

import code.hack.src.application.Application;
import code.hack.src.listeners.HandlerListener;
import code.hack.src.network.users.Account;
import lib.cliche.src.Shell;

/**
 * Created by Lasen on 02/12/15.
 * Base class for hacking applications
 */
public abstract class HackingApplication<HANDLER> extends Application implements HandlerListener
{
  protected HANDLER handler;

  public HackingApplication( final String name, final String version, final int fileSize, final int ramSize, final
            Shell shell )
  {
    super( name, new Account( "Securitech" ),version, fileSize, ramSize, shell );
  }

  @Override
  public void close()
  {
    super.close();
    setHandler( null );
  }

  public void setHandler( final HANDLER handler )
  {
    this.handler = handler;
  }

  protected abstract boolean isInstanceOfHandler( final Object o );
}
