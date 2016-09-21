package com.helger.pdflayout.base;

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.pdflayout.render.PageSetupContext;
import com.helger.pdflayout.render.PreparationContext;
import com.helger.pdflayout.render.RenderingContext;
import com.helger.pdflayout.spec.SizeSpec;

/**
 * Base interface for a renderable PDF layout object
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IPLRenderableObject <IMPLTYPE extends IPLRenderableObject <IMPLTYPE>> extends IPLObject <IMPLTYPE>
{
  SizeSpec DEFAULT_MIN_SIZE = SizeSpec.SIZE0;
  SizeSpec DEFAULT_MAX_SIZE = new SizeSpec (Float.MAX_VALUE, Float.MAX_VALUE);

  /**
   * @return The minimum size to be used. Excluding padding and margin. Never
   *         <code>null</code>.
   */
  @Nonnull
  SizeSpec getMinSize ();

  /**
   * Set the minimum size to be used. Excluding padding and margin.
   *
   * @param fMinWidth
   *        Minimum width. Must be &ge; 0.
   * @param fMinHeight
   *        Minimum height. Must be &ge; 0.
   * @return this
   */
  @Nonnull
  IMPLTYPE setMinSize (@Nonnegative final float fMinWidth, @Nonnegative final float fMinHeight);

  /**
   * @return The maximum size to be used. Excluding padding and margin. Never
   *         <code>null</code>.
   */
  @Nonnull
  SizeSpec getMaxSize ();

  /**
   * Set the maximum size to be used. Excluding padding and margin.
   *
   * @param fMaxWidth
   *        Maximum width. Must be &ge; 0.
   * @param fMaxHeight
   *        Maximum height. Must be &ge; 0.
   * @return this
   */
  @Nonnull
  IMPLTYPE setMaxSize (@Nonnegative final float fMaxWidth, @Nonnegative final float fMaxHeight);

  /**
   * Set the exact size to be used. Excluding padding and margin. This is a
   * shortcut for setting minimum and maximum size to the same values.
   *
   * @param fWidth
   *        Width to use. Must be &ge; 0.
   * @param fHeight
   *        Height to use. Must be &ge; 0.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setExactSize (@Nonnegative final float fWidth, @Nonnegative final float fHeight)
  {
    setMinSize (fWidth, fHeight);
    return setMaxSize (fWidth, fHeight);
  }

  /**
   * Prepare this element once for rendering.
   *
   * @param aCtx
   *        The preparation context
   * @return The net size of the rendered object without margin, border and
   *         margin. May not be <code>null</code>.
   * @throws IOException
   *         if already prepared
   * @see #perform(RenderingContext)
   */
  @Nonnull
  SizeSpec prepare (@Nonnull final PreparationContext aCtx) throws IOException;

  /**
   * Called after the page was created but before the content stream is created.
   * This is e.g. used for images to create their XObjects upfront.
   *
   * @param aCtx
   *        The current page setup context. Never <code>null</code>.
   */
  default void doPageSetup (@Nonnull final PageSetupContext aCtx)
  {}

  /**
   * Second step: perform. This renders the previously prepared object to the
   * PDF content stream present in the rendering context.
   *
   * @param aCtx
   *        Rendering context
   * @throws IOException
   *         In case of a PDFBox error
   * @see #prepare(PreparationContext)
   */
  @Nonnegative
  void perform (@Nonnull final RenderingContext aCtx) throws IOException;
}
