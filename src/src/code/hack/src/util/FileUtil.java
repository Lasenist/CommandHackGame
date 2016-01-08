package code.hack.src.util;

import code.hack.src.Files.File;
import code.hack.src.Files.FolderFile;

/**
 * Created by Lasen on 09/10/15.
 * Utility methods for working with files
 */
public class FileUtil
{

  public static boolean isValidFileName( final String fileName ) // Excluding  \/:*?"<>|
  {
    boolean isValid = true;
    for ( final String s : fileName.split("(?!^)") )
    {
      if( s.equals( "\\" ) || s.equals( "/" ) || s.equals( ":" ) || s.equals( "*" ) || s.equals( "?" ) || s.equals(
              "\"" ) || s.equals( "<" ) || s.equals( ">" ) || s.equals( "|" ) )
      {
        isValid = false;
      }
    }
    return isValid;
  }

  public static boolean isValidPath( final String path )
  {
    boolean isValid = true;
    final String[] segments = getFoldersInPath( path );

    for ( String folderName : segments )
    {
      if ( !isValidFileName( folderName ) )
      {
        isValid = false;
      }
    }
    return isValid;
  }

  private static String getPathSlash( final String path )
  {
    String slash = null;
    for ( final String s : path.split( "(?!^)" ) )
    {
      if( s.equals( "\\" ) )
      {
        slash = "\\";
      }
      else if( s.equals( "/" ) )
      {
        slash = "/";
      }
    }
    return slash;
  }

  public static String[] getFoldersInPath( String path )
  {
    String[] folders = new String[]{ path };
    if ( !path.equals( Fn.EMPTY_STRING ) )
    {
      if ( path.substring( 0, 1 ).equals( getPathSlash( path ) ) )
      {
        path = path.substring( 1 );
      }

      final String slash = getPathSlash( path );

      if ( slash != null )
      {
        folders = path.split( slash );
      }
    }
    return folders;
  }

  private static String removeFirstPathSegment( final String path )
  {
    final String [] pathSegments = getFoldersInPath( path );
    String newPath = Fn.EMPTY_STRING;

    for ( int i = 1; i < pathSegments.length; i++ )
    {
      newPath = newPath + pathSegments[i];
      if ( i != pathSegments.length - 1 )
      {
        newPath = newPath + "/";
      }
    }
    return newPath;
  }

  private static String removeLastPathSegment( final String path )
  {
    final String [] pathSegments = getFoldersInPath( path );
    String newPath = Fn.EMPTY_STRING;

    for ( int i = 0; i < pathSegments.length; i++ )
    {
      if ( i != pathSegments.length-1 )
      {
        newPath = newPath + pathSegments[i];
        if ( i != pathSegments.length-2 )
        {
          newPath = newPath + "/";
        }
      }
    }
    return newPath;
  }

  public static FolderFile getFolderFromPath( final FolderFile files, String path )
  {
    path = removeFirstPathSegment( path );
    final String[] pathSegments = FileUtil.getFoldersInPath( path );
    FolderFile aFile = null;

    if ( !pathSegments[0].equals( "" ) )
    {
      for ( File file : files.getContents() )
      {
        boolean isNameMatching = file.getName().equals( pathSegments[0] );
        boolean isFolder = file instanceof FolderFile;

        if ( pathSegments.length == 1 && isNameMatching && isFolder )
        {
          aFile = (FolderFile) file;
        }
        else if ( isNameMatching && isFolder )
        {
          aFile = getFolderFromPath( (FolderFile) file, removeFirstPathSegment( path ) );
        }
      }
    }
    else if ( pathSegments[0].equals( "" ) )
    {
      aFile = files;
    }
    return aFile;
  }

  public static String getNewPath( String currentPath, final String cdPath )
  {
    final String[] cdPathSegments = FileUtil.getFoldersInPath( cdPath );

    for ( int i = 0; i < cdPathSegments.length; i++ )
    {
      if ( cdPathSegments[i].equals( ".." ) )
      {
        currentPath = removeLastPathSegment( currentPath );
      }
      else
      {
        currentPath = currentPath + cdPathSegments[i];
        if ( i == cdPathSegments.length - 1 )
        {
          currentPath = currentPath + "/";
        }
      }
    }
    return currentPath;
  }
}
