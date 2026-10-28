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
package com.helger.pdflayout;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A file that is embedded into the PDF and associated with the whole document (PDF/A-3
 * "associated file"). The file is listed in the embedded files of the document and in the
 * <code>/AF</code> array of the document catalogue.
 *
 * @author Philip Helger
 * @since 8.4.0
 */
@Immutable
public final class PdfAssociatedFile
{
  private final byte [] m_aContent;
  private final String m_sFileName;
  private final String m_sMimeType;
  private final String m_sDescription;
  private final EPdfAFRelationship m_eRelationship;

  /**
   * Constructor
   *
   * @param aContent
   *        The content of the file. May not be <code>null</code>. The array is not copied.
   * @param sFileName
   *        The file name as shown in the PDF viewer, e.g. <code>invoice.xml</code>. May neither be
   *        <code>null</code> nor empty.
   * @param sMimeType
   *        The MIME type of the file, e.g. <code>text/xml</code>. May neither be <code>null</code>
   *        nor empty.
   * @param sDescription
   *        An optional description of the file. May be <code>null</code>.
   * @param eRelationship
   *        The relationship of the file to the document. May not be <code>null</code>.
   */
  public PdfAssociatedFile (final byte @NonNull [] aContent,
                            @NonNull @Nonempty final String sFileName,
                            @NonNull @Nonempty final String sMimeType,
                            @Nullable final String sDescription,
                            @NonNull final EPdfAFRelationship eRelationship)
  {
    ValueEnforcer.notNull (aContent, "Content");
    ValueEnforcer.notEmpty (sFileName, "FileName");
    ValueEnforcer.notEmpty (sMimeType, "MimeType");
    ValueEnforcer.notNull (eRelationship, "Relationship");
    m_aContent = aContent;
    m_sFileName = sFileName;
    m_sMimeType = sMimeType;
    m_sDescription = sDescription;
    m_eRelationship = eRelationship;
  }

  /**
   * @return The content of the file. Never <code>null</code>. The array is not copied.
   */
  @ReturnsMutableObject
  public byte @NonNull [] getContent ()
  {
    return m_aContent;
  }

  /**
   * @return The file name. Never <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getFileName ()
  {
    return m_sFileName;
  }

  /**
   * @return The MIME type. Never <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getMimeType ()
  {
    return m_sMimeType;
  }

  /**
   * @return The optional description. May be <code>null</code>.
   */
  @Nullable
  public String getDescription ()
  {
    return m_sDescription;
  }

  /**
   * @return The relationship of the file to the document. Never <code>null</code>.
   */
  @NonNull
  public EPdfAFRelationship getRelationship ()
  {
    return m_eRelationship;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("ContentLength", m_aContent.length)
                                       .append ("FileName", m_sFileName)
                                       .append ("MimeType", m_sMimeType)
                                       .append ("Description", m_sDescription)
                                       .append ("Relationship", m_eRelationship)
                                       .getToString ();
  }
}
