package code.hack.src.application.programs.serverindex;

import code.hack.src.network.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Lasen on 12/01/16.
 * Editor for the close button in Server Index
 */
class CloseButtonEditor extends DefaultCellEditor
{
  protected JButton button;
  protected final ServerIndexTableModel tableModel;
  private boolean isPushed;

  private int row;

  public CloseButtonEditor( final ServerIndexTableModel tableModel )
  {
    super( new JCheckBox() );
    this.tableModel = tableModel;
    button = new JButton();
    button.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        try
        {
          fireEditingStopped();
        }
        catch( ArrayIndexOutOfBoundsException ex )
        {
          //TODO: Possibly stop it throwing errors by changing something?
        }
      }
    } );
  }

  public Component getTableCellEditorComponent( JTable table, Object value,
          boolean isSelected, int row, int column )
  {
    if ( isSelected )
    {
      button.setForeground( table.getSelectionForeground() );
      button.setBackground( table.getSelectionBackground() );
    }
    else
    {
      button.setForeground( table.getForeground() );
      button.setBackground( table.getBackground() );
    }
//    button.setText( "X" );
    isPushed = true;
    this.row=row;
    return button;
  }

  public Object getCellEditorValue()
  {
    if ( isPushed )
    {
      final ArrayList<Server> servers = new ArrayList<>( tableModel.getPlayerServer().getServerIndex().values() );
      tableModel.getPlayerServer().getServerIndex().remove( servers.get( row ).getIp() );
    }
    tableModel.updateTable();
    return null;
  }

  public boolean stopCellEditing()
  {
    isPushed = false;
    row=0;
    return super.stopCellEditing();
  }
}
