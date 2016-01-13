package code.hack.src.application.programs.serverindex;

import code.hack.src.application.Application;
import code.hack.src.main.PlayerServer;
import code.hack.src.network.server.Server;
import code.hack.src.util.AccountUtil;
import lib.cliche.src.Shell;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Lasen on 12/01/16.
 * List of servers
 */
public class ServerIndex extends Application
{
  final private static String VERSION = "V1.0";
  final private static int FILE_SIZE = 512;
  final private static int RAM_SIZE = 128;

  final PlayerServer playerServer;

  final ArrayList<Server> serverIndex;

  public ServerIndex( final Shell shell, final PlayerServer playerServer )
  {
    super( "Server Index", AccountUtil.SECURITECH, VERSION, FILE_SIZE, RAM_SIZE, shell );
    this.playerServer = playerServer;
    serverIndex = new ArrayList<>();
  }

  public void launch()
  {
    final ServerIndexTableModel tableModel = new ServerIndexTableModel( playerServer );
    final JTable table = new JTable( tableModel );
    final JScrollPane scroll = new JScrollPane( table );
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
    table.setAutoCreateColumnsFromModel( false );

    final TableColumn closeColumn = table.getColumn( "Close" );
    closeColumn.setCellRenderer( new ButtonRenderer( "X" ) );
    closeColumn.setCellEditor( new CloseButtonEditor( tableModel ) );
    table.getColumnModel().getColumn( 0 ).setPreferredWidth( 5 );

    final TableColumn ipColumn = table.getColumn( "IP" );
    ipColumn.setCellRenderer( centerRenderer );
    table.getColumnModel().getColumn( 1 ).setPreferredWidth( 30 );

    table.getColumnModel().getColumn( 2 ).setPreferredWidth( 250 );

    final TableColumn connectColumn = table.getColumn( "Connect" );
    connectColumn.setCellRenderer( new ButtonRenderer( "..." ) );
    connectColumn.setCellEditor( new ConnectButtonEditor( tableModel ) );
    table.getColumnModel().getColumn(3).setPreferredWidth( 5 );

    frame.getContentPane().add( scroll );
    frame.setPreferredSize( new Dimension( 500, 300 ) );

    super.launch();
  }
}
