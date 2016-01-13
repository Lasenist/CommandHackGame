package code.hack.src.application.programs.serverindex;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by Lasen on 12/01/16.
 * Button rendered for the close column in the Server Index
 */
public class ButtonRenderer extends JButton implements TableCellRenderer
{
  public ButtonRenderer( final String value )
  {
    super( value );
  }


  public Component getTableCellRendererComponent( JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column )
  {
    if ( isSelected )
    {
      setForeground( table.getSelectionForeground() );
      setBackground( table.getSelectionBackground() );
    }
    else
    {
      setForeground( table.getForeground() );
      setBackground( UIManager.getColor( "Button.background" ) );
    }
    return this;
  }
}
