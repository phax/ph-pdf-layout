package com.helger.pdflayout.base;

/**
 * Interface for an element with padding, border, margin and fill color that
 * also has a vertical alignment
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLVertAlignedElement <IMPLTYPE extends IPLVertAlignedElement <IMPLTYPE>>
                                       extends IPLElement <IMPLTYPE>, IPLHasVerticalAlignment <IMPLTYPE>
{
  /**
   * Get the indentation for a certain vertical alignment. This method uses the
   * prepared height as the basis for alignment.
   *
   * @param fAvailableHeight
   *        The available height of the surrounding element.
   * @return The indentation offset
   */
  default float getIndentY (final float fAvailableHeight)
  {
    return getIndentY (fAvailableHeight, getPreparedHeight ());
  }

  /**
   * Get the indentation for a certain vertical alignment. This method uses the
   * provided element height as the basis for alignment.
   *
   * @param fAvailableHeight
   *        The available height of the surrounding element.
   * @param fElementHeight
   *        The height of the element to align
   * @return The indentation offset
   */
  default float getIndentY (final float fAvailableHeight, final float fElementHeight)
  {
    switch (getVertAlign ())
    {
      case TOP:
        return 0f;
      case MIDDLE:
        return (fAvailableHeight - fElementHeight) / 2f;
      case BOTTOM:
        return fAvailableHeight - fElementHeight;
      default:
        throw new IllegalStateException ("Unsupported vertical alignment " + getVertAlign ());
    }
  }
}
