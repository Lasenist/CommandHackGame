package lib.cliche.src;

/**
 * Created by Lasen on 24/09/2015.
 * Exception to be thrown when a maximum number of attempts has been reached.
 */
public class MaxAttemptsException extends Exception
{
  public MaxAttemptsException( final String message )
  {
    super( message );
  }
}
