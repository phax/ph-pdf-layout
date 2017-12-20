package com.helger.pdflayout4.element.image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Image type to use for rendering in {@link PLImage}, {@link PLStreamImage}
 * etc.
 *
 * @author Philip Helger
 * @since 5.0.1
 */
public enum EPLImageType implements IHasID <String>
{
  CCITT ("ccitt"),
  JPEG ("jpeg"),
  LOSSLESS ("lossless");

  private final String m_sID;

  private EPLImageType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nullable
  public static EPLImageType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EPLImageType.class, sID);
  }
}
