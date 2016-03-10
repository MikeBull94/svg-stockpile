package com.mikebull94.svg4j.svg.processor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.xml.XmlEventProcessor;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;

/**
 * An {@link XmlEventProcessor} that accepts all {@link XMLEvent}s whose {@link XMLEvent#getEventType()} is not
 * contained in {@link #REJECTED_TYPES}, returning an {@link ImmutableList} containing the provided {@link XMLEvent} as
 * the result of processing. This is intended to deal with all {@link XMLEvent}s that are not otherwise covered in the
 * stacking process.
 */
public final class FilterXmlEventProcessor implements XmlEventProcessor {

	/**
	 * The {@link XMLEvent#getEventType()}s to reject.
	 */
	private static final ImmutableList<Integer> REJECTED_TYPES = ImmutableList.of(
		XMLStreamConstants.START_ELEMENT,
		XMLStreamConstants.END_ELEMENT,
		XMLStreamConstants.PROCESSING_INSTRUCTION,
		XMLStreamConstants.CHARACTERS,
		XMLStreamConstants.COMMENT,
		XMLStreamConstants.SPACE,
		XMLStreamConstants.START_DOCUMENT,
		XMLStreamConstants.END_DOCUMENT,
		XMLStreamConstants.DTD
	);

	@Override
	public boolean accepts(XMLEvent event) {
		return !REJECTED_TYPES.contains(event.getEventType());
	}

	@Override
	public ImmutableList<XMLEvent> process(String id, XMLEvent event) {
		Preconditions.checkNotNull(event);
		return ImmutableList.of(event);
	}
}
