/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.element;

import org.junit.Rule;
import org.junit.rules.TestRule;

import com.helger.commons.mock.DebugModeTestRule;

public class PDFFontTest
{
  @Rule
  public TestRule m_aRule = new DebugModeTestRule ();

  // public static final class E extends Encoding
  // {
  // public E ()
  // {
  // // Map all single char entries
  // for (final Map.Entry <String, String> aEntry : getCharacterToNameMap
  // ().entrySet ())
  // {
  // final String sChars = aEntry.getKey ();
  // final String sName = aEntry.getValue ();
  // if (sChars.length () == 1)
  // {
  // final char c = sChars.charAt (0);
  // boolean bEquals = false;
  // try
  // {
  // final String sStdName = StandardEncoding.INSTANCE.getName (c);
  // bEquals = sName.equals (sStdName);
  // }
  // catch (final IOException ex)
  // {}
  // if (!bEquals)
  // addCharacterEncoding (c, sName);
  // }
  // }
  // }
  //
  // public COSBase getCOSObject ()
  // {
  // final COSDictionary ret = new COSDictionary ();
  // ret.setItem (COSName.TYPE, COSName.ENCODING);
  // if (false)
  // ret.setItem (COSName.BASE_ENCODING, COSName.STANDARD_ENCODING);
  // final COSArray aArray = new COSArray ();
  // int nLastCode = Integer.MIN_VALUE;
  // for (final Map.Entry <Integer, String> aEntry :
  // ContainerHelper.newSortedMap (codeToName).entrySet ())
  // {
  // final int nCode = aEntry.getKey ().intValue ();
  // if (nCode != nLastCode + 1)
  // aArray.add (COSInteger.get (nCode));
  // aArray.add (COSName.getPDFName (aEntry.getValue ()));
  // nLastCode = nCode;
  // }
  // ret.setItem (COSName.DIFFERENCES, aArray);
  //
  // return ret;
  // }
  // }
  //
  // private static final class EncodingIdentityH extends Encoding
  // {
  // public COSBase getCOSObject ()
  // {
  // return COSName.IDENTITY_H;
  // }
  // }
  //
  // @Test
  // public void testEuroSign () throws Exception
  // {
  // String s =
  // "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";
  // s = "XXX¥©Ω∞XXX";
  // if (false)
  // s = "ABC™↓↑αΠΦ€DEF";
  //
  // if (false)
  // s = FakeWinAnsiHelper.convertJavaStringToWinAnsi2 (s);
  //
  // Encoding aEncoding = true ? new EncodingIdentityH () :
  // WinAnsiEncoding.INSTANCE;
  //
  // if (false)
  // {
  // final COSArray aDiff = new COSArray ();
  // aDiff.add (COSInteger.get (0x20ac));
  // aDiff.add (COSName.getPDFName ("Euro"));
  // final COSDictionary aFontDict = new COSDictionary ();
  // aFontDict.setItem (COSName.BASE_ENCODING, COSName.WIN_ANSI_ENCODING);
  // aFontDict.setItem (COSName.DIFFERENCES, aDiff);
  // aEncoding = new DictionaryEncoding (aFontDict);
  // }
  //
  // final PDDocument aDummy = new PDDocument ();
  // final PDStream fontStream = new PDStream (aDummy,
  // FileSystemResource.getInputStream (new File
  // ("src/test/resources/decomp_stream_0x8FE.ttf")),
  // false);
  // fontStream.getStream ().setInt (COSName.LENGTH1, fontStream.getByteArray
  // ().length);
  // fontStream.addCompression ();
  // final PDTrueTypeFont aFont = PDTrueTypeFont.loadTTF (fontStream,
  // aEncoding);
  //
  // final COSDictionary aCIDSystemInfo = new COSDictionary ();
  // aCIDSystemInfo.setString (COSName.REGISTRY, "Adobe");
  // aCIDSystemInfo.setString (COSName.ORDERING, "Identity");
  // aCIDSystemInfo.setInt (COSName.SUPPLEMENT, 0);
  //
  // final COSArray aW = new COSArray ();
  // {
  // // Build condensed
  // final int nDefaultWidth = aFont.getDefaultWidth ();
  // final int nFirst = aFont.getFirstChar ();
  // final int nLast = aFont.getLastChar ();
  // final List <Integer> aWidths = aFont.getWidths ();
  // COSArray aNestedArray = null;
  // for (int ch = nFirst; ch <= nLast; ++ch)
  // {
  // final int nWidth = aWidths.get (ch - nFirst).intValue ();
  // if (nWidth != nDefaultWidth)
  // {
  // if (aNestedArray == null)
  // {
  // // Start index
  // aW.add (COSInteger.get (ch));
  // aNestedArray = new COSArray ();
  // aW.add (aNestedArray);
  // }
  //
  // // Add to existing nested array
  // aNestedArray.add (COSInteger.get (nWidth));
  // }
  // else
  // {
  // // Start a new array on the next non-default width
  // aNestedArray = null;
  // }
  // }
  // }
  //
  // final COSDictionary aCIDFont = new COSDictionary ();
  // aCIDFont.setItem (COSName.TYPE, COSName.FONT);
  // aCIDFont.setName (COSName.BASE_FONT, aFont.getBaseFont ());
  // aCIDFont.setItem (COSName.SUBTYPE, COSName.CID_FONT_TYPE2);
  // aCIDFont.setItem (COSName.CID_TO_GID_MAP, COSName.IDENTITY);
  // aCIDFont.setInt (COSName.DW, aFont.getDefaultWidth ());
  // aCIDFont.setItem (COSName.CIDSYSTEMINFO, aCIDSystemInfo);
  // aCIDFont.setItem (COSName.FONT_DESC, ((PDFontDescriptorDictionary)
  // aFont.getFontDescriptor ()).getCOSDictionary ());
  // aCIDFont.setItem (COSName.W, aW);
  //
  // String sCID = "/CIDInit /ProcSet findresource begin\n"
  // + "12 dict begin\n"
  // + "begincmap\n"
  // + "/CIDSystemInfo\n"
  // + "<< /Registry (Adobe)\n"
  // + "/Ordering (UCS)\n"
  // + "/Supplement 0\n"
  // + ">> def\n"
  // + "/CMapName /Adobe-Identity-UCS def\n"
  // + "/CMapType 2 def\n";
  //
  // final TreeMap <Integer, Integer> aG2CMap = new TreeMap <Integer, Integer>
  // ();
  // {
  // final int [] aG2C = aFont.getUnicodeMap ().getGlyphIdToCharacterCode ();
  // int nGID = 0;
  // for (final int nCID : aG2C)
  // {
  // if (nCID != 0)
  // aG2CMap.put (Integer.valueOf (nGID), Integer.valueOf (nCID));
  // ++nGID;
  // }
  // }
  // final boolean bUseC2GMap = !aG2CMap.isEmpty ();
  // if (bUseC2GMap)
  // {
  // if (false)
  // {
  // final List <String> aRanges = new ArrayList <String> ();
  // int nLastStart = -1;
  // int nLastValue = -1;
  // for (final Integer aKey : aG2CMap.values ())
  // {
  // final int nKey = aKey.intValue ();
  // if (nLastStart == -1)
  // {
  // nLastStart = nKey;
  // nLastValue = nKey;
  // }
  // else
  // if (nKey == nLastValue + 1)
  // {
  // nLastValue = nKey;
  // }
  // else
  // {
  // final String sRange = "<" +
  // StringHelper.getHexStringLeadingZero (nLastStart, 4) +
  // "> <" +
  // StringHelper.getHexStringLeadingZero (nLastValue, 4) +
  // ">\n";
  // aRanges.add (sRange);
  // nLastStart = nKey;
  // nLastValue = nKey;
  // }
  // }
  //
  // if (nLastStart > -1)
  // {
  // final String sRange = "<" +
  // StringHelper.getHexStringLeadingZero (nLastStart, 4) +
  // "> <" +
  // StringHelper.getHexStringLeadingZero (nLastValue, 4) +
  // ">\n";
  // aRanges.add (sRange);
  // }
  //
  // sCID += aRanges.size () + " begincodespacerange\n";
  // for (final String sRange : aRanges)
  // sCID += sRange;
  // sCID += "endcodespacerange\n";
  // }
  // else
  // {
  // final int nFirstChar = Collections.min (aG2CMap.values ()).intValue ();
  // final int nLastChar = Collections.max (aG2CMap.values ()).intValue ();
  // sCID += "1 begincodespacerange\n" +
  // "<" +
  // StringHelper.getHexStringLeadingZero (nFirstChar, 4) +
  // "> <" +
  // StringHelper.getHexStringLeadingZero (nLastChar, 4) +
  // ">\n" +
  // "endcodespacerange\n";
  // }
  // }
  // else
  // {
  // // Identity mapping
  // sCID += "1 begincodespacerange\n" + "<0000> <FFFF>\n" +
  // "endcodespacerange\n";
  // }
  //
  // if (bUseC2GMap)
  // {
  // sCID += aG2CMap.size () + " beginbfchar\n";
  // for (final Map.Entry <Integer, Integer> aEntry : aG2CMap.entrySet ())
  // {
  // final int nGID = aEntry.getKey ().intValue ();
  // final int nCID = aEntry.getValue ().intValue ();
  // sCID += "<" +
  // StringHelper.getHexStringLeadingZero (nGID, 4) +
  // "> <" +
  // StringHelper.getHexStringLeadingZero (nCID, 4) +
  // ">\n";
  // }
  // sCID += "endbfchar\n";
  // }
  // else
  // {
  // // Identity mapping
  // sCID += "2 beginbfrange\n" + "<00> <FF> <00>\n" + "<0100> <FFFF> <0100>\n"
  // + "endbfrange\n";
  // }
  //
  // sCID += "endcmap\n" + "CMapName currentdict /CMap defineresource pop\n" +
  // "end\n" + "end";
  //
  // if (false)
  // sCID = StreamUtils.getAllBytesAsString (new ClassPathResource
  // ("/org/apache/pdfbox/resources/cmap/Identity-H"),
  // CCharset.CHARSET_ISO_8859_1_OBJ);
  //
  // if (false)
  // {
  // final CMap aMap = new CMapParser ().parse ("Inline", new StringInputStream
  // (sCID));
  // System.out.println (aMap);
  // }
  //
  // final byte [] aBytes = CharsetManager.getAsBytes (sCID,
  // CCharset.CHARSET_ISO_8859_1_OBJ);
  // final COSStream aToUnicode = new COSStream (new RandomAccessBuffer ());
  // final OutputStream out = aToUnicode.createUnfilteredStream ();
  // out.write (aBytes);
  // out.close ();
  // if (false)
  // aToUnicode.setFilters (COSName.FLATE_DECODE);
  //
  // final COSArray aDescendant = new COSArray ();
  // aDescendant.add (aCIDFont);
  //
  // final COSDictionary a0Font = new COSDictionary ();
  // a0Font.setItem (COSName.TYPE, COSName.FONT);
  // a0Font.setItem (COSName.SUBTYPE, COSName.TYPE0);
  // a0Font.setName (COSName.BASE_FONT, aFont.getBaseFont ());
  // a0Font.setItem (COSName.ENCODING, COSName.IDENTITY_H);
  // a0Font.setItem (COSName.DESCENDANT_FONTS, aDescendant);
  // a0Font.setItem (COSName.TO_UNICODE, aToUnicode);
  //
  // aDummy.close ();
  //
  // final FontSpec r10 = new FontSpec (new PDFFont (PDFontFactory.createFont
  // (a0Font)), 10);
  //
  // final PLPageSet aPS1 = new PLPageSet (PDPage.PAGE_SIZE_A4).setMargin (30);
  // final PLTable aTable = PLTable.createWithPercentage (50, 50);
  // final PLHBox aHBox = new PLHBox ();
  // aHBox.addColumn (new PLText (s, r10).setBorder (new BorderStyleSpec
  // (Color.RED)), WidthSpec.abs (180));
  // aTable.addRow (aHBox);
  // aPS1.addElement (aTable);
  //
  // final PageLayoutPDF aPageLayout = new PageLayoutPDF ().setDebug (true);
  // aPageLayout.addPageSet (aPS1);
  //
  // final File aFile = new File ("pdf/test-font.pdf");
  // aPageLayout.renderTo (new FileOutputStream (aFile));
  //
  // // render the pages
  // final PDDocument x = PDDocument.load (aFile);
  // final int numPages = x.getNumberOfPages ();
  // final PDFRenderer renderer = new PDFRenderer (x);
  // int dpi;
  // try
  // {
  // dpi = Toolkit.getDefaultToolkit ().getScreenResolution ();
  // }
  // catch (final HeadlessException e)
  // {
  // dpi = 96;
  // }
  // for (int i = 0; i < numPages; i++)
  // {
  // final BufferedImage image = renderer.renderImageWithDPI (i, dpi,
  // ImageType.RGB);
  // final String fileName = "pdf/image-" + (i + 1) + ".png";
  // ImageIOUtil.writeImage (image, fileName, dpi);
  // }
  // x.close ();
  // System.out.println ("Done");
  // }
}
