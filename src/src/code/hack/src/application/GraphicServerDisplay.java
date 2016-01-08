package code.hack.src.application;

import lib.cliche.src.Shell;

import javax.swing.*;

/**
 * Created by Lasen on 21/11/15.
 * Application to graphically show information about a server
 */
public class GraphicServerDisplay extends JFrame
{
  private final Shell shell;

  public GraphicServerDisplay( final Shell shell )
  {
    super( "Graphic Server Display v1.0" + " - " + shell.getSession().getRequestedServer().getIp() );
    this.shell = shell;
  }


  /**
   * Servers will give what information the user can currently view. This application will be in charge of what that
   * information looks like. This is for servers that do not inherently have its own graphics display. this is just a
   * supplement to make it easier for players.
   *
   * TODO: Implementation
   */

}
