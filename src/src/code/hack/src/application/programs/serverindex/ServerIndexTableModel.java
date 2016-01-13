package code.hack.src.application.programs.serverindex;

import code.hack.src.main.PlayerServer;
import code.hack.src.network.server.Server;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Created by Lasen on 12/01/16.
 * Table model for the Server Index application
 */
public class ServerIndexTableModel extends DefaultTableModel
{
  private PlayerServer playerServer;

  final String closeButton = "Close";
  final String connectButton = "Connect";

  public ServerIndexTableModel( final PlayerServer playerServer )
  {
    super();
    this.playerServer = playerServer;
    calculateData();
  }

  public PlayerServer getPlayerServer()
  {
    return playerServer;
  }

  public void updateTable()
  {
    calculateData();
    fireTableDataChanged();
  }

  private void calculateData()
  {
    final Object[] columnData = new Object[]{ "Close", "IP", "Description", "Connect" };

    final ArrayList<Server> servers = new ArrayList<>( playerServer.getServerIndex().values() );
    final Object[][] tableData = new Object[ servers.size() ] [ 4 ];

    for( final Server server : servers )
    {
      tableData[ servers.indexOf( server ) ][ 0 ] = closeButton;
      tableData[ servers.indexOf( server ) ][ 1 ] = server.getIp();
      tableData[ servers.indexOf( server ) ][ 2 ] = server.getDescription();
      tableData[ servers.indexOf( server ) ][ 3 ] = connectButton;
    }
    setDataVector( tableData, columnData );
  }


  public boolean isCellEditable( int row, int column )
  {
    return column == 0 || column == 3;
  }
}
