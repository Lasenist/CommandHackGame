package lib.cliche.src;

import code.hack.src.util.Fn;
import lib.cliche.src.util.Strings;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


/**
 * Created by Lasen on 09/01/16.
 * Custom command prompt
 */
public class ShellFrame extends JPanel
{
  private int FONT_SIZE = 16;

  private JFrame frame;

  private boolean isAnsweringQuestions;

  private final Shell shell;
  private final Timer cursorBlinkTimer;

  private int textOffset;
  private int historyOffset;
  private int cursorOffset;

  private ArrayList<ShellOutput> output;
  private InputRequest inputRequest;
  private String path;
  private String input;
  private String cursor;

  private Font font;
  private FontMetrics fontMetric;

  public ShellFrame( final Shell shell)
  {
    this.shell = shell;
    path = Strings.joinStrings( shell.getPath(), false, '/' ) + ">";
    input = Fn.EMPTY_STRING;
    cursor = "_";

    cursorBlinkTimer = new Timer( 700, new CursorActionListener() );
    cursorBlinkTimer.start();

    output = new ArrayList<>();

    font = new Font( Font.MONOSPACED, Font.LAYOUT_LEFT_TO_RIGHT, FONT_SIZE );

    addMouseWheelListener( new MouseAdapter()
    {
      public void mouseWheelMoved( MouseWheelEvent e )
      {
        final int unitsToScroll = e.getUnitsToScroll();
        if ( e.isControlDown() )
        {
          if ( unitsToScroll > 0 )
          {
            FONT_SIZE++;
          }
          else if ( unitsToScroll < 0 )
          {
            FONT_SIZE--;
          }
        }
        else
        {
          if ( unitsToScroll > 0 )
          {
            textOffset--;
          }
          else if ( unitsToScroll < 0 && textOffset != 0 )
          {
            textOffset++;
          }
        }
      }
    } );
  }

  public void updatePath()
  {
    this.path = Strings.joinStrings( shell.getPath(), false, '/' ) + ">";
  }

  public boolean isAnsweringQuestions()
  {
    return isAnsweringQuestions;
  }

  public JFrame getFrame()
  {
    return frame;
  }

  protected void paintComponent( Graphics g )
  {
    super.paintComponent( g );
    g.setColor( Color.BLACK );
    g.fillRect( 0, 0, getWidth(), getHeight() );

    g.setColor( Color.WHITE );

    int y = 1;
    font = new Font( Font.MONOSPACED, Font.LAYOUT_LEFT_TO_RIGHT, FONT_SIZE - 4 );
    g.setFont( font );
    fontMetric = g.getFontMetrics( font );

    //Paint everything in the history
    for ( final ShellOutput output : this.output )
    {
      g.drawString( output.getString(), 3, textOffset * fontMetric.getHeight() + y * fontMetric.getHeight() - 1 );
      y++;
    }

    final int yCoord = textOffset * fontMetric.getHeight() + y * fontMetric.getHeight() - 1;
    //Draw path and inputted text
    if ( isAnsweringQuestions )
    {
      g.drawString( inputRequest.getQuestions().get( 0 ).getQuestion() + input, 3, yCoord );
      g.drawString( cursor, fontMetric.stringWidth( inputRequest.getQuestions().get( 0 ).getQuestion() +
              input.substring( 0, input.length() - cursorOffset ) ) + 3 ,
              yCoord );
    }
    else
    {
      g.drawString( path + input, 3, yCoord );
      g.drawString( cursor, fontMetric.stringWidth( path + input.substring( 0, input.length() - cursorOffset ) ) + 3 ,
            yCoord );
    }

    //Debug draws below //TODO: Remove these
//    g.drawString( String.valueOf( textOffset ), 750, fontMetric.getHeight() );
  }

  public Dimension getPreferredSize()
  {
    return new Dimension( 800, 400 );
  }

  protected void initUI()
  {
    final String title = "CommandShell V1.0";
    frame = new JFrame( title );
    addToOutput( title );
    frame.addFocusListener(
            new FocusAdapter()
            {
              public void focusGained( FocusEvent e )
              {
                if ( ! cursorBlinkTimer.isRunning() )
                {
                  cursorBlinkTimer.start();
                  cursor = "_";
                  repaint();
                }
              }

              public void focusLost( FocusEvent e )
              {
                if ( cursorBlinkTimer.isRunning() )
                {
                  cursorBlinkTimer.stop();
                  cursor = Fn.EMPTY_STRING;
                  repaint();
                }
              }
            } );
    frame.addKeyListener( new KeyAdapter()
    {
      public void keyTyped( KeyEvent e )
      {
        if ( KeyEvent.getKeyText( e.getKeyChar() ).equals( "Backspace" ) )
        {
          if ( !input.equals( Fn.EMPTY_STRING ) && cursorOffset < input.length() )
          {
            input = input.substring( 0, input.length() - cursorOffset - 1 ) + input.substring( input
                    .length() - cursorOffset, input.length() );
          }
        }
        else if( KeyEvent.getKeyText( e.getKeyChar() ).equals( "Delete" ) )
        {
          if ( ! input.equals( Fn.EMPTY_STRING ) && cursorOffset > 0 )
          {
            input = input.substring( 0, input.length() - cursorOffset ) + input.substring( input.length() - cursorOffset + 1, input.length() );
            cursorOffset--;
          }
        }
        else if( KeyEvent.getKeyText( e.getKeyChar() ).equals( "Enter" ) )
        {
          try
          {
            if ( !isAnsweringQuestions )
            {
              output.add( new ShellOutput( path, input ) );
              shell.processLine( input );
            }
            else
            {
              output.add( new ShellOutput( inputRequest.getQuestions().get( 0 ).getQuestion(), input ) );
              shell.processLine( inputRequest.getQuestions().get( 0 ).getCommandName() + Fn.SPACE + input );
              inputRequest.getQuestions().remove( 0 );

              if ( inputRequest.getQuestions().isEmpty() )
              {
                shell.processLine( inputRequest.getOriginalCommand() );
                isAnsweringQuestions = false;
                inputRequest = null;
              }
            }
              input = Fn.EMPTY_STRING;
              historyOffset = 0;
              cursorOffset = 0;
          }
          catch ( CLIException | IllegalAccessException e1 )
          {
            e1.printStackTrace();
            addToOutput( e1.getMessage() );
            input = Fn.EMPTY_STRING;
          }
          catch ( InvocationTargetException ite )
          {
            final Throwable cause = ite.getCause();
            cause.printStackTrace();
            addToOutput( cause.getMessage() );
            input = Fn.EMPTY_STRING;
          }
          finally
          {
            final int yCoord = textOffset * fontMetric.getHeight() + ( output.size() + 1 ) * fontMetric.getHeight() - 1;
            if ( yCoord > getHeight() )
            {
              final int difference = ((yCoord - getHeight()) / fontMetric.getHeight()) + 1;
              textOffset = textOffset - difference;
            }
          }
        }
        else if ( e.getKeyChar() == '\u0016' )
        {
          System.out.println( "Unknown Key Pressed" );
        }
        else
        {
          processInput( String.valueOf( e.getKeyChar() ) );
        }
      }

      public void keyPressed( KeyEvent e )
      {
        if ( e.getKeyCode() == KeyEvent.VK_UP )
        {
          input = getPreviousOutput().getInput();
        }
        else if ( e.getKeyCode() == KeyEvent.VK_DOWN )
        {
          if ( historyOffset != 0 )
          {
            input = getNextOutput().getInput();
          }
        }
        else if ( e.getKeyCode() == KeyEvent.VK_LEFT )
        {
           if ( cursorOffset < input.length() )
           {
             cursorOffset++;
            cursor = "_";
           }
        }
        else if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
        {
          if ( cursorOffset > 0 )
          {
            cursorOffset--;
            cursor = "_";
          }
        }
        else if ( e.getKeyCode() == KeyEvent.VK_HOME )
        {
          cursorOffset = input.length();
          cursor = "_";
        }
        else if ( e.getKeyCode() == KeyEvent.VK_END )
        {
          cursorOffset = 0;
          cursor = "_";
        }
        else if ( e.getKeyCode() == KeyEvent.VK_V && e.isControlDown() )
        {
          try
          {
            processInput( (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData( DataFlavor.stringFlavor) );
          }
          catch ( UnsupportedFlavorException | IOException e1 )
          {
            e1.printStackTrace();
          }
        }
      }
    } );
    frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    frame.add( this );
    frame.pack();
    frame.setVisible( true );
  }

  private void processInput( final String string )
  {
    if ( !input.equals( Fn.EMPTY_STRING ) )
    {
      input = input.substring( 0, input.length() - cursorOffset ) + string + input.substring( input
              .length() - cursorOffset, input.length() );
    }
    else
    {
      input = input + string;
    }
  }

  private ShellOutput getPreviousOutput()
  {
    if(  output.size() - historyOffset - 1 > 0 )
    {
      historyOffset++;
    }
    ShellOutput shellOutput = output.get( output.size() - historyOffset - 1 );
    if ( ( shellOutput.getPath().equals( Fn.EMPTY_STRING ) || shellOutput.getInput().equals( Fn.EMPTY_STRING ) ) &&
            ( historyOffset + 1 ) < output.size() )
    {
      shellOutput = getPreviousOutput();
    }
    return shellOutput.getPath().equals( Fn.EMPTY_STRING ) ? new ShellOutput( Fn.EMPTY_STRING, input ) : shellOutput;
  }

  private ShellOutput getNextOutput()
  {
    if( historyOffset > 0 )
    {
      historyOffset--;
    }
    ShellOutput shellOutput = output.get( output.size() - historyOffset - 1 );
    if ( ( shellOutput.getPath().equals( Fn.EMPTY_STRING ) || shellOutput.getInput().equals( Fn.EMPTY_STRING ) ) &&
            historyOffset != 0 )
    {
      shellOutput = getNextOutput();
    }
    return shellOutput.getPath().equals( Fn.EMPTY_STRING ) ? new ShellOutput( Fn.EMPTY_STRING, input ) : shellOutput;
  }

  public void addToOutput( final String string )
  {
    output.add( new ShellOutput( Fn.EMPTY_STRING, string ) );

    if ( fontMetric != null )
    {
      //Keep cursor on the screen. Below sets textsOffset so it is
      final int yCoordOffset = textOffset * fontMetric.getHeight() + ( output.size() + 1 ) * fontMetric.getHeight() - 1;
      if ( yCoordOffset > getHeight() )
      {
        final int difference = ( ( yCoordOffset - getHeight() ) / fontMetric.getHeight() ) + 1;
        textOffset = textOffset - difference;
      }
    }
  }

  public void setInputRequest( final InputRequest inputRequest )
  {
    this.inputRequest = inputRequest;
    isAnsweringQuestions = true;
  }

  private class CursorActionListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      if( cursor.equals( "_" ) )
      {
        cursor = Fn.EMPTY_STRING;
      }
      else
      {
        cursor = "_";
      }
    }
  }

  private class ShellOutput
  {
    final String path;
    final String input;

    public ShellOutput( final String path, final String input )
    {
      this.path = path;
      this.input = input;
    }

    public String getPath()
    {
      return path;
    }

    public String getInput()
    {
      return input;
    }

    public String getString()
    {
      return path + input;
    }
  }
}
