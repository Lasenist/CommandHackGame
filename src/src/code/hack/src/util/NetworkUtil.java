package code.hack.src.util;

/**
 * Created by Lasen on 25/11/15.
 * Helper class for network related methods
 */
public class NetworkUtil
{
  public static boolean isValidIp( final String ip )
  {
    boolean valid = true;
    final String[] ipSegments = ip.split( "\\." );

    if ( ipSegments.length == 4 )
    {
      for( final String ipSegment : ipSegments )
      {
        try
        {
          final int number = Integer.parseInt( ipSegment );
          if ( number > 255 || number < 0 )
          {
            valid = false;
          }
        }
        catch ( final NumberFormatException nfe )
        {
          valid = false;
        }
      }
    }
    else
    {
      valid = false;
    }
    return valid;
  }

}
