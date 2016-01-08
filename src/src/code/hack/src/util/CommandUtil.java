package code.hack.src.util;

/**
 * Created by Lasen on 03/12/15.
 * Helper class for commands.
 */
public class CommandUtil
{
  /*
  * C O N S T R U C T O R
  */
  public static boolean isYesOrNo( final String answer )
  {
    return answer.toUpperCase().equals( "Y" ) || answer.toUpperCase().equals( "N" );
  }

  /*
  * M E T H O D S
  */
  public static boolean isYes( final String answer )
  {
    return answer.toUpperCase().equals( "Y" );
  }

  public static boolean isNo( final String answer )
  {
    return answer.toUpperCase().equals( "N" );
  }
}
