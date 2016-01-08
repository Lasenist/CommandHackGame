package code.hack.src.Files;

import code.hack.src.network.users.Account;

import java.util.Date;

/**
 * Created by Lasen on 04/10/15.
 * A text file
 */
public class TextFile extends File
{
  /*
  * V A R I A B L E S
  */
  private String content;

  /*
  * C O N S T R U C T O R
  */
  public TextFile( final String name, final Account author, final Account lastAuthor, final Date dateCreated, final
  Date dateModified, final String content )
  {
    super( name + ".txt", author, lastAuthor, dateCreated, dateModified );
    this.content = content;
  }

  /*
  * G E T T E R S
  */
  public String getContent()
  {
    return content;
  }

}
