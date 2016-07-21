/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.pdflayout.pdfbox;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDAbstractContentStream;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentHelper;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;

import com.helger.commons.annotation.CodingStyleguideUnaware;

/**
 * Provides the ability to write to a page content stream.<br>
 * Extensions/changes for this project:
 * <ul>
 * <li>Speed up in text drawing</li>
 * <li>Removed all deprecated methods</li>
 * <li>Allowing to prepend content</li>
 * </ul>
 *
 * @author Ben Litchfield
 */
@CodingStyleguideUnaware
@NotThreadSafe
public final class PDPageContentStreamExt extends PDAbstractContentStream
{
  public static enum EAppendMode
  {
    OVERWRITE,
    APPEND,
    PREPEND;

    public boolean isNotOverwrite ()
    {
      return this != OVERWRITE;
    }

    public boolean isPrepend ()
    {
      return this == PREPEND;
    }
  }
 private static final Log s_aLogger = LogFactory.getLog (PDPageContentStreamExt.class);

  private final PDDocument m_aDoc;

  private final Stack <PDFont> fontStack = new Stack<> ();

  /**
   * Create a new PDPage content stream.
   *
   * @param document
   *        The document the page is part of.
   * @param sourcePage
   *        The page to write the contents to.
   * @throws IOException
   *         If there is an error writing to the page contents.
   */
  public PDPageContentStreamExt (final PDDocument document, final PDPage sourcePage) throws IOException
  {
    this (document, sourcePage, EAppendMode.OVERWRITE, true);
  }

  /**
   * Create a new PDPage content stream.
   *
   * @param document
   *        The document the page is part of.
   * @param sourcePage
   *        The page to write the contents to.
   * @param appendContent
   *        Indicates whether content will be overwritten. If false all previous
   *        content is deleted.
   * @param compress
   *        Tell if the content stream should compress the page contents.
   * @throws IOException
   *         If there is an error writing to the page contents.
   */
  public PDPageContentStreamExt (final PDDocument document,
                                 final PDPage sourcePage,
                                 final EAppendMode appendContent,
                                 final boolean compress) throws IOException
  {
    this (document, sourcePage, appendContent, compress, false);
  }

  /**
   * Create a new PDPage content stream.
   *
   * @param document
   *        The document the page is part of.
   * @param sourcePage
   *        The page to write the contents to.
   * @param appendContent
   *        Indicates whether content will be overwritten. If false all previous
   *        content is deleted.
   * @param compress
   *        Tell if the content stream should compress the page contents.
   * @param resetContext
   *        Tell if the graphic context should be reseted.
   * @throws IOException
   *         If there is an error writing to the page contents.
   */
  public PDPageContentStreamExt (final PDDocument document,
                                 final PDPage sourcePage,
                                 final EAppendMode appendContent,
                                 final boolean compress,
                                 final boolean resetContext) throws IOException
  {
    this.m_aDoc = document;
    final COSName filter = compress ? COSName.FLATE_DECODE : null;

    // If request specifies the need to append to the document
    if (appendContent.isNotOverwrite () && sourcePage.hasContents ())
    {
      // Create a stream to append new content
      final PDStream contentsToAppend = new PDStream (document);

      // Add new stream to contents array
      final COSBase contents = sourcePage.getCOSObject ().getDictionaryObject (COSName.CONTENTS);
      COSArray array;
      if (contents instanceof COSArray)
      {
        // If contents is already an array, a new stream is simply appended to
        // it
        array = (COSArray) contents;
      }
      else
      {
        // Creates a new array and adds the current stream plus a new one to it
        array = new COSArray ();
        array.add (contents);
      }
      if (appendContent.isPrepend ())
        array.add (0, contentsToAppend.getCOSObject ());
      else
        array.add (contentsToAppend);

      // save the initial/unmodified graphics context
      if (resetContext)
      {
        // create a new stream to encapsulate the existing stream
        final PDStream saveGraphics = new PDStream (document);
        setOutput (saveGraphics.createOutputStream (filter));

        // save the initial/unmodified graphics context
        saveGraphicsState ();
        close ();

        // insert the new stream at the beginning
        array.add (0, saveGraphics.getCOSObject ());
      }

      // Sets the compoundStream as page contents
      sourcePage.getCOSObject ().setItem (COSName.CONTENTS, array);
      setOutput (contentsToAppend.createOutputStream (filter));

      // restore the initial/unmodified graphics context
      if (resetContext)
      {
        restoreGraphicsState ();
      }
    }
    else
    {
      if (sourcePage.hasContents ())
      {
        s_aLogger.warn ("You are overwriting an existing content, you should use the append mode");
      }
      final PDStream contents = new PDStream (document);
      sourcePage.setContents (contents);
      setOutput (contents.createOutputStream (filter));
    }

    // this has to be done here, as the resources will be set to null when
    // resetting the content
    // stream
    PDResources resources = sourcePage.getResources ();
    if (resources == null)
    {
      resources = new PDResources ();
      sourcePage.setResources (resources);
    }
    setResources (resources);
    // configure NumberFormat
    setMaximumFractionDigits (5);
  }

  /**
   * Create a new appearance stream. Note that this is not actually a "page"
   * content stream.
   *
   * @param doc
   *        The document the page is part of.
   * @param appearance
   *        The appearance stream to write to.
   * @throws IOException
   *         If there is an error writing to the page contents.
   */
  public PDPageContentStreamExt (final PDDocument doc, final PDAppearanceStream appearance) throws IOException
  {
    this (doc, appearance, appearance.getStream ().createOutputStream ());
  }

  /**
   * Create a new appearance stream. Note that this is not actually a "page"
   * content stream.
   *
   * @param doc
   *        The document the appearance is part of.
   * @param appearance
   *        The appearance stream to add to.
   * @param outputStream
   *        The appearances output stream to write to.
   * @throws IOException
   *         If there is an error writing to the page contents.
   */
  public PDPageContentStreamExt (final PDDocument doc,
                                 final PDAppearanceStream appearance,
                                 final OutputStream outputStream) throws IOException
  {
    super (appearance, outputStream);
    this.m_aDoc = doc;
    setResources (appearance.getResources ());
  }

  /**
   * Set the font and font size to draw text with.
   *
   * @param font
   *        The font to use.
   * @param fontSize
   *        The font size to draw the text.
   * @throws IOException
   *         If there is an error writing the font information.
   */
  @Override
  public void setFont (final PDFont font, final float fontSize) throws IOException
  {
    if (fontStack.isEmpty ())
      fontStack.add (font);
    else
      fontStack.set (fontStack.size () - 1, font);

    PDDocumentHelper.handleFontSubset (m_aDoc, font);

    writeOperand (getResources ().add (font));
    writeOperand (fontSize);
    writeOperator ("Tf");
  }

  /**
   * Shows the given text at the location specified by the current text matrix.
   *
   * @param text
   *        The Unicode text to show.
   * @throws IOException
   *         If an io exception occurs.
   */
  @Override
  public void showText (final String text) throws IOException
  {
    if (!isInTextMode ())
    {
      throw new IllegalStateException ("Must call beginText() before showText()");
    }

    if (fontStack.isEmpty ())
    {
      throw new IllegalStateException ("Must call setFont() before showText()");
    }

    final PDFont font = fontStack.peek ();

    // Unicode code points to keep when subsetting
    if (font.willBeSubset ())
    {
      for (int offset = 0; offset < text.length ();)
      {
        final int codePoint = text.codePointAt (offset);
        font.addToSubset (codePoint);
        offset += Character.charCount (codePoint);
      }
    }

    COSWriter.writeString (font.encode (text), getOutput ());
    write (" ");

    writeOperator ("Tj");
  }

  /**
   * q operator. Saves the current graphics state.
   *
   * @throws IOException
   *         If an error occurs while writing to the stream.
   */
  @Override
  public void saveGraphicsState () throws IOException
  {
    if (!fontStack.isEmpty ())
    {
      fontStack.push (fontStack.peek ());
    }
    writeOperator ("q");
  }

  /**
   * Q operator. Restores the current graphics state.
   *
   * @throws IOException
   *         If an error occurs while writing to the stream.
   */
  @Override
  public void restoreGraphicsState () throws IOException
  {
    if (!fontStack.isEmpty ())
    {
      fontStack.pop ();
    }
    writeOperator ("Q");
  }

  @Override
  public void write (final String text) throws IOException
  {
    super.write (text);
  }

  @Override
  public void writeOperator (final String text) throws IOException
  {
    super.writeOperator (text);
  }
}
