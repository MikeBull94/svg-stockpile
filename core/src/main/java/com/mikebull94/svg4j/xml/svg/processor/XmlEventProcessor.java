package com.mikebull94.svg4j.xml.svg.processor;

import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.xml.XmlDocument;

import javax.xml.stream.events.XMLEvent;

/**
 * May accept an {@link XMLEvent}, returning an {@link ImmutableList} of {@link XMLEvent}s as a result of the processing
 * the provided {@link XMLEvent}.
 */
public interface XmlEventProcessor {

	/**
	 * A flag indicating whether this processor should accept an {@link XMLEvent}.
	 * @param event The {@link XMLEvent}.
	 * @return {@code true} if so, {@code false} otherwise.
	 */
	boolean accepts(XMLEvent event);

	/**
	 * Processes an {@link XMLEvent}.
	 * @param id The id of the {@link XmlDocument} this {@link XMLEvent} belongs to.
	 * @param event The {@link XMLEvent} to process.
	 * @return An {@link ImmutableList} of {@link XMLEvent}s that represent the result of the processing.
	 */
	ImmutableList<XMLEvent> process(String id, XMLEvent event);
}
