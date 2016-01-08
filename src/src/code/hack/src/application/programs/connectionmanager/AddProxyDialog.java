package code.hack.src.application.programs.connectionmanager;

import code.hack.src.main.NetworkManager;
import code.hack.src.util.Fn;
import code.hack.src.util.NetworkUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lasen on 23/12/15.
 * Dialog box for ConnectionManager
 */
public class AddProxyDialog extends JOptionPane
{
  /*
  * V A R I A B L E S
  */
  final NetworkManager manager;

  /*
  * C O N S T R U C T O R
  */
  public AddProxyDialog( final NetworkManager manager )
  {
    this.manager = manager;
  }

  /*
  * M E T H O D S
  */
  public String showInputDialog( final Component parentComponent )
  {
    String value = null;
    boolean isValid = false;
    boolean isCancelled = false;
    while ( !isValid )
    {
      value = showInputDialog( parentComponent, "Enter ip to add", null, JOptionPane.QUESTION_MESSAGE );

      if ( value == null )
      {
        isCancelled = true;
      }

      if ( NetworkUtil.isValidIp( isCancelled ? Fn.EMPTY_STRING : value ) && ( isCancelled || manager
              .isProxyEnabled( value ) ) || isCancelled )
      {
        isValid = true;
      }
      else
      {
        JOptionPane.showMessageDialog( parentComponent, "Invalid proxy address", "Error", JOptionPane.ERROR_MESSAGE );
      }
    }
    return value;
  }
}
