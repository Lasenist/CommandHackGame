package code.hack.src.network.server.ui;

import code.hack.src.network.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Lasen on 12/09/2015.
 * The login screen for servers
 */
public class LoginScreen extends JFrame
{
  /*
  * V A R I A B L E S
  */
  final Server computer;
  final Timer timer = new Timer( 1600, new TimerActionlistener() );
  final LoginActionListener loginActionListener = new LoginActionListener();

  /*
   * I M A G E S
   */
  final private ImageIcon blankIcon = new ImageIcon( getClass().getResource( "/res/blank16.png" ) );
  final private ImageIcon crossIcon = new ImageIcon( getClass().getResource( "/res/cross.png" ) );
  final private ImageIcon tickIcon = new ImageIcon( getClass().getResource( "/res/tick.png" ) );
  final private ImageIcon loadingGif = new ImageIcon( getClass().getResource( "/res/loading.gif" ) );
  final private ImageIcon blankLoadingGif = new ImageIcon( getClass().getResource( "/res/blank128.png" ) );

  /*
   * J C O M P O N E N T S
   */
  private JLabel loadingAnimation = new JLabel();
  private JTextField userField = new JTextField( 12 );
  private JPasswordField passwordField = new JPasswordField( 12 );
  private JLabel userIcon = new JLabel();
  private JLabel passIcon = new JLabel();

  /*
  * C O N S T R U C T O R
  */
  public LoginScreen( final Server server )
  {
    super( server.getIp() + " - Login Screen" );
    this.computer = server;
    init();
  }

  /*
  * G E T T E R S
  */
  public LoginActionListener getLoginActionListener()
  {
    return loginActionListener;
  }

  /*
  * M E T H O D S
  */
  private void init()
  {
    final JPanel panel = new JPanel( new GridBagLayout() );
    final JPanel animationPanel = new JPanel();
    final JPanel loginPanel = new JPanel( new GridBagLayout() );

    final JButton loginButton = new JButton( "Login" );
    loginButton.addActionListener( loginActionListener );

    //Alter the timer
    timer.setInitialDelay( 0 );

    // Default the icons
    loadingAnimation.setIcon( blankLoadingGif );
    userIcon.setIcon( blankIcon );
    passIcon.setIcon( blankIcon );

    // Add loading animation to the panel
    animationPanel.add( loadingAnimation, getConstraints( 0, 0, 0, 2, GridBagConstraints.PAGE_START, 0 ) );

    // Add appropriate fields to the login panel
    loginPanel.add( new JLabel( "Username:" ), getConstraints( 0, 1, 0 ) );
    loginPanel.add( new JLabel( "Password:" ), getConstraints( 0, 2 ) );
    loginPanel.add( userField, getConstraints( 1, 1, 5 ) );
    loginPanel.add( passwordField, getConstraints( 1, 2, 5 ) );
    loginPanel.add( userIcon, getConstraints( 2, 1, 5 ) );
    loginPanel.add( passIcon, getConstraints( 2, 2, 5 ) );
    loginPanel.add( loginButton, getConstraints( 1, 3, 0, 1, GridBagConstraints.PAGE_END, 0 ) );

    // Set frame parameters
    setSize( 800, 600 );
    setVisible( true );
    requestFocus();

    // Add panels for animation and logging in
    panel.add( animationPanel, getConstraints( 0, 0, 0, 1, GridBagConstraints.PAGE_START, 250 ) );
    panel.add( loginPanel, getConstraints( 0, 0, 0, 1, GridBagConstraints.PAGE_END, 0 ) );
    add( panel );
  }

  private GridBagConstraints getConstraints( final int x, final int y )
  {
    return getConstraints( x, y, 0, 1, GridBagConstraints.PAGE_END, 0 );
  }

  private GridBagConstraints getConstraints( final int x, final int y, final int leftPad )
  {
    return getConstraints( x, y, leftPad, 1, GridBagConstraints.PAGE_END, 0 );
  }

  private GridBagConstraints getConstraints( final int x, final int y, final int leftPad, final int gridWidth )
  {
    return getConstraints( x, y, leftPad, gridWidth, GridBagConstraints.PAGE_END, 0 );
  }

  private GridBagConstraints getConstraints( final int x, final int y, final int leftPad, final int gridWidth, final
  int anchor, final int insetBottom )
  {
    GridBagConstraints g = new GridBagConstraints();
    g.gridx = x;
    g.gridy = y;
    g.gridwidth = gridWidth;
    g.insets.left = leftPad;
    g.insets.top = 5;
    g.insets.bottom = insetBottom;
    g.anchor = anchor;
    return g;
  }

  public boolean isValidLogin()
  {
//    boolean isUserValid = userField.getText().equals( admin.getUsername() );
//    boolean isPassValid = Arrays.equals( passwordField.getPassword(), admin.getPassword().toCharArray() );

//    UIUtil.iconSwitch( userIcon, isUserValid ? tickIcon : crossIcon );
//    UIUtil.iconSwitch( passIcon, isPassValid ? tickIcon : crossIcon );

//    return isUserValid && isPassValid;
    return false;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // N E S T E D  C L A S S
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class LoginActionListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      trigger();
    }

    public void trigger()
    {
      timer.start();
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // N E S T E D  C L A S S
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  private class TimerActionlistener implements ActionListener
  {
    int pass = 0;

    public void actionPerformed( ActionEvent e )
    {
      switch ( pass )
      {
        case 0:
          loadingAnimation.setIcon( loadingGif );
          userIcon.setIcon( blankIcon );
          passIcon.setIcon( blankIcon );
          repaint();
          pass++;
          break;

        case 1:
          isValidLogin();
          loadingAnimation.setIcon( blankLoadingGif );
          repaint();
          pass = 0;
          timer.stop();
          break;
      }
    }
  }
}
