package code.hack.src.Files.Exceptions;

import code.hack.src.Files.FolderFile;
import code.hack.src.Files.ImageFile;
import code.hack.src.Files.TextFile;

/**
 * Created by Lasen on 28/10/15.
 * Exception for when multiple files of the same name in same folder.
 */
public class DuplicateFileException extends Exception
{
  public DuplicateFileException( final FolderFile parentFolder, final FolderFile newFolder )
  {
    super( "The folder " + newFolder.getName() + " already exists in folder " + parentFolder.getName() + "." );
  }

  public DuplicateFileException( final FolderFile parentFolder, final TextFile newFile )
  {
    super( "The text file " + newFile.getName() + " already exists in folder " + parentFolder.getName() + "." );
  }

  public DuplicateFileException( final FolderFile parentFolder, final ImageFile newFile )
  {
    super( "The image file " + newFile.getName() + " already exists in folder " + parentFolder.getName() + "." );
  }
}
