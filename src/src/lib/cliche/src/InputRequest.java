package lib.cliche.src;

import java.util.ArrayList;

/**
 * Created by Lasen on 02/10/2015.
 * Holds the question to be asked to the user, and the command to invoke with the answer.
 */
public class InputRequest
{
  private ArrayList<Question> questions;
  private String originalCommand;

  public InputRequest( final ArrayList<Question> questions, final String originalCommand )
  {
    this.questions = questions;
    this.originalCommand = originalCommand;
  }

  public ArrayList<Question> getQuestions()
  {
    return questions;
  }

  public String getOriginalCommand()
  {
    return originalCommand;
  }
}
