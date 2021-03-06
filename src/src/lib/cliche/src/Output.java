/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package lib.cliche.src;

/**
 * Output for Shell to direct its output to.
 * Something like the Builder pattern.
 *
 * @author ASG
 */
public interface Output
{

  void output( Object obj, OutputConversionEngine oce );

  void outputException( String input, TokenException error );

  void outputException( Throwable e );

  void outputHeader( String text );

  void outputHeader( String text, String path );

}
