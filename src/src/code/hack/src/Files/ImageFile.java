package code.hack.src.Files;

import code.hack.src.network.users.Account;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Lasen on 04/10/15.
 * A file that is an image
 */
public class ImageFile extends File
{
  private BufferedImage content;

  public ImageFile( final String name, final Account author, final Account lastAuthor, final Date dateCreated, final
  Date dateModified, final String imagePath )
  {
    super( name + ".png", author, lastAuthor, dateCreated, dateModified );

    try
    {
      this.content = ImageIO.read( getClass().getResource( imagePath ) );
    }
    catch ( IOException e ) { e.printStackTrace(); }

  }

  public BufferedImage getContent()
  {
    return content;
  }
}
