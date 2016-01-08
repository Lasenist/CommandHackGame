/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package lib.cliche.src;

import code.hack.src.util.Fn;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Command table is responsible for managing a lot of ShellCommands and is like a dictionary,
 * because its main function is to return a command by name.
 *
 * @author ASG
 */
public class CommandTable
{

  private List<ShellCommand> userCommandTable = new ArrayList<>();
  private List<ShellCommand> superCommandTable = new ArrayList<>();
  private CommandNamer namer;

  public CommandNamer getNamer()
  {
    return namer;
  }

  public CommandTable( CommandNamer namer )
  {
    this.namer = namer;
  }

  public List<ShellCommand> getUserCommandTable()
  {
    return Collections.unmodifiableList( userCommandTable );
  }

  public void addMethod( Method method, Object handler, String prefix )
  {
    final Command annotation = method.getAnnotation( Command.class );
    if ( annotation != null && annotation.visible() )
    {
      userCommandTable.add( buildCommand( method, handler, prefix ) );
    }
    else
    {
      superCommandTable.add( buildCommand( method, handler, prefix ) );
    }
  }

  public void removeMethod( final Method method )
  {
    String tokens = method.getName();

    for ( Object o : method.getParameterTypes() )
    {
      tokens = tokens + Fn.SPACE + "DUMMY_STRING";
    }

    try
    {
      if ( method.getAnnotation( Command.class ).visible() )
      {
        userCommandTable.remove( lookupCommand( method.getName(), Token.tokenize( tokens ) ) );
      }
      else
      {
        superCommandTable.remove( lookupCommand( method.getName(), Token.tokenize( tokens ), true ) );
      }
    }
    catch ( CLIException e )
    {
      e.printStackTrace();
    }
  }

  private ShellCommand buildCommand( final Method method, final Object handler, final String prefix )
  {
    Command annotation = method.getAnnotation( Command.class );
    String name;
    String autoAbbrev = null;

    if ( annotation != null && annotation.name() != null && ! annotation.name().equals( "" ) )
    {
      name = annotation.name();
    }
    else
    {
      CommandNamer.NamingInfo autoNames = namer.nameCommand( method );
      name = autoNames.commandName;
      for ( String abbr : autoNames.possibleAbbreviations )
      {
        if ( ! doesCommandExist( prefix + abbr, method.getParameterTypes().length ) )
        {
          autoAbbrev = abbr;
          break;
        }
      }
    }

    ShellCommand command = new ShellCommand( handler, method, prefix, name );

    if ( annotation != null && annotation.abbrev() != null && ! annotation.abbrev().equals( "" ) )
    {
      command.setAbbreviation( annotation.abbrev() );
    }
    else
    {
      command.setAbbreviation( autoAbbrev );
    }
    if ( annotation != null && annotation.description() != null && ! annotation.description().equals( "" ) )
    {
      command.setDescription( annotation.description() );
    }
    if ( annotation != null && annotation.header() != null && ! annotation.header().equals( "" ) )
    {
      command.setHeader( annotation.header() );
    }
    return command;
  }

  private boolean doesCommandExist( String commandName, int arity )
  {
    for ( ShellCommand cmd : userCommandTable )
    {
      if ( cmd.canBeDenotedBy( commandName ) && cmd.getArity() == arity )
      {
        return true;
      }
    }
    return false;
  }


  public List<ShellCommand> commandsByName( String discriminator, final boolean superCommands )
  {
    List<ShellCommand> collectedTable = new ArrayList<>();
    // collection
    for ( ShellCommand cs : userCommandTable )
    {
      if ( cs.canBeDenotedBy( discriminator ) )
      {
        collectedTable.add( cs );
      }
    }

    if ( superCommands )
    {
      for ( ShellCommand cs : superCommandTable )
      {
        if ( cs.canBeDenotedBy( discriminator ) )
        {
          collectedTable.add( cs );
        }
      }
    }
    return collectedTable;
  }

  public ShellCommand lookupCommand( final String discriminator, List<Token> tokens ) throws CLIException
  {
    return lookupCommand( discriminator, tokens, false );
  }

  public ShellCommand lookupCommand( String discriminator, List<Token> tokens, final boolean superCommands ) throws
          CLIException
  {
    List<ShellCommand> collectedTable = commandsByName( discriminator, superCommands );
    // reduction
    List<ShellCommand> reducedTable = new ArrayList<>();
    for ( ShellCommand cs : collectedTable )
    {
      if ( cs.getMethod().getParameterTypes().length == tokens.size() - 1
              || ( cs.getMethod().isVarArgs()
              && ( cs.getMethod().getParameterTypes().length <= tokens.size() - 1 ) ) )
      {
        reducedTable.add( cs );
      }
    }
    // selection
    if ( collectedTable.size() == 0 )
    {
      throw CLIException.createCommandNotFound( discriminator );
    }
    else if ( reducedTable.size() == 0 )
    {
      throw CLIException.createCommandNotFoundForArgNum( discriminator, tokens.size() - 1 );
    }
    else if ( reducedTable.size() > 1 )
    {
      throw CLIException.createAmbiguousCommandExc( discriminator, tokens.size() - 1 );
    }
    else
    {
      return reducedTable.get( 0 );
    }
  }




}
