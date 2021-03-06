/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package sf.util;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Utility methods.
 * 
 * @author Sualeh Fatehi
 */
public final class Utility
{

  private static final Logger LOGGER = Logger
    .getLogger(Utility.class.getName());

  /**
   * System specific line separator character.
   */
  public static final String NEWLINE = System.getProperty("line.separator");

  private static final Pattern containsWhitespacePattern = Pattern
    .compile(".*\\s.*");
  private static final Pattern isAllWhitespacePattern = Pattern
    .compile("^\\s*$");

  public static String commonPrefix(final String string1, final String string2)
  {
    final int index = indexOfDifference(string1, string2);
    if (index == -1)
    {
      return null;
    }
    else
    {
      return string1.substring(0, index).toLowerCase();
    }
  }

  /**
   * Checks if the text contains whitespace.
   * 
   * @param text
   *        Text to check.
   * @return Whether the string contains whitespace.
   */
  public static boolean containsWhitespace(final String text)
  {
    if (text == null)
    {
      return false;
    }
    else
    {
      return containsWhitespacePattern.matcher(text).matches();
    }
  }

  public static String convertForComparison(final String text)
  {
    final String textWithoutQuotes;
    if (!isBlank(text))
    {
      final char[] charArray = text.toCharArray();
      final StringBuilder builder = new StringBuilder();
      for (final char ch: charArray)
      {
        if (Character.isLetterOrDigit(ch) || ch == '_' || ch == '.')
        {
          builder.append(Character.toLowerCase(ch));
        }
      }
      textWithoutQuotes = builder.toString();
    }
    else
    {
      textWithoutQuotes = text;
    }
    return textWithoutQuotes;
  }

  public static void copyFile(final File sourceFile, final File destFile)
    throws IOException
  {
    if (!destFile.exists())
    {
      destFile.createNewFile();
    }

    try (final FileInputStream fileInputStream = new FileInputStream(sourceFile);
        final FileOutputStream fileOutputStream = new FileOutputStream(destFile);
        final FileChannel source = fileInputStream.getChannel();
        final FileChannel destination = fileOutputStream.getChannel();)
    {
      destination.transferFrom(source, 0, source.size());
    }
  }

  /**
   * Checks if the text is null or empty.
   * 
   * @param text
   *        Text to check.
   * @return Whether the string is blank.
   */
  public static boolean isBlank(final String text)
  {
    return text == null || text.isEmpty()
           || isAllWhitespacePattern.matcher(text).matches();
  }

  public static String pastelColorHTMLValue(final String text)
  {
    final float hue;
    if (isBlank(text))
    {
      hue = 0.123456f;
    }
    else
    {
      hue = text.hashCode() / 5119f % 1;
    }

    final float saturation = 0.4f;
    final float luminance = 0.98f;

    final Color color = Color.getHSBColor(hue, saturation, luminance);
    return "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
  }

  public static String readFully(final InputStream stream)
  {
    if (stream == null)
    {
      return null;
    }
    final Reader reader;
    try
    {
      reader = new InputStreamReader(stream, "UTF-8");
    }
    catch (final UnsupportedEncodingException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      return "";
    }
    return readFully(reader);
  }

  /**
   * Reads the stream fully, and returns a byte array of data.
   * 
   * @param reader
   *        Reader to read.
   * @return Byte array
   */
  public static String readFully(final Reader reader)
  {
    if (reader == null)
    {
      LOGGER.log(Level.WARNING, "Cannot read null reader");
      return "";
    }

    final StringBuilder out = new StringBuilder();

    final char[] buffer = new char[0x10000];
    try (final Reader bufferedReader = new BufferedReader(reader, buffer.length);)
    {
      int read;
      do
      {
        read = bufferedReader.read(buffer, 0, buffer.length);
        if (read > 0)
        {
          out.append(buffer, 0, read);
        }
      } while (read >= 0);
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }

    return out.toString();
  }

  public static String readResourceFully(final String resource)
  {
    return readFully(Utility.class.getResourceAsStream(resource));
  }

  /**
   * Sets the application-wide log level.
   * 
   * @param logLevel
   *        Log level to set
   */
  public static void setApplicationLogLevel(final Level logLevel)
  {
    final LogManager logManager = LogManager.getLogManager();
    final List<String> loggerNames = Collections.list(logManager
      .getLoggerNames());
    for (final String loggerName: loggerNames)
    {
      final Logger logger = logManager.getLogger(loggerName);
      if (logger != null)
      {
        logger.setLevel(null);
        for (final Handler handler: logger.getHandlers())
        {
          handler.setLevel(logLevel);
        }
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);
  }

  private static int indexOfDifference(final String string1,
                                       final String string2)
  {
    if (string1 == null || string2 == null)
    {
      return 0;
    }
    int i;
    for (i = 0; i < string1.length() && i < string2.length(); ++i)
    {
      if (string1.charAt(i) != string2.charAt(i))
      {
        break;
      }
    }
    if (i < string2.length() || i < string1.length())
    {
      return i;
    }
    return -1;
  }

  private Utility()
  { // Prevent instantiation
  }

}
