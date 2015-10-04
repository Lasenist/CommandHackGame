package code.hack.src.network.Users;


/**
 * Created by Lasen on 13/09/2015.
 * An account used to login to a server
 */
public class Account
{
  /*
  * V A R I A B L E S
  */
  private String username;
  private String password;
  private Enum permission;

  /*
  * C O N S T R U C T O R
  */
  public Account( final String username, final String password, final Enum permission )
  {
    this.username = username;
    this.password = password;
    this.permission = permission;
  }

  /*
  * G E T T E R S
  */
  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }

  public Enum getPermissions()
  {
    return permission;
  }

  /*
  * S E T T E R S
  */
  public void setPermissions( final Enum permissions )
  {
    this.permission = permissions;
  }
}
