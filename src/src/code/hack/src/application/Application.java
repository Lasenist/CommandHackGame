package code.hack.src.application;

import code.hack.src.Files.File;
import code.hack.src.network.users.Account;
import code.hack.src.util.Fn;
import lib.cliche.src.CLIException;
import lib.cliche.src.Shell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Created by Lasen on 02/12/15.
 * Base class for applications
 */
public abstract class Application extends File
{
  /*
  * V A R I A B L E S
  */
  final private String version;
  final private int ramSize;
  boolean running;

  final protected Shell shell;
  protected JFrame frame;
  protected JPanel panel;

  /*
  * C O N S T R U C T O R
  */
  public Application( final String name, final Account author, final String version, final int fileSize, final int
          ramSize, final Shell shell )
  {
    super( name, author, author, null, null ); //TODO: Possibly give dates?
    this.version = version;
    this.ramSize = ramSize;
    this.fileSize = fileSize;
    this.shell = shell;
  }

  /*
  * G E T T E R S
  */
  public String getName()
  {
    final String[] abbrevName = name.split( Fn.SPACE );
    final StringBuilder sb = new StringBuilder();
    for ( final String s : abbrevName )
    {
      sb.append( s );
    }
    return sb.toString();
  }

  public Shell getShell()
  {
    return shell;
  }

  public boolean isrunning()
  {
    return running;
  }

  /*
  * M E T H O D S
  */
  public void init()
  {
    shell.addMainHandler( this );
    frame = new JFrame( name + " - " + version );
    panel = new JPanel( new GridBagLayout() );
    frame.setAutoRequestFocus( false );
    frame.addWindowListener( new WindowAdapter()
    {
      public void windowClosing( final WindowEvent e )
      {
        close();
      }
    } );
    launch();
  }

  /**
   * Called when the application is launched.
   * Override to amend JFrame to how the application needs it.
   */
  public void launch()
  {
    frame.pack();
    frame.setVisible( true );
    running = true;
  }

  /**
   * Called when the user clicks the X on the window. Called from listener on the window, which is configured in
   * {@link #init()}
   * @throws CLIException
   */
  public void close()
  {
    frame.dispose();
    shell.removeHandler( this );
    running = false;
  }

  /**
   * Helpful repaint method to be used in child classes.
   */
  public void repaint()
  {
    frame.repaint();
  }
}
