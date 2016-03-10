package com.mikebull94.svg4j.svg.processor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.svg.SvgDocument;
import com.mikebull94.svg4j.xml.XmlEventProcessor;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;

/**
 * An {@link XmlEventProcessor} that accepts {@link EndElement}s whose {@link QName} does not equal
 * {@link SvgDocument#SVG_TAG} or {@link SvgDocument#GROUP_TAG}.
 */
public final class EndElementProcessor implements XmlEventProcessor {
	@Override
	public boolean accepts(XMLEvent event) {
		if (!event.isEndElement()) {
			return false;
		}

		QName name = event.asEndElement().getName();
		return SvgDocument.optimized(name) && !name.equals(SvgDocument.SVG_TAG) && !name.equals(SvgDocument.GROUP_TAG);
	}

	@Override
	public ImmutableList<XMLEvent> process(String id, XMLEvent event) {
		Preconditions.checkNotNull(event);
		return ImmutableList.of(event);
	}
}
