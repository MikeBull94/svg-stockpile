package com.mikebull94.svg4j.xml;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents an XML document composed of {@link XMLEvent}s.
 */
public final class XmlDocument {

	/**
	 * The XML namespace attribute.
	 * @see <a href="http://www.w3schools.com/xml/xml_namespaces.asp">XML Namespaces - The xmlns attribute</a>
	 */
	public static final String NAMESPACE = "xmlns";

	/**
	 * Used to create an {@link XMLEventWriter}.
	 */
	private static final XMLOutputFactory output = XMLOutputFactory.newFactory();

	/**
	 * The {@link XMLEvent}s in this {@link XmlDocument}.
	 */
	private final ImmutableList<XMLEvent> events;

	/**
	 * Creates a new {@link XmlDocument}.
	 * @param events The {@link XMLEvent}s used to compose the {@link XmlDocument}
	 */
	public XmlDocument(XMLEvent... events) {
		this(ImmutableList.copyOf(events));
	}

	/**
	 * Creates a new {@link XmlDocument}.
	 * @param events The {@link XMLEvent}s used to compose the {@link XmlDocument}
	 */
	public XmlDocument(Collection<XMLEvent> events) {
		this(ImmutableList.copyOf(events));
	}

	/**
	 * Creates a new {@link XmlDocument}.
	 * @param events The {@link XMLEvent}s used to compose the {@link XmlDocument}
	 */
	public XmlDocument(ImmutableList<XMLEvent> events) {
		this.events = Preconditions.checkNotNull(events);
	}

	/**
	 * Writes the {@link XMLEvent}s in this {@link XmlDocument} to a {@link File}.
	 * @param path The {@link Path} at which to write the {@link File}.
	 * @return The {@link Path} at which the written {@link File} is located.
	 * @throws IOException If an I/O error occurs.
	 * @throws FileNotFoundException If the file exists but is a directory rather than a regular file, does not exist
	 * but cannot be created, or cannot be opened for any other reason.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public Path write(Path path) throws IOException, XMLStreamException {
		try (OutputStream outputStream = Files.newOutputStream(path)) {
			write(outputStream);
		}
		return path;
	}

	/**
	 * Writes the {@link XMLEvent}s in this {@link XmlDocument} to an {@link OutputStream}.
	 * @param outputStream The {@link OutputStream} to write the {@link XMLEvent}s to.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public void write(OutputStream outputStream) throws XMLStreamException {
		XMLEventWriter writer = output.createXMLEventWriter(outputStream);

		try {
			addEventsTo(writer);
		} finally {
			writer.flush();
			writer.close();
		}
	}

	/**
	 * Adds all of the {@link XMLEvent}s in this {@link XmlDocument} to an {@link XMLEventConsumer}.
	 * @param consumer The {@link XMLEventConsumer} to add the {@link XMLEvent}s to.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public void addEventsTo(XMLEventConsumer consumer) throws XMLStreamException {
		for (XMLEvent event : events) {
			consumer.add(event);
		}
	}

	/**
	 * Gets the {@link XMLEvent}s that compose this {@link XmlDocument}.
	 * @return The {@link XMLEvent}s that compose this {@link XmlDocument}.
	 */
	public ImmutableList<XMLEvent> getEvents() {
		return events;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		XmlDocument document = (XmlDocument) o;
		return Objects.equals(events, document.events);
	}

	@Override
	public int hashCode() {
		return Objects.hash(events);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("events", events)
			.toString();
	}
}
