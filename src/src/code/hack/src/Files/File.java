package code.hack.src.Files;

import code.hack.src.network.users.Account;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lasen on 04/10/15.
 * Abstract class to be used by files that can be interacted with the player.
 */
public abstract class File
{
  protected String name;
  protected Account author;
  protected Account lastAuthor;
  protected ArrayList<FilePermissions> permissions;
  protected Date dateCreated;
  protected Date dateModified;

  public File( final String name, final Account author, final Account lastAuthor, final Date dateCreated, final
  Date dateModified )
  {
    this.name = name;
    this.author = author;
    this.lastAuthor = lastAuthor;
    this.dateCreated = dateCreated;
    this.dateModified = dateModified;
    permissions = new ArrayList<>();
  }

  public String getName()
  {
    return name;
  }

  public Account getAuthor()
  {
    return author;
  }

  public Account getLastAuthor()
  {
    return lastAuthor;
  }

  public ArrayList<FilePermissions> getPermissions()
  {
    return permissions;
  }

  public Date getDateCreated()
  {
    return dateCreated;
  }

  public Date getDateModified()
  {
    return dateModified;
  }

  public void addPermission( final FilePermissions filePermissions )
  {
    permissions.add( filePermissions );
  }

  public FilePermissions getPermission( final Account account )
  {
    for( FilePermissions o : permissions )
    {
      if ( o.getAccount().equals( account ) )
      {
        return o;
      }
    }
    return null;
  }
}
