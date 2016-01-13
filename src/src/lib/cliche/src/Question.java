package lib.cliche.src;

/**
 * Created by Lasen on 10/01/16.
 * Object to hold questions to ask the player.
 */
public class Question
{
  private String commandName;
  private String question;

  public Question( final String commandName, final String question )
  {
    this.commandName = commandName;
    this.question = question + ":";
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
