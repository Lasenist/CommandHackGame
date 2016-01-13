package code.hack.src.application.programs.connectionmanager;

import code.hack.src.main.NetworkManager;
import code.hack.src.util.Fn;
import code.hack.src.util.NetworkUtil;
import lib.cliche.src.CLIException;

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
      value = showInputDialog( parentComponent, "Enter IP to add", null, JOptionPane.QUESTION_MESSAGE );

      if ( value == null )
      {
        isCancelled = true;
      }

      try
      {
        if ( NetworkUtil.isValidIp( isCancelled ? Fn.EMPTY_STRING : value ) &&
                ( isCancelled || manager.isProxyEnabled( value ) ) || isCancelled )
        {
          isValid = true;
        }
        else
        {
          invalidIpAddress( parentComponent );
        }
      }
      catch ( CLIException e )
      {
        invalidIpAddress( parentComponent );
      }
    }
    return value;
  }

  private void invalidIpAddress( final Component parentComponent )
  {
    JOptionPane.showMessageDialog( parentComponent, "Invalid IP address", "Error", JOptionPane.ERROR_MESSAGE );
  }

}
