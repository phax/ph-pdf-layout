package com.helger.pdflayout;

import org.apache.xmpbox.XMPMetadata;

@FunctionalInterface
public interface IXMPMetadataCustomizer {

	void customizeMetadata(XMPMetadata metadata);

}
