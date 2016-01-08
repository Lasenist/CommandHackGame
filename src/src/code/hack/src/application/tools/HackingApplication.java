package code.hack.src.application.tools;

import code.hack.src.application.Application;
import code.hack.src.listeners.HandlerListener;
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
    super( name, version, fileSize, ramSize, shell );
  }

  public void setHandler( final HANDLER handler )
  {
    this.handler = handler;
  }

  protected abstract boolean isInstanceOfHandler( final Object o );
}
