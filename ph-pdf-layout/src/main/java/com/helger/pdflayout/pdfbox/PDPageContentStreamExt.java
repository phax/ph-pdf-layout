/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.awt.geom.AffineTransform;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentHelper;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceN;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDSeparation;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDInlineImage;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.NumberFormatUtil;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.collection.NonBlockingStack;
import com.helger.pdflayout.base.PLColor;

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
public class PDPageContentStreamExt implements Closeable
{
  private static final Log LOGGER = LogFactory.getLog (PDPageContentStreamExt.class);

  private final PDDocument m_aDoc;
  protected OutputStream m_aOS;
  private PDResources m_aResources;

  private boolean m_bInTextMode = false;
  private final NonBlockingStack <PDFont> m_aFontStack = new NonBlockingStack <> ();

  private final NonBlockingStack <PDColorSpace> m_aNonStrokingColorSpaceStack = new NonBlockingStack <> ();
  private final NonBlockingStack <PDColorSpace> m_aStrokingColorSpaceStack = new NonBlockingStack <> ();

  // number format
  private final NumberFormat m_aFormatDecimal = NumberFormat.getNumberInstance (Locale.US);
  private final byte [] m_aFormatBuffer = new byte [32];

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
    this (document, sourcePage, PDPageContentStream.AppendMode.OVERWRITE, true);
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
                                 final PDPageContentStream.AppendMode appendContent,
                                 final boolean compress) throws IOException
  {
    this (document, sourcePage, appendContent, compress, false);
  }

  /**
   * Create a new PDPage content stream.
   *
   * @param aDoc
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
  public PDPageContentStreamExt (final PDDocument aDoc,
                                 final PDPage sourcePage,
                                 final PDPageContentStream.AppendMode appendContent,
                                 final boolean compress,
                                 final boolean resetContext) throws IOException
  {
    m_aDoc = aDoc;
    final COSName filter = compress ? COSName.FLATE_DECODE : null;

    // If request specifies the need to append to the document
    if (!appendContent.isOverwrite () && sourcePage.hasContents ())
    {
      // Create a stream to append new content
      final PDStream contentsToAppend = new PDStream (aDoc);

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
        final PDStream saveGraphics = new PDStream (aDoc);
        m_aOS = saveGraphics.createOutputStream (filter);

        // save the initial/unmodified graphics context
        saveGraphicsState ();
        close ();

        // insert the new stream at the beginning
        array.add (0, saveGraphics.getCOSObject ());
      }

      // Sets the compoundStream as page contents
      sourcePage.getCOSObject ().setItem (COSName.CONTENTS, array);
      m_aOS = contentsToAppend.createOutputStream (filter);

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
        LOGGER.warn ("You are overwriting an existing content, you should use the append mode");
      }
      final PDStream contents = new PDStream (aDoc);
      sourcePage.setContents (contents);
      m_aOS = contents.createOutputStream (filter);
    }

    // this has to be done here, as the resources will be set to null when
    // resetting the content stream
    m_aResources = sourcePage.getResources ();
    if (m_aResources == null)
    {
      m_aResources = new PDResources ();
      sourcePage.setResources (m_aResources);
    }

    // configure NumberFormat
    m_aFormatDecimal.setMaximumFractionDigits (5);
    m_aFormatDecimal.setGroupingUsed (false);
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
   * @param aDoc
   *        The document the appearance is part of.
   * @param appearance
   *        The appearance stream to add to.
   * @param outputStream
   *        The appearances output stream to write to.
   */
  public PDPageContentStreamExt (final PDDocument aDoc,
                                 final PDAppearanceStream appearance,
                                 final OutputStream outputStream)
  {
    m_aDoc = aDoc;

    m_aOS = outputStream;
    m_aResources = appearance.getResources ();

    m_aFormatDecimal.setMaximumFractionDigits (4);
    m_aFormatDecimal.setGroupingUsed (false);
  }

  @Nonnull
  OutputStream getOutput ()
  {
    return m_aOS;
  }

  /**
   * Begin some text operations.
   *
   * @throws IOException
   *         If there is an error writing to the stream or if you attempt to
   *         nest beginText calls.
   * @throws IllegalStateException
   *         If the method was not allowed to be called at this time.
   */
  public void beginText () throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: Nested beginText() calls are not allowed.");
    }
    writeOperator ((byte) 'B', (byte) 'T');
    m_bInTextMode = true;
  }

  /**
   * End some text operations.
   *
   * @throws IOException
   *         If there is an error writing to the stream or if you attempt to
   *         nest endText calls.
   * @throws IllegalStateException
   *         If the method was not allowed to be called at this time.
   */
  public void endText () throws IOException
  {
    if (!m_bInTextMode)
    {
      throw new IllegalStateException ("Error: You must call beginText() before calling endText.");
    }
    writeOperator ((byte) 'E', (byte) 'T');
    m_bInTextMode = false;
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
  public void setFont (final PDFont font, final float fontSize) throws IOException
  {
    if (m_aFontStack.isEmpty ())
      m_aFontStack.add (font);
    else
      m_aFontStack.set (m_aFontStack.size () - 1, font);

    PDDocumentHelper.handleFontSubset (m_aDoc, font);

    writeOperand (m_aResources.add (font));
    writeOperand (fontSize);
    writeOperator ((byte) 'T', (byte) 'f');
  }

  /**
   * Set the character spacing. The value shall be added to the horizontal or
   * vertical component of the glyph's displacement, depending on the writing
   * mode.
   *
   * @param spacing
   *        character spacing
   * @throws IOException
   *         If the content stream could not be written.
   */
  public void setCharacterSpacing (final float spacing) throws IOException
  {
    writeOperand (spacing);
    writeOperator ((byte) 'T', (byte) 'c');
  }

  /**
   * Shows the given text at the location specified by the current text matrix.
   *
   * @param text
   *        The Unicode text to show.
   * @throws IOException
   *         If an io exception occurs.
   */
  public void showText (final String text) throws IOException
  {
    if (!m_bInTextMode)
      throw new IllegalStateException ("Must call beginText() before showText()");

    if (m_aFontStack.isEmpty ())
      throw new IllegalStateException ("Must call setFont() before showText()");

    final PDFont font = m_aFontStack.peek ();

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

    COSWriter.writeString (font.encode (text), m_aOS);
    write ((byte) ' ');
    writeOperator ((byte) 'T', (byte) 'j');
  }

  /**
   * Sets the text leading.
   *
   * @param leading
   *        The leading in unscaled text units.
   * @throws IOException
   *         If there is an error writing to the stream.
   */
  public void setLeading (final double leading) throws IOException
  {
    writeOperand ((float) leading);
    writeOperator ((byte) 'T', (byte) 'L');
  }

  /**
   * Move to the start of the next line of text. Requires the leading (see
   * {@link #setLeading}) to have been set.
   *
   * @throws IOException
   *         If there is an error writing to the stream.
   */
  public void newLine () throws IOException
  {
    if (!m_bInTextMode)
      throw new IllegalStateException ("Must call beginText() before newLine()");
    writeOperator ((byte) 'T', (byte) '*');
  }

  /**
   * The Td operator. Move to the start of the next line, offset from the start
   * of the current line by (tx, ty).
   *
   * @param tx
   *        The x translation.
   * @param ty
   *        The y translation.
   * @throws IOException
   *         If there is an error writing to the stream.
   * @throws IllegalStateException
   *         If the method was not allowed to be called at this time.
   */
  public void newLineAtOffset (final float tx, final float ty) throws IOException
  {
    if (!m_bInTextMode)
      throw new IllegalStateException ("Error: must call beginText() before newLineAtOffset()");
    writeOperand (tx);
    writeOperand (ty);
    writeOperator ((byte) 'T', (byte) 'd');
  }

  /**
   * The Tm operator. Sets the text matrix to the given values. A current text
   * matrix will be replaced with the new one.
   *
   * @param matrix
   *        the transformation matrix
   * @throws IOException
   *         If there is an error writing to the stream.
   * @throws IllegalStateException
   *         If the method was not allowed to be called at this time.
   */
  public void setTextMatrix (final Matrix matrix) throws IOException
  {
    if (!m_bInTextMode)
      throw new IllegalStateException ("Error: must call beginText() before setTextMatrix");
    writeAffineTransform (matrix.createAffineTransform ());
    writeOperator ((byte) 'T', (byte) 'm');
  }

  /**
   * Draw an image at the x,y coordinates, with the default size of the image.
   *
   * @param image
   *        The image to draw.
   * @param x
   *        The x-coordinate to draw the image.
   * @param y
   *        The y-coordinate to draw the image.
   * @throws IOException
   *         If there is an error writing to the stream.
   */
  public void drawImage (final PDImageXObject image, final float x, final float y) throws IOException
  {
    drawImage (image, x, y, image.getWidth (), image.getHeight ());
  }

  /**
   * Draw an image at the x,y coordinates, with the given size.
   *
   * @param image
   *        The image to draw.
   * @param x
   *        The x-coordinate to draw the image.
   * @param y
   *        The y-coordinate to draw the image.
   * @param width
   *        The width to draw the image.
   * @param height
   *        The height to draw the image.
   * @throws IOException
   *         If there is an error writing to the stream.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void drawImage (final PDImageXObject image,
                         final float x,
                         final float y,
                         final float width,
                         final float height) throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: drawImage is not allowed within a text block.");

    saveGraphicsState ();

    final AffineTransform transform = new AffineTransform (width, 0, 0, height, x, y);
    transform (new Matrix (transform));

    writeOperand (m_aResources.add (image));
    writeOperator ((byte) 'D', (byte) 'o');

    restoreGraphicsState ();
  }

  /**
   * Draw an inline image at the x,y coordinates, with the default size of the
   * image.
   *
   * @param inlineImage
   *        The inline image to draw.
   * @param x
   *        The x-coordinate to draw the inline image.
   * @param y
   *        The y-coordinate to draw the inline image.
   * @throws IOException
   *         If there is an error writing to the stream.
   */
  public void drawImage (final PDInlineImage inlineImage, final float x, final float y) throws IOException
  {
    drawImage (inlineImage, x, y, inlineImage.getWidth (), inlineImage.getHeight ());
  }

  /**
   * Draw an inline image at the x,y coordinates and a certain width and height.
   *
   * @param inlineImage
   *        The inline image to draw.
   * @param x
   *        The x-coordinate to draw the inline image.
   * @param y
   *        The y-coordinate to draw the inline image.
   * @param width
   *        The width of the inline image to draw.
   * @param height
   *        The height of the inline image to draw.
   * @throws IOException
   *         If there is an error writing to the stream.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void drawImage (final PDInlineImage inlineImage,
                         final float x,
                         final float y,
                         final float width,
                         final float height) throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: drawImage is not allowed within a text block.");

    saveGraphicsState ();
    transform (new Matrix (width, 0, 0, height, x, y));

    // create the image dictionary
    final StringBuilder sb = new StringBuilder ();
    sb.append ("BI");

    sb.append ("\n /W ");
    sb.append (inlineImage.getWidth ());

    sb.append ("\n /H ");
    sb.append (inlineImage.getHeight ());

    sb.append ("\n /CS ");
    sb.append ("/");
    sb.append (inlineImage.getColorSpace ().getName ());

    if (inlineImage.getDecode () != null && inlineImage.getDecode ().size () > 0)
    {
      sb.append ("\n /D ");
      sb.append ("[");
      for (final COSBase base : inlineImage.getDecode ())
      {
        sb.append (((COSNumber) base).intValue ());
        sb.append (" ");
      }
      sb.append ("]");
    }

    if (inlineImage.isStencil ())
    {
      sb.append ("\n /IM true");
    }

    sb.append ("\n /BPC ");
    sb.append (inlineImage.getBitsPerComponent ());

    // image dictionary
    write (sb.toString ());
    writeLine ();

    // binary data
    writeOperator ((byte) 'I', (byte) 'D');
    writeBytes (inlineImage.getData ());
    writeLine ();
    writeOperator ((byte) 'E', (byte) 'I');

    restoreGraphicsState ();
  }

  /**
   * Draws the given Form XObject at the current location.
   *
   * @param form
   *        Form XObject
   * @throws IOException
   *         if the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void drawForm (final PDFormXObject form) throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: drawForm is not allowed within a text block.");

    writeOperand (m_aResources.add (form));
    writeOperator ((byte) 'D', (byte) 'o');
  }

  /**
   * The cm operator. Concatenates the given matrix with the CTM.
   *
   * @param matrix
   *        the transformation matrix
   * @throws IOException
   *         If there is an error writing to the stream.
   */
  public void transform (final Matrix matrix) throws IOException
  {
    writeAffineTransform (matrix.createAffineTransform ());
    writeOperator ((byte) 'c', (byte) 'm');
  }

  /**
   * q operator. Saves the current graphics state.
   *
   * @throws IOException
   *         If an error occurs while writing to the stream.
   */
  public void saveGraphicsState () throws IOException
  {
    if (!m_aFontStack.isEmpty ())
      m_aFontStack.push (m_aFontStack.peek ());
    if (!m_aStrokingColorSpaceStack.isEmpty ())
      m_aStrokingColorSpaceStack.push (m_aStrokingColorSpaceStack.peek ());
    if (!m_aNonStrokingColorSpaceStack.isEmpty ())
      m_aNonStrokingColorSpaceStack.push (m_aNonStrokingColorSpaceStack.peek ());

    writeOperator ((byte) 'q');
  }

  /**
   * Q operator. Restores the current graphics state.
   *
   * @throws IOException
   *         If an error occurs while writing to the stream.
   */
  public void restoreGraphicsState () throws IOException
  {
    if (!m_aFontStack.isEmpty ())
      m_aFontStack.pop ();
    if (!m_aStrokingColorSpaceStack.isEmpty ())
      m_aStrokingColorSpaceStack.pop ();
    if (!m_aNonStrokingColorSpaceStack.isEmpty ())
      m_aNonStrokingColorSpaceStack.pop ();

    writeOperator ((byte) 'Q');
  }

  private COSName getName (final PDColorSpace colorSpace)
  {
    if (colorSpace instanceof PDDeviceGray || colorSpace instanceof PDDeviceRGB || colorSpace instanceof PDDeviceCMYK)
      return COSName.getPDFName (colorSpace.getName ());
    return m_aResources.add (colorSpace);
  }

  /**
   * Sets the stroking color and, if necessary, the stroking color space.
   *
   * @param color
   *        Color in a specific color space.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   */
  public void setStrokingColor (@Nonnull final PDColor color) throws IOException
  {
    if (m_aStrokingColorSpaceStack.isEmpty () || m_aStrokingColorSpaceStack.peek () != color.getColorSpace ())
    {
      writeOperand (getName (color.getColorSpace ()));
      writeOperator ((byte) 'C', (byte) 'S');

      if (m_aStrokingColorSpaceStack.isEmpty ())
        m_aStrokingColorSpaceStack.add (color.getColorSpace ());
      else
        m_aStrokingColorSpaceStack.set (m_aNonStrokingColorSpaceStack.size () - 1, color.getColorSpace ());
    }

    for (final float value : color.getComponents ())
      writeOperand (value);

    if (color.getColorSpace () instanceof PDPattern)
      writeOperand (color.getPatternName ());

    if (color.getColorSpace () instanceof PDPattern ||
        color.getColorSpace () instanceof PDSeparation ||
        color.getColorSpace () instanceof PDDeviceN ||
        color.getColorSpace () instanceof PDICCBased)
    {
      writeOperator ((byte) 'S', (byte) 'C', (byte) 'N');
    }
    else
    {
      writeOperator ((byte) 'S', (byte) 'C');
    }
  }

  /**
   * Set the stroking color using an AWT color. Conversion uses the default sRGB
   * color space.
   *
   * @param aColor
   *        The color to set.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   * @since 7.2.0
   */
  public void setStrokingColor (@Nonnull final PLColor aColor) throws IOException
  {
    setStrokingColor (aColor.getAsPDColor ());
  }

  /**
   * Set the stroking color in the DeviceRGB color space. Range is 0..255.
   *
   * @param r
   *        The red value
   * @param g
   *        The green value.
   * @param b
   *        The blue value.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   * @throws IllegalArgumentException
   *         If the parameters are invalid.
   */
  public void setStrokingColor (final int r, final int g, final int b) throws IOException
  {
    if (_isOutside255Interval (r) || _isOutside255Interval (g) || _isOutside255Interval (b))
      throw new IllegalArgumentException ("Parameters must be within 0..255, but are (" + r + "," + g + "," + b + ")");

    writeOperand (r / 255f);
    writeOperand (g / 255f);
    writeOperand (b / 255f);
    writeOperator ((byte) 'R', (byte) 'G');
  }

  /**
   * Set the stroking color in the DeviceCMYK color space. Range is 0..1
   *
   * @param c
   *        The cyan value.
   * @param m
   *        The magenta value.
   * @param y
   *        The yellow value.
   * @param k
   *        The black value.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   * @throws IllegalArgumentException
   *         If the parameters are invalid.
   */
  public void setStrokingColor (final float c, final float m, final float y, final float k) throws IOException
  {
    if (_isOutsideOneInterval (c) ||
        _isOutsideOneInterval (m) ||
        _isOutsideOneInterval (y) ||
        _isOutsideOneInterval (k))
    {
      throw new IllegalArgumentException ("Parameters must be within 0..1, but are (" +
                                          c +
                                          "," +
                                          m +
                                          "," +
                                          y +
                                          "," +
                                          k +
                                          ")");
    }
    writeOperand (c);
    writeOperand (m);
    writeOperand (y);
    writeOperand (k);
    writeOperator ((byte) 'K');
  }

  /**
   * Set the stroking color in the DeviceGray color space. Range is 0..1.
   *
   * @param g
   *        The gray value.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   * @throws IllegalArgumentException
   *         If the parameter is invalid.
   */
  public void setStrokingColor (final double g) throws IOException
  {
    if (_isOutsideOneInterval (g))
      throw new IllegalArgumentException ("Parameter must be within 0..1, but is " + g);

    writeOperand ((float) g);
    writeOperator ((byte) 'G');
  }

  /**
   * Sets the non-stroking color and, if necessary, the non-stroking color
   * space.
   *
   * @param color
   *        Color in a specific color space.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   */
  public void setNonStrokingColor (@Nonnull final PDColor color) throws IOException
  {
    if (m_aNonStrokingColorSpaceStack.isEmpty () || m_aNonStrokingColorSpaceStack.peek () != color.getColorSpace ())
    {
      writeOperand (getName (color.getColorSpace ()));
      writeOperator ((byte) 'c', (byte) 's');

      if (m_aNonStrokingColorSpaceStack.isEmpty ())
        m_aNonStrokingColorSpaceStack.add (color.getColorSpace ());
      else
        m_aNonStrokingColorSpaceStack.set (m_aNonStrokingColorSpaceStack.size () - 1, color.getColorSpace ());
    }

    for (final float value : color.getComponents ())
      writeOperand (value);

    if (color.getColorSpace () instanceof PDPattern)
      writeOperand (color.getPatternName ());

    if (color.getColorSpace () instanceof PDPattern ||
        color.getColorSpace () instanceof PDSeparation ||
        color.getColorSpace () instanceof PDDeviceN ||
        color.getColorSpace () instanceof PDICCBased)
    {
      writeOperator ((byte) 's', (byte) 'c', (byte) 'n');
    }
    else
    {
      writeOperator ((byte) 's', (byte) 'c');
    }
  }

  /**
   * Set the non-stroking color using an AWT color. Conversion uses the default
   * sRGB color space.
   *
   * @param aColor
   *        The color to set.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   * @since 7.2.0
   */
  public void setNonStrokingColor (@Nonnull final PLColor aColor) throws IOException
  {
    setNonStrokingColor (aColor.getAsPDColor ());
  }

  /**
   * Set the non-stroking color in the DeviceRGB color space. Range is 0..255.
   *
   * @param r
   *        The red value.
   * @param g
   *        The green value.
   * @param b
   *        The blue value.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   * @throws IllegalArgumentException
   *         If the parameters are invalid.
   */
  public void setNonStrokingColor (final int r, final int g, final int b) throws IOException
  {
    if (_isOutside255Interval (r) || _isOutside255Interval (g) || _isOutside255Interval (b))
      throw new IllegalArgumentException ("Parameters must be within 0..255, but are (" + r + "," + g + "," + b + ")");

    writeOperand (r / 255f);
    writeOperand (g / 255f);
    writeOperand (b / 255f);
    writeOperator ((byte) 'r', (byte) 'g');
  }

  /**
   * Set the non-stroking color in the DeviceCMYK color space. Range is 0..255.
   *
   * @param c
   *        The cyan value.
   * @param m
   *        The magenta value.
   * @param y
   *        The yellow value.
   * @param k
   *        The black value.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   * @throws IllegalArgumentException
   *         If the parameters are invalid.
   */
  public void setNonStrokingColor (final int c, final int m, final int y, final int k) throws IOException
  {
    if (_isOutside255Interval (c) ||
        _isOutside255Interval (m) ||
        _isOutside255Interval (y) ||
        _isOutside255Interval (k))
    {
      throw new IllegalArgumentException ("Parameters must be within 0..255, but are (" +
                                          c +
                                          "," +
                                          m +
                                          "," +
                                          y +
                                          "," +
                                          k +
                                          ")");
    }

    setNonStrokingColor (c / 255f, m / 255f, y / 255f, k / 255f);
  }

  /**
   * Set the non-stroking color in the DeviceRGB color space. Range is 0..1.
   *
   * @param c
   *        The cyan value.
   * @param m
   *        The magenta value.
   * @param y
   *        The yellow value.
   * @param k
   *        The black value.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   */
  public void setNonStrokingColor (final double c, final double m, final double y, final double k) throws IOException
  {
    if (_isOutsideOneInterval (c) ||
        _isOutsideOneInterval (m) ||
        _isOutsideOneInterval (y) ||
        _isOutsideOneInterval (k))
    {
      throw new IllegalArgumentException ("Parameters must be within 0..1, but are (" +
                                          c +
                                          "," +
                                          m +
                                          "," +
                                          y +
                                          "," +
                                          k +
                                          ")");
    }

    writeOperand ((float) c);
    writeOperand ((float) m);
    writeOperand ((float) y);
    writeOperand ((float) k);
    writeOperator ((byte) 'k');
  }

  /**
   * Set the non-stroking color in the DeviceGray color space. Range is 0..255.
   *
   * @param g
   *        The gray value.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   * @throws IllegalArgumentException
   *         If the parameter is invalid.
   */
  public void setNonStrokingColor (final int g) throws IOException
  {
    if (_isOutside255Interval (g))
      throw new IllegalArgumentException ("Parameter must be within 0..255, but is " + g);

    setNonStrokingColor (g / 255f);
  }

  /**
   * Set the non-stroking color in the DeviceGray color space. Range is 0..1.
   *
   * @param g
   *        The gray value.
   * @throws IOException
   *         If an IO error occurs while writing to the stream.
   * @throws IllegalArgumentException
   *         If the parameter is invalid.
   */
  public void setNonStrokingColor (final double g) throws IOException
  {
    if (_isOutsideOneInterval (g))
      throw new IllegalArgumentException ("Parameter must be within 0..1, but is " + g);

    writeOperand ((float) g);
    writeOperator ((byte) 'g');
  }

  /**
   * Add a rectangle to the current path.
   *
   * @param x
   *        The lower left x coordinate.
   * @param y
   *        The lower left y coordinate.
   * @param width
   *        The width of the rectangle.
   * @param height
   *        The height of the rectangle.
   * @throws IOException
   *         If the content stream could not be written.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void addRect (final float x, final float y, final float width, final float height) throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: addRect is not allowed within a text block.");

    writeOperand (x);
    writeOperand (y);
    writeOperand (width);
    writeOperand (height);
    writeOperator ((byte) 'r', (byte) 'e');
  }

  /**
   * Append a cubic Bézier curve to the current path. The curve extends from the
   * current point to the point (x3, y3), using (x1, y1) and (x2, y2) as the
   * Bézier control points.
   *
   * @param x1
   *        x coordinate of the point 1
   * @param y1
   *        y coordinate of the point 1
   * @param x2
   *        x coordinate of the point 2
   * @param y2
   *        y coordinate of the point 2
   * @param x3
   *        x coordinate of the point 3
   * @param y3
   *        y coordinate of the point 3
   * @throws IOException
   *         If the content stream could not be written.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void curveTo (final float x1, final float y1, final float x2, final float y2, final float x3, final float y3)
                                                                                                                       throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: curveTo is not allowed within a text block.");

    writeOperand (x1);
    writeOperand (y1);
    writeOperand (x2);
    writeOperand (y2);
    writeOperand (x3);
    writeOperand (y3);
    writeOperator ((byte) 'c');
  }

  /**
   * Append a cubic Bézier curve to the current path. The curve extends from the
   * current point to the point (x3, y3), using the current point and (x2, y2)
   * as the Bézier control points.
   *
   * @param x2
   *        x coordinate of the point 2
   * @param y2
   *        y coordinate of the point 2
   * @param x3
   *        x coordinate of the point 3
   * @param y3
   *        y coordinate of the point 3
   * @throws IllegalStateException
   *         If the method was called within a text block.
   * @throws IOException
   *         If the content stream could not be written.
   */
  public void curveTo2 (final float x2, final float y2, final float x3, final float y3) throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: curveTo2 is not allowed within a text block.");

    writeOperand (x2);
    writeOperand (y2);
    writeOperand (x3);
    writeOperand (y3);
    writeOperator ((byte) 'v');
  }

  /**
   * Append a cubic Bézier curve to the current path. The curve extends from the
   * current point to the point (x3, y3), using (x1, y1) and (x3, y3) as the
   * Bézier control points.
   *
   * @param x1
   *        x coordinate of the point 1
   * @param y1
   *        y coordinate of the point 1
   * @param x3
   *        x coordinate of the point 3
   * @param y3
   *        y coordinate of the point 3
   * @throws IOException
   *         If the content stream could not be written.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void curveTo1 (final float x1, final float y1, final float x3, final float y3) throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: curveTo1 is not allowed within a text block.");

    writeOperand (x1);
    writeOperand (y1);
    writeOperand (x3);
    writeOperand (y3);
    writeOperator ((byte) 'y');
  }

  /**
   * Move the current position to the given coordinates.
   *
   * @param x
   *        The x coordinate.
   * @param y
   *        The y coordinate.
   * @throws IOException
   *         If the content stream could not be written.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void moveTo (final float x, final float y) throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: moveTo is not allowed within a text block.");

    writeOperand (x);
    writeOperand (y);
    writeOperator ((byte) 'm');
  }

  /**
   * Draw a line from the current position to the given coordinates.
   *
   * @param x
   *        The x coordinate.
   * @param y
   *        The y coordinate.
   * @throws IOException
   *         If the content stream could not be written.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void lineTo (final float x, final float y) throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: lineTo is not allowed within a text block.");

    writeOperand (x);
    writeOperand (y);
    writeOperator ((byte) 'l');
  }

  /**
   * Stroke the path.
   *
   * @throws IOException
   *         If the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void stroke () throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: stroke is not allowed within a text block.");

    writeOperator ((byte) 'S');
  }

  /**
   * Close and stroke the path.
   *
   * @throws IOException
   *         If the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void closeAndStroke () throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: closeAndStroke is not allowed within a text block.");

    writeOperator ((byte) 's');
  }

  /**
   * Fills the path using the nonzero winding rule.
   *
   * @throws IOException
   *         If the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void fill () throws IOException
  {
    if (m_bInTextMode)
      throw new IllegalStateException ("Error: fill is not allowed within a text block.");

    writeOperator ((byte) 'f');
  }

  /**
   * Fills the path using the even-odd winding rule.
   *
   * @throws IOException
   *         If the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void fillEvenOdd () throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: fill is not allowed within a text block.");
    }
    writeOperator ((byte) 'f', (byte) '*');
  }

  /**
   * Fills the clipping area with the given shading.
   *
   * @param shading
   *        Shading resource
   * @throws IOException
   *         If the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void shadingFill (final PDShading shading) throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: shadingFill is not allowed within a text block.");
    }

    writeOperand (m_aResources.add (shading));
    writeOperator ((byte) 's', (byte) 'h');
  }

  /**
   * Closes the current subpath.
   *
   * @throws IOException
   *         If the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void closePath () throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: closePath is not allowed within a text block.");
    }
    writeOperator ((byte) 'h');
  }

  /**
   * Intersects the current clipping path with the current path, using the
   * nonzero rule.
   *
   * @throws IOException
   *         If the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void clip () throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: clip is not allowed within a text block.");
    }
    writeOperator ((byte) 'W');

    // end path without filling or stroking
    writeOperator ((byte) 'n');
  }

  /**
   * Intersects the current clipping path with the current path, using the
   * even-odd rule.
   *
   * @throws IOException
   *         If the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void clipEvenOdd () throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: clipEvenOdd is not allowed within a text block.");
    }
    writeOperator ((byte) 'W', (byte) '*');

    // end path without filling or stroking
    writeOperator ((byte) 'n');
  }

  /**
   * Set line width to the given value.
   *
   * @param lineWidth
   *        The width which is used for drawing.
   * @throws IOException
   *         If the content stream could not be written
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void setLineWidth (final float lineWidth) throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: setLineWidth is not allowed within a text block.");
    }
    writeOperand (lineWidth);
    writeOperator ((byte) 'w');
  }

  /**
   * Set the line join style.
   *
   * @param lineJoinStyle
   *        0 for miter join, 1 for round join, and 2 for bevel join.
   * @throws IOException
   *         If the content stream could not be written.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   * @throws IllegalArgumentException
   *         If the parameter is not a valid line join style.
   */
  public void setLineJoinStyle (final int lineJoinStyle) throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: setLineJoinStyle is not allowed within a text block.");
    }
    if (lineJoinStyle >= 0 && lineJoinStyle <= 2)
    {
      writeOperand (lineJoinStyle);
      writeOperator ((byte) 'j');
    }
    else
    {
      throw new IllegalArgumentException ("Error: unknown value for line join style");
    }
  }

  /**
   * Set the line cap style.
   *
   * @param lineCapStyle
   *        0 for butt cap, 1 for round cap, and 2 for projecting square cap.
   * @throws IOException
   *         If the content stream could not be written.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   * @throws IllegalArgumentException
   *         If the parameter is not a valid line cap style.
   */
  public void setLineCapStyle (final int lineCapStyle) throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: setLineCapStyle is not allowed within a text block.");
    }
    if (lineCapStyle >= 0 && lineCapStyle <= 2)
    {
      writeOperand (lineCapStyle);
      writeOperator ((byte) 'J');
    }
    else
    {
      throw new IllegalArgumentException ("Error: unknown value for line cap style");
    }
  }

  /**
   * Set the line dash pattern.
   *
   * @param pattern
   *        The pattern array
   * @param phase
   *        The phase of the pattern
   * @throws IOException
   *         If the content stream could not be written.
   * @throws IllegalStateException
   *         If the method was called within a text block.
   */
  public void setLineDashPattern (final float [] pattern, final float phase) throws IOException
  {
    if (m_bInTextMode)
    {
      throw new IllegalStateException ("Error: setLineDashPattern is not allowed within a text block.");
    }
    write ((byte) '[');
    for (final float value : pattern)
    {
      writeOperand (value);
    }
    write ((byte) ']', (byte) ' ');
    writeOperand (phase);
    writeOperator ((byte) 'd');
  }

  /**
   * Begin a marked content sequence.
   *
   * @param tag
   *        the tag
   * @throws IOException
   *         If the content stream could not be written
   */
  public void beginMarkedContent (final COSName tag) throws IOException
  {
    writeOperand (tag);
    writeOperator ((byte) 'B', (byte) 'M', (byte) 'C');
  }

  /**
   * Begin a marked content sequence with a reference to an entry in the page
   * resources' Properties dictionary.
   *
   * @param tag
   *        the tag
   * @param propertyList
   *        property list
   * @throws IOException
   *         If the content stream could not be written
   */
  public void beginMarkedContent (final COSName tag, final PDPropertyList propertyList) throws IOException
  {
    writeOperand (tag);
    writeOperand (m_aResources.add (propertyList));
    writeOperator ((byte) 'B', (byte) 'D', (byte) 'C');
  }

  /**
   * End a marked content sequence.
   *
   * @throws IOException
   *         If the content stream could not be written
   */
  public void endMarkedContent () throws IOException
  {
    writeOperator ((byte) 'E', (byte) 'M', (byte) 'C');
  }

  /**
   * Set an extended graphics state.
   *
   * @param state
   *        The extended graphics state.
   * @throws IOException
   *         If the content stream could not be written.
   */
  public void setGraphicsStateParameters (final PDExtendedGraphicsState state) throws IOException
  {
    writeOperand (m_aResources.add (state));
    writeOperator ((byte) 'g', (byte) 's');
  }

  /**
   * Writes a real real to the content stream.
   *
   * @param real
   *        the value to be written
   * @throws IOException
   *         In case of IO error
   */
  protected void writeOperand (final float real) throws IOException
  {
    final int byteCount = NumberFormatUtil.formatFloatFast (real,
                                                            m_aFormatDecimal.getMaximumFractionDigits (),
                                                            m_aFormatBuffer);

    if (byteCount == -1)
    {
      // Fast formatting failed
      write (m_aFormatDecimal.format (real));
    }
    else
    {
      m_aOS.write (m_aFormatBuffer, 0, byteCount);
    }
    m_aOS.write (' ');
  }

  /**
   * Writes a real number to the content stream.
   *
   * @param integer
   *        the value to be written
   * @throws IOException
   *         In case of IO error
   */
  protected void writeOperand (final int integer) throws IOException
  {
    write (m_aFormatDecimal.format (integer));
    m_aOS.write (' ');
  }

  /**
   * Writes a COSName to the content stream.
   *
   * @param name
   *        the value to be written
   * @throws IOException
   *         In case of IO error
   */
  protected void writeOperand (final COSName name) throws IOException
  {
    name.writePDF (m_aOS);
    m_aOS.write (' ');
  }

  /**
   * Writes a string to the content stream as ASCII.
   *
   * @param text
   *        the value to be written
   * @throws IOException
   *         In case of IO error
   */
  public final void writeOperator (final byte... text) throws IOException
  {
    m_aOS.write (text);
    m_aOS.write ('\n');
  }

  /**
   * Writes a string to the content stream as ASCII.
   *
   * @param text
   *        the value to be written
   * @throws IOException
   *         In case of IO error
   */
  private void write (final String text) throws IOException
  {
    write (text.getBytes (StandardCharsets.US_ASCII));
  }

  /**
   * Writes a string to the content stream as ASCII.
   *
   * @param text
   *        the value to be written
   * @throws IOException
   *         In case of IO error
   */
  protected void write (final byte... text) throws IOException
  {
    m_aOS.write (text);
  }

  /**
   * Writes a string to the content stream as ASCII.
   *
   * @throws IOException
   *         In case of IO error
   */
  protected void writeLine () throws IOException
  {
    m_aOS.write ('\n');
  }

  /**
   * Writes binary data to the content stream.
   *
   * @param data
   *        the value to be written
   * @throws IOException
   *         In case of IO error
   */
  protected void writeBytes (final byte [] data) throws IOException
  {
    m_aOS.write (data);
  }

  /**
   * Writes an AffineTransform to the content stream as an array.
   *
   * @param transform
   *        the transform to use
   * @throws IOException
   *         In case of IO error
   */
  private void writeAffineTransform (final AffineTransform transform) throws IOException
  {
    final double [] values = new double [6];
    transform.getMatrix (values);
    for (final double v : values)
    {
      writeOperand ((float) v);
    }
  }

  /**
   * Close the content stream. This must be called when you are done with this
   * object.
   *
   * @throws IOException
   *         If the underlying stream has a problem being written to.
   */
  @Override
  public void close () throws IOException
  {
    m_aOS.close ();
  }

  private static boolean _isOutside255Interval (final int val)
  {
    return val < 0 || val > 255;
  }

  private static boolean _isOutsideOneInterval (final double val)
  {
    return val < 0 || val > 1;
  }
}
