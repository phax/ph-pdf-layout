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
package com.helger.pdflayout.spec;

/**
 * Defines the rotation of an object.
 *
 * @author Philip Helger
 */
public enum EPLRotate
{
  /** No rotation */
  ROTATE_0 (0),
  /** 90 degrees clockwise rotation */
  ROTATE_90 (90),
  /** 180 degrees clockwise rotation */
  ROTATE_180 (180),
  /** 270 degrees clockwise rotation */
  ROTATE_270 (270);

  /** By default objects are not rotated. */
  public static final EPLRotate DEFAULT = ROTATE_0;

  private final int m_nAngle;

  EPLRotate (final int nAngle)
  {
    m_nAngle = nAngle;
  }

  public int getAngle ()
  {
    return m_nAngle;
  }

  public boolean isRotate0 ()
  {
    return this == ROTATE_0;
  }

  public boolean isRotate90 ()
  {
    return this == ROTATE_90;
  }

  public boolean isRotate180 ()
  {
    return this == ROTATE_180;
  }

  public boolean isRotate270 ()
  {
    return this == ROTATE_270;
  }

  public boolean isHorizontal ()
  {
    return this == ROTATE_0 || this == ROTATE_180;
  }

  public boolean isVertical ()
  {
    return this == ROTATE_90 || this == ROTATE_270;
  }
}
