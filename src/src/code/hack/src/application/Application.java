package code.hack.src.application;

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
public abstract class Application
{
  /*
  * V A R I A B L E S
  */
  final private String name;
  final private String version;
  final private int fileSize;
  final private int ramSize;

  final protected Shell shell;
  protected JFrame frame;
  protected JPanel panel;

  /*
  * C O N S T R U C T O R
  */
  public Application( final String name, final String version, final int fileSize, final int ramSize, final Shell
          shell )
  {
    this.name = name;
    this.version = version;
    this.fileSize = fileSize;
    this.ramSize = ramSize;
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

  /*
  * M E T H O D S
  */
  public void init()
  {
    shell.addMainHandler( this );
    frame = new JFrame( name + " - " + version );
    panel = new JPanel( new GridBagLayout() );
    frame.add( panel );
    frame.addWindowListener( new WindowAdapter()
    {
      public void windowClosing( final WindowEvent e )
      {
        try
        {
          close();
        }
        catch ( CLIException e1 )
        {
          e1.printStackTrace();
        }
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
  }

  /**
   * Called when the user clicks the X on the window. Called from listener on the window, which is configured in
   * {@link #init()}
   * @throws CLIException
   */
  public void close() throws CLIException
  {
    frame.dispose();
    shell.removeHandler( this, "" );
  }

  /**
   * Helpful repaint method to be used in child classes.
   */
  public void repaint()
  {
    frame.repaint();
  }
}
