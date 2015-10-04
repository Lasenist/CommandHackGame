package lib.cliche.src;

/**
 * Created by Lasen on 02/10/2015.
 * Holds the question to be asked to the user, and the command to invoke with the answer.
 */
public class InputRequest
{
  private String commandName;
  private String question;

  public InputRequest( final String commandName, final String question )
  {
    this.commandName = commandName;
    this.question = question;
  }

  public String getCommandName()
  {
    return commandName;
  }

  public String getQuestion()
  {
    return question;
  }
}
