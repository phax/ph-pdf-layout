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

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAExtensionSchema;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PDFAPropertyType;
import org.apache.xmpbox.type.PDFASchemaType;
import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * A custom XMP schema with text properties, together with the PDF/A extension schema description
 * (<code>pdfaExtension:schemas</code>) that PDF/A requires for every schema that is not predefined.
 * Add it with {@link com.helger.pdflayout.PageLayoutPDF#addXmpExtensionSchema(PdfAXmpExtensionSchema)}.
 *
 * @author Philip Helger
 * @since 8.4.0
 */
@NotThreadSafe
public final class PdfAXmpExtensionSchema
{
  /**
   * A single text property of the schema, with its description and value.
   *
   * @param name
   *        The local name of the property. May neither be <code>null</code> nor empty.
   * @param valueType
   *        The XMP value type, e.g. <code>Text</code>. May neither be <code>null</code> nor empty.
   * @param category
   *        <code>internal</code> or <code>external</code>. May neither be <code>null</code> nor
   *        empty.
   * @param description
   *        The description of the property. May neither be <code>null</code> nor empty.
   * @param value
   *        The value of the property. May neither be <code>null</code> nor empty.
   */
  public record Property (@NonNull @Nonempty String name,
                          @NonNull @Nonempty String valueType,
                          @NonNull @Nonempty String category,
                          @NonNull @Nonempty String description,
                          @NonNull @Nonempty String value)
  {
    /**
     * Constructor with validation.
     */
    public Property
    {
      ValueEnforcer.notEmpty (name, "Name");
      ValueEnforcer.notEmpty (valueType, "ValueType");
      ValueEnforcer.notEmpty (category, "Category");
      ValueEnforcer.notEmpty (description, "Description");
      ValueEnforcer.notEmpty (value, "Value");
    }
  }

  /** The value type of text properties */
  public static final String VALUE_TYPE_TEXT = "Text";
  /** The category of properties that are visible to the user */
  public static final String CATEGORY_EXTERNAL = "external";
  /** The category of properties that are only meant for processing */
  public static final String CATEGORY_INTERNAL = "internal";

  private final String m_sNamespaceURI;
  private final String m_sPrefix;
  private final String m_sSchemaDescription;
  private final ICommonsList <Property> m_aProperties = new CommonsArrayList <> ();

  /**
   * Constructor
   *
   * @param sNamespaceURI
   *        The namespace URI of the schema. May neither be <code>null</code> nor empty.
   * @param sPrefix
   *        The preferred namespace prefix. May neither be <code>null</code> nor empty.
   * @param sSchemaDescription
   *        The description of the schema. May neither be <code>null</code> nor empty.
   */
  public PdfAXmpExtensionSchema (@NonNull @Nonempty final String sNamespaceURI,
                                 @NonNull @Nonempty final String sPrefix,
                                 @NonNull @Nonempty final String sSchemaDescription)
  {
    ValueEnforcer.notEmpty (sNamespaceURI, "NamespaceURI");
    ValueEnforcer.notEmpty (sPrefix, "Prefix");
    ValueEnforcer.notEmpty (sSchemaDescription, "SchemaDescription");
    m_sNamespaceURI = sNamespaceURI;
    m_sPrefix = sPrefix;
    m_sSchemaDescription = sSchemaDescription;
  }

  /**
   * @return The namespace URI of the schema. Never <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getNamespaceURI ()
  {
    return m_sNamespaceURI;
  }

  /**
   * @return The preferred namespace prefix. Never <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getPrefix ()
  {
    return m_sPrefix;
  }

  /**
   * @return The description of the schema. Never <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getSchemaDescription ()
  {
    return m_sSchemaDescription;
  }

  /**
   * @return A copy of all properties in the order they were added. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <Property> getAllProperties ()
  {
    return m_aProperties.getClone ();
  }

  /**
   * Add a property.
   *
   * @param aProperty
   *        The property to add. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PdfAXmpExtensionSchema addProperty (@NonNull final Property aProperty)
  {
    ValueEnforcer.notNull (aProperty, "Property");
    m_aProperties.add (aProperty);
    return this;
  }

  /**
   * Add an external text property.
   *
   * @param sName
   *        The local name of the property. May neither be <code>null</code> nor empty.
   * @param sDescription
   *        The description of the property. May neither be <code>null</code> nor empty.
   * @param sValue
   *        The value of the property. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public PdfAXmpExtensionSchema addTextProperty (@NonNull @Nonempty final String sName,
                                                 @NonNull @Nonempty final String sDescription,
                                                 @NonNull @Nonempty final String sValue)
  {
    return addProperty (new Property (sName, VALUE_TYPE_TEXT, CATEGORY_EXTERNAL, sDescription, sValue));
  }

  /**
   * Add the values of this schema and its PDF/A extension schema description to the provided XMP
   * metadata.
   *
   * @param aXmpMetadata
   *        The metadata to modify. May not be <code>null</code>.
   */
  public void addToMetadata (@NonNull final XMPMetadata aXmpMetadata)
  {
    ValueEnforcer.notNull (aXmpMetadata, "XmpMetadata");

    // The values
    final XMPSchema aSchema = aXmpMetadata.createAndAddDefaultSchema (m_sPrefix, m_sNamespaceURI);
    for (final Property aProperty : m_aProperties)
      aSchema.setTextPropertyValue (aProperty.name (), aProperty.value ());

    // The description of the schema
    PDFAExtensionSchema aExtension = aXmpMetadata.getPDFExtensionSchema ();
    if (aExtension == null)
      aExtension = aXmpMetadata.createAndAddPDFAExtensionSchemaWithDefaultNS ();

    final PDFASchemaType aSchemaType = new PDFASchemaType (aXmpMetadata);
    aSchemaType.addProperty (aSchemaType.createTextType (PDFASchemaType.SCHEMA, m_sSchemaDescription));
    aSchemaType.addProperty (aSchemaType.createTextType (PDFASchemaType.NAMESPACE_URI, m_sNamespaceURI));
    aSchemaType.addProperty (aSchemaType.createTextType (PDFASchemaType.PREFIX, m_sPrefix));

    final ArrayProperty aPropertySeq = aSchemaType.createArrayProperty (PDFASchemaType.PROPERTY, Cardinality.Seq);
    for (final Property aProperty : m_aProperties)
    {
      final PDFAPropertyType aPropertyType = new PDFAPropertyType (aXmpMetadata);
      aPropertyType.addProperty (aPropertyType.createTextType (PDFAPropertyType.NAME, aProperty.name ()));
      aPropertyType.addProperty (aPropertyType.createTextType (PDFAPropertyType.VALUETYPE, aProperty.valueType ()));
      aPropertyType.addProperty (aPropertyType.createTextType (PDFAPropertyType.CATEGORY, aProperty.category ()));
      aPropertyType.addProperty (aPropertyType.createTextType (PDFAPropertyType.DESCRIPTION,
                                                               aProperty.description ()));
      aPropertySeq.addProperty (aPropertyType);
    }
    aSchemaType.addProperty (aPropertySeq);

    aExtension.addBagValue (PDFAExtensionSchema.SCHEMAS, aSchemaType);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("NamespaceURI", m_sNamespaceURI)
                                       .append ("Prefix", m_sPrefix)
                                       .append ("SchemaDescription", m_sSchemaDescription)
                                       .append ("Properties", m_aProperties)
                                       .getToString ();
  }
}
