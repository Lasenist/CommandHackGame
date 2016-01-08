package code.hack.src.application.programs.connectionmanager;

import code.hack.src.network.server.Server;
import code.hack.src.util.UIUtil;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;


/**
 * Created by Lasen on 18/12/15.
 * Panel used in the ConnectionManager application
 */
public class ProxyPanel extends JPanel
{
  /*
  * V A R I A B L E S
  */
  private Border proxyBorder =
          new CompoundBorder( new EmptyBorder( 2, 5, 2, 5 ), new BevelBorder( BevelBorder.RAISED, Color
                  .BLACK, Color.BLACK ) );
  private Border emptyBorder = new CompoundBorder( new EmptyBorder( 2, 5, 2, 5 ), new EtchedBorder( EtchedBorder
          .LOWERED ) );
  private JTextField proxyIp;
  private JButton addButton;
  private JButton removebutton;
  private Dimension dimension = new Dimension( 350, 100 );
  private Server server;

  /*
  * C O N S T R U C T O R
  */
  public ProxyPanel( final ConnectionManager connectionManager )
  {
    super( new GridBagLayout() );
    proxyIp = new JTextField();
    proxyIp.setEditable( false );
    addButton = new JButton( "Add Proxy" );
    addButton.addActionListener( e ->
    {
      final String value = new AddProxyDialog( connectionManager.getNetworkManager() ).showInputDialog( SwingUtilities.getRoot( this
      ) );

      if ( value != null )
      {
        try
        {
          connectionManager.addProxy( value );
        }
        catch ( Exception e1 )
        {
          //TODO:Error handling
          JOptionPane.showMessageDialog( SwingUtilities.getRoot( this ), "There was an error when adding the proxy. " +
                  "Something is wrong, so contact the developers." );
        }
      }

    } );

    removebutton = new JButton( "Remove" );
    removebutton.addActionListener( e ->
    {
      connectionManager.removeProxy( server.getIp() );
    } );

    setVisible( true );
    setBorder( emptyBorder );

    add( proxyIp, UIUtil.getConstraints( 0, 0, 60 ) );
    add( Box.createHorizontalGlue(), UIUtil.getConstraints( 1, 0, 100 ) );
    add( removebutton, UIUtil.getConstraints( 2, 0, 10 ) );

    add( addButton, UIUtil.getConstraints( 1, 1, 40 ) );
  }

  /*
  * G E T T E R S
  */
  public Dimension getMinimumSize()
  {
    return dimension;
  }

  public Dimension getPreferredSize()
  {
    return dimension;
  }

  /*
  * S E T T E R S
  */
  public void setServer( final Server server )
  {
    this.server = server;
  }

  /*
  * M E T H O D S
  */
  public void updatePanel( final Server server )
  {
    updatePanel( server, false );
  }

  public void updatePanel( final Server server, final boolean isNextProxy )
  {
    setServer( server );

    if ( server != null )
    {
      addButton.setVisible( false );

      setBorder( proxyBorder );
      proxyIp.setVisible( true );
      removebutton.setVisible( true );

      proxyIp.setText( server.getIp() );
    }
    else
    {
      setBorder( emptyBorder );
      addButton.setVisible( isNextProxy );
      removebutton.setVisible( false );
      proxyIp.setVisible( false );
    }
  }
}
