package code.hack.src.util;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lasen on 14/09/2015.
 * Helper for ui related processes.
 */
public class UIUtil
{
  /*
  * V A R I A B L E S
  */
  public static final int LABEL_PADDING = 3;

  /*
  * M E T H O D S
  */
  public static void iconSwitch( final JLabel label, final ImageIcon icon )
  {
    label.setIcon( icon );
  }

  public static GridBagConstraints getConstraints( final int x, final int y )
  {
    return getConstraints( x, y, 0 );
  }

  public static GridBagConstraints getConstraints( final int x, final int y, final int ipadx )
  {
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.gridx = x;
    gbc.gridy = y;
    gbc.ipadx = ipadx;
    return gbc;
  }
}
