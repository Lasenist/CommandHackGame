package code.hack.src.network.server;

import code.hack.src.network.connection.Session;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Lasen on 11/01/16.
 * Triggers after a the timer is fired from a Server
 */
public class ActiveTracerActionListener implements ActionListener
{
  final private Session session;
  private Timer timer;

  public ActiveTracerActionListener( final Session session )
  {
    this.session = session;
  }

  public void setTimer( final Timer timer )
  {
    this.timer = timer;
  }

  public void actionPerformed( ActionEvent e )
  {
    session.getRequestedServer().disconnect( session, true );
    session.getRequestingServer().disconnect( session, true );

    if ( timer != null )
    {
      timer.stop();
    }
  }
}
