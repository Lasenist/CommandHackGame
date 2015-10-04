package lib.cliche.src;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

/**
 * Created by Lasen on 23/09/2015.
 * User to allow a handler to communicate with the shell.
 */
public class Response
{
  //Notifies the shell that...
  //The command is finished
  public static final int FINISHED = 0;
  //The command requests input
  public static final int REQUEST_INPUT = 1;
  //The command is giving a handler;
  public static final int UPDATE_SESSION_HANDLER = 2;


  /*
  * V A R I A B L E S
  */
  //Why the command is notifying the shell
  @NotNull
  private int response;

  //The name of the command used to validate the requested input.
  private String validationCommand;

  //The question to ask the user for the input
  private ArrayList<InputRequest> questions;

  //The message given to the user after the command has finished.
  private String message;

  //The handler to give the Shell.
  private Object handler;

  /*
  * C O N S T R U C T O R
  */
  public Response( final int response )
  {
    this.response = response;
    questions = new ArrayList<>();
  }

  /*
  * G E T T E R S
  */
  public int getResponse()
  {
    return response;
  }

  public String getValidationCommand()
  {
    return validationCommand;
  }

  public ArrayList<InputRequest> getQuestions()
  {
    return questions;
  }

  public String getMessage()
  {
    return message;
  }

  public Object getNewHandler()
  {
    return handler;
  }

  /*
  * S E T T E R S
  */
  public void setResponse( final int response )
  {
    this.response = response;
  }

  public void setMessage( final String message )
  {
    this.message = message;
  }

  public void setNewHandler( final Object handler )
  {
    this.handler = handler;
  }

  /*
  * M E T H O D S
  */
  public void addToSetters( final String commandName, final String question )
  {
    questions.add( new InputRequest( commandName, question ) );
  }
}
