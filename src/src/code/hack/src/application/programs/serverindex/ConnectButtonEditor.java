package code.hack.src.application.programs.serverindex;

import code.hack.src.network.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Lasen on 12/01/16.
 * Editor for the connect button in ServerIndex
 */
class ConnectButtonEditor extends DefaultCellEditor
{
  protected JButton button;
  private boolean isPushed;
  private final ServerIndexTableModel tableModel;

  private int row;

  public ConnectButtonEditor( final ServerIndexTableModel tableModel )
  {
    super( new JCheckBox() );
    this.tableModel = tableModel;
    button = new JButton();
    button.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        fireEditingStopped();
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
    button.setText( "..." );
    isPushed = true;
    this.row = row;
    return button;
  }

  public Object getCellEditorValue()
  {
    if ( isPushed )
    {
      final ArrayList<Server> servers = new ArrayList<>( tableModel.getPlayerServer().getServerIndex().values() );
      try
      {
        tableModel.getPlayerServer().connect( servers.get( row ).getIp() );
      }
      catch ( Exception e ) { e.printStackTrace(); }
    }
    return null;
  }

  public boolean stopCellEditing()
  {
    isPushed = false;
    row = 0;
    return super.stopCellEditing();
  }
}
