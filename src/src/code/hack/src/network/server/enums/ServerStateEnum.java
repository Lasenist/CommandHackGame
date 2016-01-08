package code.hack.src.network.server.enums;

/**
 * Created by Lasen on 23/11/15.
 * Overridable enum.
 */
public class ServerStateEnum
{
  /*
  * V A R I A B L E S
  */
  final String name;

  /*
  * C O N S T R U C T O R
  */
  public ServerStateEnum( final String name )
  {
    this.name=name;
  }

  public static final ServerStateEnum OFF = new ServerStateEnum( "OFF" );
  public static final ServerStateEnum BOOTING = new ServerStateEnum( "BOOTING" );
  public static final ServerStateEnum ON = new ServerStateEnum( "ON" );
  public static final ServerStateEnum CRASHED = new ServerStateEnum( "CRASHED" );
}
