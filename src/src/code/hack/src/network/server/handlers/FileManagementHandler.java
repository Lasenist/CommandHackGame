package code.hack.src.network.server.handlers;

import code.hack.src.network.connection.Session;
import code.hack.src.util.FileUtil;
import lib.cliche.src.Command;
import lib.cliche.src.Response;

/**
 * Created by Lasen on 08/10/15.
 * Handler to let the user navigate directories etc.
 */
public class FileManagementHandler extends CommandHandler
{
  public FileManagementHandler( final Session session )
  {
    super( session );
  }

  @Command( description = "Move to another folder" )
  public Response cd( final String path )
  {
    Response response = new Response();
    if ( FileUtil.isValidPath( path ) )
    {
      response = server.cd( session, path );
    }
    return response;
  }

  @Command( description = "Print working directory" )
  public Response ls()
  {
    return server.ls( session );
  }

}
