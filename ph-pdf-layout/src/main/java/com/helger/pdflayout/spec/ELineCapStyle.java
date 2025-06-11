package com.helger.pdflayout.spec;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;

/**
 * Possible line cap styles.
 *
 * @author Philip Helger
 * @since 7.4.0
 */
public enum ELineCapStyle implements IHasIntID
{
  CAP (0),
  ROUND_CAP (1),
  SQUARE (2);

  private final int m_nValue;

  ELineCapStyle (@Nonnegative final int nValue)
  {
    m_nValue = nValue;
  }

  @Nonnegative
  public int getID ()
  {
    return m_nValue;
  }

  @Nullable
  public static ELineCapStyle getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (ELineCapStyle.class, nID);
  }
}
