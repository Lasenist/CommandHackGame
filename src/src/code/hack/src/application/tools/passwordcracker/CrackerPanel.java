package code.hack.src.application.tools.passwordcracker;

import code.hack.src.network.connection.Session;
import code.hack.src.network.server.handlers.NoAccountHandler;
import code.hack.src.network.users.Account;
import code.hack.src.util.Fn;
import code.hack.src.util.UIUtil;
import lib.cliche.src.Shell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by Lasen on 04/01/16.
 * Panel used in PasswordCracker application.
 */
public class CrackerPanel extends JPanel
{
  /*
  * V A R I A B L E S
  */
  private PasswordCracker passwordCracker;
  private NoAccountHandler handler;
  private JLabel noHandler;
  private JLabel usernameLabel;
  private JLabel passwordLabel;
  private JTextField usernameField;
  private JTextField passwordField;
  private JButton passwordHack;
  private final Timer timer = new Timer( 100, new CrackerTimerActionListener() );

  /*
  * C O N S T R U C T O R
  */
  public CrackerPanel( final PasswordCracker passwordCracker, final NoAccountHandler handler )
  {
    super( new GridBagLayout() );
    this.passwordCracker = passwordCracker;
    this.handler = handler;

    noHandler = new JLabel( "No login function available" );
    usernameLabel = new JLabel( "Username:" );
    passwordLabel = new JLabel( "Password:" );
    usernameField = new JTextField();
    passwordField = new JTextField();
    passwordHack = new JButton( "Hack a Password" );

    usernameField.setColumns( 15 );
    passwordField.setColumns( 15 );

    passwordField.setEditable( false );

    setupButtonAction();

    add( usernameLabel, getLabelConstraints( 0, 0 ) );
    add( usernameField, getFieldConstraints( 1, 0 ) );
    add( passwordLabel, getLabelConstraints( 0, 1 ) );
    add( passwordField, getFieldConstraints( 1, 1 ) );
    add( passwordHack, getButtonConstraints( 1, 2 ) );

    add( noHandler, UIUtil.getConstraints( 0, 0 ) );
  }

  /*
  * S E T T E R S
  */
  public void setHandler( final NoAccountHandler handler )
  {
    this.handler=handler;
    updatePanel();
  }

  /*
  * M E T H O D S
  */
  private void setupButtonAction()
  {
    passwordHack.addActionListener( e ->
    {
      if ( usernameField.getText().equals( Fn.EMPTY_STRING ) )
      {
        usernameField.setText( "admin" );
      }
      timer.setInitialDelay( 0 );

      final Session session = handler.getSession();
      final Account account = session.getRequestedServer().getAccount( usernameField.getText() );
      if ( account != null )
      {
        timer.start();

      }
    } );
  }

  public void updatePanel()
  {
    if ( handler == null )
    {
      usernameLabel.setVisible( false );
      passwordLabel.setVisible( false );
      usernameField.setVisible( false );
      passwordField.setVisible( false );
      passwordHack.setVisible( false );

      noHandler.setVisible( true );
    }
    else
    {
      noHandler.setVisible( false );

      usernameLabel.setVisible( true );
      passwordLabel.setVisible( true );
      usernameField.setVisible( true );
      passwordField.setVisible( true );
      passwordHack.setVisible( true );
    }
  }

  private GridBagConstraints getLabelConstraints( final int x, final int y )
  {
    final GridBagConstraints g = UIUtil.getConstraints( x, y, UIUtil.LABEL_PADDING );
    g.insets.left=5;
    g.insets.right=5;
    g.insets.top=7;
    g.anchor=GridBagConstraints.EAST;
    return g;
  }

  private GridBagConstraints getFieldConstraints( final int x, final int y )
  {
    final GridBagConstraints g = UIUtil.getConstraints( x, y );
    g.insets.left=5;
    g.insets.right=5;
    g.insets.top=7;
    return g;
  }

  private GridBagConstraints getButtonConstraints( final int x, final int y )
  {
    final GridBagConstraints g = UIUtil.getConstraints( x, y );
    g.insets.left=7;
    g.insets.right=7;
    g.insets.top=10;
    return g;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // N E S T E D  C L A S S
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public class CrackerTimerActionListener implements ActionListener
  {
    /*
    * V A R I A B L E S
    */
    final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    int timePassed = 0;
    final private Random r = new Random();
    public String password;

    /*
    * M E T H O D S
    */
    public void actionPerformed( ActionEvent e )
    {
      final Session session = handler.getSession();
      final String text = usernameField.getText();
      if ( password == null )
      {
        password = session.getRequestedServer().getAccount( text ).getPassword();
      }
      final StringBuilder sb = new StringBuilder( password.length() );
      for ( int i = 0; i < password.length(); i++ )
      {
        sb.append( getRandomCharacter() );
      }

      if ( timePassed < 5000 )
      {
        passwordField.setText( sb.toString() );
        timePassed = timePassed + 100;
      }
      else
      {
        final Shell shell = passwordCracker.getShell();
        passwordField.setText( password );
        timer.stop();
        timePassed = 0;
        password = null;
        session.getRequestedServer().login( session, text, passwordField.getText() );
        shell.updateHandlersFromSession();
        shell.addToOutputWithPath( "\n" + "Account cracked and logged into." );
      }
    }

    private String getRandomCharacter()
    {
      return String.valueOf( alphabet.charAt( r.nextInt( alphabet.length() ) ) );
    }
  }

}
