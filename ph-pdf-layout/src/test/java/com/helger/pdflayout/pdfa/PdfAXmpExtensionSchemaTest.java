/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.pdflayout.pdfa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAExtensionSchema;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.xml.DomXmpParser;
import org.apache.xmpbox.xml.XmpSerializer;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.collection.commons.ICommonsList;

/**
 * Test class for class {@link PdfAXmpExtensionSchema}.
 *
 * @author Philip Helger
 */
public final class PdfAXmpExtensionSchemaTest
{
  private static final String NS = "urn:example:test:1p0#";

  @Test
  public void testBasic ()
  {
    final PdfAXmpExtensionSchema aSchema = new PdfAXmpExtensionSchema (NS, "ext", "Test schema");
    assertEquals (NS, aSchema.getNamespaceURI ());
    assertEquals ("ext", aSchema.getPrefix ());
    assertEquals ("Test schema", aSchema.getSchemaDescription ());
    assertTrue (aSchema.getAllProperties ().isEmpty ());

    aSchema.addTextProperty ("Version", "The version", "1.0")
           .addProperty (new PdfAXmpExtensionSchema.Property ("Kind",
                                                              PdfAXmpExtensionSchema.VALUE_TYPE_TEXT,
                                                              PdfAXmpExtensionSchema.CATEGORY_INTERNAL,
                                                              "The kind",
                                                              "INVOICE"));
    final ICommonsList <PdfAXmpExtensionSchema.Property> aProps = aSchema.getAllProperties ();
    assertEquals (2, aProps.size ());
    assertEquals ("Version", aProps.get (0).name ());
    assertEquals (PdfAXmpExtensionSchema.VALUE_TYPE_TEXT, aProps.get (0).valueType ());
    assertEquals (PdfAXmpExtensionSchema.CATEGORY_EXTERNAL, aProps.get (0).category ());
    assertEquals ("The version", aProps.get (0).description ());
    assertEquals ("1.0", aProps.get (0).value ());
    assertEquals (PdfAXmpExtensionSchema.CATEGORY_INTERNAL, aProps.get (1).category ());

    // Returns a copy
    aSchema.getAllProperties ().clear ();
    assertEquals (2, aSchema.getAllProperties ().size ());
    assertNotNull (aSchema.toString ());
  }

  @Test
  public void testInvalid ()
  {
    try
    {
      new PdfAXmpExtensionSchema ("", "ext", "desc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new PdfAXmpExtensionSchema.Property ("", "Text", "external", "desc", "v");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new PdfAXmpExtensionSchema (NS, "ext", "desc").addTextProperty ("Name", "desc", "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testAddToMetadata () throws Exception
  {
    final XMPMetadata aXmp = XMPMetadata.createXMPMetadata ();
    new PdfAXmpExtensionSchema (NS, "ext", "Test schema").addTextProperty ("Version", "The version", "1.0")
                                                         .addTextProperty ("Kind", "The kind", "INVOICE")
                                                         .addToMetadata (aXmp);
    // A second schema reuses the existing extension schema
    new PdfAXmpExtensionSchema ("urn:example:other#", "oth", "Other schema").addTextProperty ("Name", "The name", "x")
                                                                           .addToMetadata (aXmp);

    // The values
    final XMPSchema aValues = aXmp.getSchema (NS);
    assertNotNull (aValues);
    assertEquals ("1.0", aValues.getUnqualifiedTextPropertyValue ("Version"));
    assertEquals ("INVOICE", aValues.getUnqualifiedTextPropertyValue ("Kind"));

    // The descriptions
    final PDFAExtensionSchema aExtension = aXmp.getPDFExtensionSchema ();
    assertNotNull (aExtension);
    final ArrayProperty aSchemas = aExtension.getSchemasProperty ();
    assertNotNull (aSchemas);
    assertEquals (2, aSchemas.getAllProperties ().size ());

    // Serialize and parse again
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    new XmpSerializer ().serialize (aXmp, aBAOS, true);
    final String sXmp = aBAOS.getAsString (StandardCharsets.UTF_8);
    assertTrue (sXmp, sXmp.contains ("<pdfaSchema:namespaceURI>" + NS + "</pdfaSchema:namespaceURI>"));
    assertTrue (sXmp, sXmp.contains ("<pdfaProperty:name>Version</pdfaProperty:name>"));
    assertTrue (sXmp, sXmp.contains ("<ext:Version>1.0</ext:Version>"));

    final XMPMetadata aParsed = new DomXmpParser ().parse (new NonBlockingByteArrayInputStream (aBAOS.toByteArray ()));
    assertEquals ("INVOICE", aParsed.getSchema (NS).getUnqualifiedTextPropertyValue ("Kind"));
  }
}
