/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Options;
import sf.util.Utilities;

/**
 * Contains output options.
 * 
 * @author Sualeh Fatehi
 */
public final class OutputOptions
  implements Options
{

  private static final long serialVersionUID = 7018337388923813055L;

  private static final Logger LOGGER = Logger.getLogger(OutputOptions.class
    .getName());

  private String outputFormatValue;
  private File outputFile;

  private boolean appendOutput;

  private boolean noHeader;
  private boolean noFooter;
  private boolean noInfo;

  /**
   * Creates default OutputOptions.
   */
  public OutputOptions()
  {
    this(OutputFormat.text.name(), null);
  }

  /**
   * Output options, given the type and the output filename.
   * 
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   * @param outputFilename
   *        Output filename
   */
  public OutputOptions(final OutputFormat outputFormat,
                       final String outputFilename)
  {
    if (outputFormat == null)
    {
      throw new IllegalArgumentException("No output format provided");
    }
    outputFormatValue = outputFormat.name();

    if (!Utilities.isBlank(outputFilename))
    {
      outputFile = new File(outputFilename);
    }

    noHeader = true;
    noFooter = true;
    noInfo = true;
  }

  /**
   * Output options, given the type and the output filename.
   * 
   * @param outputFormatValue
   *        Type of output, which is dependent on the executor
   * @param outputFilename
   *        Output filename
   */
  public OutputOptions(final String outputFormatValue,
                       final String outputFilename)
  {
    this.outputFormatValue = outputFormatValue;

    if (!Utilities.isBlank(outputFilename))
    {
      outputFile = new File(outputFilename);
    }

    noHeader = true;
    noFooter = true;
    noInfo = true;
  }

  /**
   * Close the output writer.
   * 
   * @param writer
   *        Output writer
   */
  public void closeOutputWriter(final Writer writer)
  {
    if (outputFile != null)
    {
      if (writer != null)
      {
        try
        {
          writer.close();
          LOGGER.log(Level.FINER, "Output writer closed");
        }
        catch (final IOException e)
        {
          LOGGER.log(Level.FINER, "Exception closing output writer", e);
        }
      }
    }
    else
    {
      LOGGER.log(Level.FINER,
                 "Not closing output writer, since output is to console");
    }
  }

  /**
   * Clone this object.
   * 
   * @return Clone
   */
  public OutputOptions duplicate()
  {
    final OutputOptions outputOptions = new OutputOptions();

    outputOptions.outputFormatValue = outputFormatValue;

    outputOptions.outputFile = outputFile;

    outputOptions.appendOutput = appendOutput;

    outputOptions.noHeader = noHeader;
    outputOptions.noFooter = noFooter;
    outputOptions.noInfo = noInfo;

    return outputOptions;
  }

  /**
   * Output file, which has previously been created.
   * 
   * @return Output file
   */
  public File getOutputFile()
  {
    return outputFile;
  }

  /**
   * Output format.
   * 
   * @return Output format
   */
  public OutputFormat getOutputFormat()
  {
    OutputFormat outputFormat;
    try
    {
      outputFormat = OutputFormat.valueOf(outputFormatValue);
    }
    catch (final IllegalArgumentException e)
    {
      outputFormat = OutputFormat.other;
    }
    return outputFormat;
  }

  /**
   * Gets the output format value.
   * 
   * @return Output format value.s
   */
  public String getOutputFormatValue()
  {
    return outputFormatValue;
  }

  /**
   * Whether the output gets appended.
   * 
   * @return Whether the output gets appended
   */
  public boolean isAppendOutput()
  {
    return appendOutput;
  }

  /**
   * Whether to print footers.
   * 
   * @return Whether to print footers
   */
  public boolean isNoFooter()
  {
    return noFooter;
  }

  /**
   * Whether to print headers.
   * 
   * @return Whether to print headers
   */
  public boolean isNoHeader()
  {
    return noHeader;
  }

  /**
   * Whether to print information.
   * 
   * @return Whether to print information
   */
  public boolean isNoInfo()
  {
    return noInfo;
  }

  /**
   * Opens the output writer.
   * 
   * @throws IOException
   *         On an exception
   * @return Writer
   */
  public PrintWriter openOutputWriter()
    throws IOException
  {
    PrintWriter writer;
    if (outputFile == null)
    {
      writer = new PrintWriter(System.out, /* autoFlush = */true);
      LOGGER.log(Level.FINER, "Output writer to console opened");
    }
    else
    {
      final FileWriter fileWriter = new FileWriter(outputFile, appendOutput);
      writer = new PrintWriter(fileWriter, /* autoFlush = */true);
      LOGGER.log(Level.FINER, "Output writer to console opened");
    }
    return writer;
  }

  /**
   * Whether the output gets appended.
   * 
   * @param appendOutput
   *        Whether the output gets appended
   */
  public void setAppendOutput(final boolean appendOutput)
  {
    this.appendOutput = appendOutput;
  }

  /**
   * Whether to print footers.
   * 
   * @param noFooter
   *        Whether to print footers
   */
  public void setNoFooter(final boolean noFooter)
  {
    this.noFooter = noFooter;
  }

  /**
   * Whether to print headers.
   * 
   * @param noHeader
   *        Whether to print headers
   */
  public void setNoHeader(final boolean noHeader)
  {
    this.noHeader = noHeader;
  }

  /**
   * Whether to print information.
   * 
   * @param noInfo
   *        Whether to print information
   */
  public void setNoInfo(final boolean noInfo)
  {
    this.noInfo = noInfo;
  }

  /**
   * Sets the name of the output file.
   * 
   * @param outputFileName
   *        Output file name.
   */
  public void setOutputFileName(final String outputFileName)
  {
    if (outputFileName == null)
    {
      throw new IllegalArgumentException("Cannot set null output file name");
    }
    outputFile = new File(outputFileName);
    if (!outputFile.canWrite())
    {
      throw new IllegalArgumentException("Cannot write to "
                                         + outputFile.getAbsolutePath());
    }
  }

  /**
   * Sets output format value.
   * 
   * @param outputFormatValue
   *        Output format value
   */
  public void setOutputFormatValue(final String outputFormatValue)
  {
    if (outputFormatValue == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.outputFormatValue = outputFormatValue;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("OutputOptions[");
    buffer.append("outputFormat=").append(getOutputFormat());
    buffer.append("; outputFormatValue=").append(outputFormatValue);
    if (outputFile != null)
    {
      buffer.append("; outputFile=").append(outputFile.getAbsolutePath());
    }
    buffer.append("; appendOutput=").append(appendOutput);
    buffer.append("; noHeader=").append(noHeader);
    buffer.append("; noFooter=").append(noFooter);
    buffer.append("; noInfo=").append(noInfo);
    buffer.append("]");
    return buffer.toString();
  }

}
