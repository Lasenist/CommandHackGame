package code.hack.src.network.common;

import code.hack.src.ui.LoginScreen;

/**
 * Created by Lasen on 31/08/2015.
 * A computer to connect to.
 */
public class Computer extends Server
{
  /*
  * V A R I A B L E S
  */
  private LoginScreen loginScreen;

  /*
   * C O N S T R U C T O R
   */
  public Computer( final String ip )
  {
    super( ip );
    loginScreen = new LoginScreen( this );
  }

  /*
   * G E T T E R S
   */
  public LoginScreen getLoginScreen()
  {
    return loginScreen;
  }


}
