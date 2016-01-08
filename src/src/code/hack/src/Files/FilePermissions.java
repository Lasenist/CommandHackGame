package code.hack.src.Files;

import code.hack.src.network.users.Account;

/**
 * Created by Lasen on 04/10/15.
 * Holds a files permissions relative to the user.
 */
public class FilePermissions
{
  /*
  * V A R I A B L E S
  */
  private boolean read;
  private boolean write;
  private boolean delete;
  private boolean execute;
  private Account account;

  /*
  * C O N S T R U C T O R
  */
  public FilePermissions( final boolean read, final boolean write, final boolean delete, final
  boolean execute, final Account account )
  {
    this.read = read;
    this.write = write;
    this.delete = delete;
    this.execute = execute;
    this.account = account;
  }

  /*
  * G E T T E R S
  */
  public boolean isRead()
  {
    return read;
  }

  public boolean isWrite()
  {
    return write;
  }

  public boolean isDelete()
  {
    return delete;
  }

  public boolean isExecute()
  {
    return execute;
  }

  public Account getAccount()
  {
    return account;
  }
}
