package com.helger.pdflayout.spec;

import javax.annotation.Nullable;

import com.helger.font.api.IFontResource;
import com.helger.font.api.IHasFontResource;

@FunctionalInterface
public interface IPreloadFontResolver
{
  /**
   * Get the {@link PreloadFont} with the provided ID.
   *
   * @param sID
   *        The ID to be resolved. May be <code>null</code>.
   * @return <code>null</code> if no such {@link PreloadFont} exists.
   */
  @Nullable
  PreloadFont getPreloadFontOfID (@Nullable String sID);

  /**
   * Get the {@link PreloadFont} from the provided font resource.
   *
   * @param aFontRes
   *        The font resource to be resolved. May be <code>null</code>.
   * @return <code>null</code> if no such {@link PreloadFont} exists.
   */
  @Nullable
  default PreloadFont getPreloadFontOfID (@Nullable final IFontResource aFontRes)
  {
    if (aFontRes == null)
      return null;
    return getPreloadFontOfID (aFontRes.getID ());
  }

  /**
   * Get the {@link PreloadFont} from the provided font resource provider.
   *
   * @param aFontResProvider
   *        The font resource provided from which to be resolved. May be
   *        <code>null</code>.
   * @return <code>null</code> if no such {@link PreloadFont} exists.
   */
  @Nullable
  default PreloadFont getPreloadFontOfID (@Nullable final IHasFontResource aFontResProvider)
  {
    if (aFontResProvider == null)
      return null;
    return getPreloadFontOfID (aFontResProvider.getFontResource ());
  }
}
