package com.helger.pdflayout.base;

/**
 * Interface for an element with padding, border, margin and fill color that
 * also has a horizontal alignment
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLHorzAlignedElement <IMPLTYPE extends IPLHorzAlignedElement <IMPLTYPE>>
                                       extends IPLElement <IMPLTYPE>, IPLHasHorizontalAlignment <IMPLTYPE>
{
  /**
   * Get the indentation for a certain horizontal alignment. This method uses
   * the prepared width as the basis for alignment.
   *
   * @param fAvailableWidth
   *        The available width of the surrounding element.
   * @return The indentation offset
   */
  default float getIndentX (final float fAvailableWidth)
  {
    return getIndentX (fAvailableWidth, getPreparedWidth ());
  }

  /**
   * Get the indentation for a certain horizontal alignment. This method uses
   * the provided element width as the basis for alignment.
   *
   * @param fAvailableWidth
   *        The available width of the surrounding element.
   * @param fElementWidth
   *        The width of the element to align
   * @return The indentation offset
   */
  default float getIndentX (final float fAvailableWidth, final float fElementWidth)
  {
    switch (getHorzAlign ())
    {
      case LEFT:
        return 0f;
      case CENTER:
        return (fAvailableWidth - fElementWidth) / 2;
      case RIGHT:
        return fAvailableWidth - fElementWidth;
      default:
        throw new IllegalStateException ("Unsupported horizontal alignment " + getHorzAlign ());
    }
  }
}
