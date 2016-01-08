package code.hack.src.util;

import code.hack.src.Files.FilePermissions;
import code.hack.src.network.users.Account;

/**
 * Created by Lasen on 05/10/15.
 * Utility class for returning common file permissions.
 */
public class FilePermissionUtil
{
  public FilePermissions getReadOnly( final Account account )
  {
    return new FilePermissions( true, false, false, true, account );
  }

  public FilePermissions getReadWrite( final Account account )
  {
    return new FilePermissions( true, true, false, true, account );
  }

  public FilePermissions getReadWriteDelete( final Account account )
  {
    return new FilePermissions( true, true, true, true, account );
  }

  public FilePermissions getInaccessible( final Account account )
  {
    return new FilePermissions( false, false, false, false, account );
  }
}
