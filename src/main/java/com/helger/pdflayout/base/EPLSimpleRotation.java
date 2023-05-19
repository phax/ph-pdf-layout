package com.helger.pdflayout.base;

/**
 * This enum contains the straight forward rotations by 90 degrees.
 *
 * @author Philip Helger
 */
public enum EPLSimpleRotation
{
  ROTATE_0 (0),
  ROTATE_90 (90),
  ROTATE_180 (180),
  ROTATE_270 (270);

  private final int m_nDegrees;
  private final double m_dRadians;

  EPLSimpleRotation (final int nDegrees)
  {
    m_nDegrees = nDegrees;
    m_dRadians = Math.toRadians (nDegrees);
  }

  public int getDegrees ()
  {
    return m_nDegrees;
  }

  public double getRadians ()
  {
    return m_dRadians;
  }

  public boolean isTransformNeeded ()
  {
    return this != ROTATE_0;
  }

  /**
   * @return <code>true</code> if the resulting box will have width and height
   *         swapped, <code>false</code> if width stays width, and height stays
   *         height.
   */
  public boolean isRotateBox ()
  {
    return this == ROTATE_90 || this == ROTATE_270;
  }

  public boolean hasTx ()
  {
    return this == ROTATE_90 || this == ROTATE_180;
  }

  public boolean hasTy ()
  {
    return this == ROTATE_180 || this == ROTATE_270;
  }
}
