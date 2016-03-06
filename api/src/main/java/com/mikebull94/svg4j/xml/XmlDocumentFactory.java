package com.mikebull94.svg4j.xml;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * A factory for creating {@link XmlDocument}s. All parsed {@link XMLEvent}s are processed by the {@link Set} of
 * registered {@link XmlEventProcessor}s using {@link XmlEventProcessor#process(String, XMLEvent)}, with the result
 * attributing to the final {@link XmlDocument}.
 */
public final class XmlDocumentFactory {

	/**
	 * Used to create {@link XMLEventReader}s.
	 */
	private static final XMLInputFactory input = XMLInputFactory.newFactory();

	/**
	 * The {@link XmlEventProcessor}s used to process all parsed {@link XMLEvent}s.
	 */
	private final ImmutableSet<XmlEventProcessor> processors;

	/**
	 * Creates a new {@link XmlDocumentFactory}.
	 * @param processors The {@link XmlEventProcessor}s used to process all parsed {@link XMLEvent}s.
	 */
	public XmlDocumentFactory(XmlEventProcessor... processors) {
		this(ImmutableSet.copyOf(processors));
	}

	/**
	 * Creates a new {@link XmlDocumentFactory}.
	 * @param processors The {@link XmlEventProcessor}s used to process all parsed {@link XMLEvent}s.
	 */
	public XmlDocumentFactory(Collection<XmlEventProcessor> processors) {
		this(ImmutableSet.copyOf(processors));
	}

	/**
	 * Creates a new {@link XmlDocumentFactory}.
	 * @param processors The {@link XmlEventProcessor}s used to process all parsed {@link XMLEvent}s.
	 */
	public XmlDocumentFactory(ImmutableSet<XmlEventProcessor> processors) {
		this.processors = Preconditions.checkNotNull(processors);
	}

	/**
	 * Creates an {@link XmlDocument} by reading from an {@link InputStream}.
	 * @param id The id of the created {@link XmlDocument}.
	 * @param inputStream The {@link InputStream} to read from.
	 * @return The constructed {@link XmlDocument}.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public XmlDocument create(String id, InputStream inputStream) throws XMLStreamException {
		Preconditions.checkNotNull(id);

		ImmutableList.Builder<XMLEvent> builder = ImmutableList.builder();
		XMLEventReader reader = input.createXMLEventReader(inputStream);

		try {
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();

				processors.stream()
					.filter(processor -> processor.accepts(event))
					.forEach(processor -> builder.addAll(processor.process(id, event)));
			}
		} finally {
			reader.close();
		}

		return new XmlDocument(builder.build());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		XmlDocumentFactory that = (XmlDocumentFactory) o;
		return Objects.equals(processors, that.processors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(processors);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("processors", processors)
			.toString();
	}
}
