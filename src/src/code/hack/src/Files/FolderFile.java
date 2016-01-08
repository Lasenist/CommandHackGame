package code.hack.src.Files;

import code.hack.src.Files.Exceptions.DuplicateFileException;
import code.hack.src.network.users.Account;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lasen on 05/10/15.
 * A file that holds other files.
 */
public class FolderFile extends File
{
  private ArrayList<File> contents;

  public FolderFile( final String name, final Account author, final Account lastAuthor, final Date dateCreated, final
  Date dateModified )
  {
    super( name, author, lastAuthor, dateCreated, dateModified );
    contents = new ArrayList<>();
  }

  public ArrayList<File> getContents()
  {
    return contents;
  }

  public File getFile( final String fileName )
  {
    File theFile = null;
    for( File file : contents )
    {
      if( file.getName().equals( fileName ) )
      {
        theFile = file;
      }
    }
    return theFile;
  }

  public void addFolder( final FolderFile folder ) throws DuplicateFileException
  {
    if ( getFile( folder.getName() ) instanceof FolderFile )
    {
      throw new DuplicateFileException( this, folder );
    }
    addFile( folder );
  }

  public void addFolder( final TextFile textFile ) throws DuplicateFileException
  {
    if ( getFile( textFile.getName() ) instanceof FolderFile )
    {
      throw new DuplicateFileException( this, textFile );
    }
    addFile( textFile );
  }

  public void addFolder( final ImageFile file ) throws DuplicateFileException
  {
    if ( getFile( file.getName() ) instanceof FolderFile )
    {
      throw new DuplicateFileException( this, file );
    }
    addFile( file );
  }

  private void addFile( final File file )
  {
    contents.add( file );
  }
}
