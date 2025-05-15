package com.helger.pdflayout.spec;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;

/**
 * Possible line join styles.
 *
 * @author Philip Helger
 * @since 7.3.8
 */
public enum ELineJoinStyle implements IHasIntID
{
  MITER (0),
  ROUND (1),
  BEVEL (2);

  private final int m_nValue;

  ELineJoinStyle (@Nonnegative final int nValue)
  {
    m_nValue = nValue;
  }

  @Nonnegative
  public int getID ()
  {
    return m_nValue;
  }

  @Nullable
  public static ELineJoinStyle getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (ELineJoinStyle.class, nID);
  }
}
