package com.helger.pdflayout;

import javax.annotation.Nonnull;

import org.apache.xmpbox.XMPMetadata;

/**
 * Callback interface to customize XMP Metadata.
 *
 * @author stmuecke
 * @author Philip Helger
 * @since 7.3.6
 */
@FunctionalInterface
public interface IXMPMetadataCustomizer
{
  /**
   * Customize the provided {@link XMPMetadata} object. It is called as the last
   * action before it gets serialized.
   *
   * @param aXmpMetadata
   *        The object to be customized. Never <code>null</code>.
   */
  void customizeMetadata (@Nonnull XMPMetadata aXmpMetadata);
}
