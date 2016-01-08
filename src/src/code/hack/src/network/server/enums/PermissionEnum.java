package code.hack.src.network.server.enums;

/**
 * Created by Lasen on 23/11/15.
 * Overridable enum.
 */
public class PermissionEnum
{
  /*
  * V A R I A B L E S
  */
  final String name;

  /*
  * C O N S T R U C T O R
  */
  public PermissionEnum( final String name )
  {
    this.name = name;
  }

  public static final PermissionEnum ADMIN = new PermissionEnum( "ADMIN" );

}
