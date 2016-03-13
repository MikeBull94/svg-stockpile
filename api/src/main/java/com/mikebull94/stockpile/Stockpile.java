package com.mikebull94.stockpile;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mikebull94.stockpile.svg.processor.EndElementProcessor;
import com.mikebull94.stockpile.svg.processor.FilterXmlEventProcessor;
import com.mikebull94.stockpile.svg.processor.StartElementProcessor;
import com.mikebull94.stockpile.svg.processor.SvgTagProcessor;
import com.mikebull94.stockpile.xml.XmlEventProcessor;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

/**
 * SVG Stockpile is an optimizing and stacking tool for <a href="https://www.w3.org/Graphics/SVG/">Scalable Vector
 * Graphics</a>. {@link Stockpile}s can be built using the {@link StockpileBuilder} class, and once created their output
 * may be written to the file system using {@link #write(File)} or {@link #write(Path)}), or any user-supplied
 * {@link OutputStream} using {@link #write(OutputStream)}.
 */
public final class Stockpile {

	/**
	 * Used to create {@link XMLEventWriter}s.
	 */
	private static final XMLOutputFactory output = XMLOutputFactory.newFactory();

	/**
	 * Creates a new {@link StockpileBuilder} with {@link XmlEventProcessor}s registered to stack and optimize SVG
	 * documents.
	 * @return The {@link StockpileBuilder}.
	 */
	public static StockpileBuilder builder() {
		return builder(
			new FilterXmlEventProcessor(),
			new SvgTagProcessor(),
			new StartElementProcessor(),
			new EndElementProcessor()
		);
	}

	/**
	 * Creates a new {@link StockpileBuilder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link StockpileBuilder}.
	 */
	public static StockpileBuilder builder(ImmutableSet<XmlEventProcessor> processors) {
		return new StockpileBuilder(processors);
	}

	/**
	 * Creates a new {@link StockpileBuilder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link StockpileBuilder}.
	 */
	public static StockpileBuilder builder(Collection<XmlEventProcessor> processors) {
		return new StockpileBuilder(ImmutableSet.copyOf(processors));
	}

	/**
	 * Creates a new {@link StockpileBuilder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link StockpileBuilder}.
	 */
	public static StockpileBuilder builder(Iterable<XmlEventProcessor> processors) {
		return new StockpileBuilder(ImmutableSet.copyOf(processors));
	}

	/**
	 * Creates a new {@link StockpileBuilder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link StockpileBuilder}.
	 */
	public static StockpileBuilder builder(Iterator<XmlEventProcessor> processors) {
		return new StockpileBuilder(ImmutableSet.copyOf(processors));
	}

	/**
	 * Creates a new {@link StockpileBuilder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link StockpileBuilder}.
	 */
	public static StockpileBuilder builder(XmlEventProcessor... processors) {
		return new StockpileBuilder(ImmutableSet.copyOf(processors));
	}

	/**
	 * The {@link XMLEvent}s in this stockpile.
	 */
	private final ImmutableList<XMLEvent> events;

	/**
	 * Creates a new {@link Stockpile}.
	 * @param events The {@link XMLEvent}s.
	 * @throws NullPointerException If {@code events} is null.
	 */
	public Stockpile(ImmutableList<XMLEvent> events) {
		this.events = Preconditions.checkNotNull(events);
	}

	/**
	 * Writes the processed {@link XMLEvent}s to a {@link File}.
	 * @param file The {@link File} to write to.
	 * @return The {@link Stockpile} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public Stockpile write(File file) throws IOException, XMLStreamException {
		return write(file.toPath());
	}

	/**
	 * Writes the processed {@link XMLEvent}s to a {@link File}.
	 * @param path The {@link Path} at which to write the {@link File}.
	 * @return The {@link Stockpile} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public Stockpile write(Path path) throws IOException, XMLStreamException {
		try (OutputStream outputStream = Files.newOutputStream(path)) {
			return write(outputStream);
		}
	}

	/**
	 * Writes the processed {@link XMLEvent}s to an {@link OutputStream}.
	 * @param outputStream The {@link OutputStream} to write the {@link XMLEvent}s to.
	 * @return The {@link Stockpile} instance for chaining.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public Stockpile write(OutputStream outputStream) throws XMLStreamException {
		XMLEventWriter writer = output.createXMLEventWriter(outputStream);

		try {
			addEventsTo(writer);
		} finally {
			writer.flush();
			writer.close();
		}

		return this;
	}

	/**
	 * Adds the {@link XMLEvent}s in this stockpile to an {@link XMLEventConsumer}.
	 * @param consumer The {@link XMLEventConsumer} to add the {@link XMLEvent}s to.
	 * @return The {@link Stockpile} instance for chaining.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public Stockpile addEventsTo(XMLEventConsumer consumer) throws XMLStreamException {
		for (XMLEvent event : events) {
			consumer.add(event);
		}

		return this;
	}

	/**
	 * Gets the number of {@link XMLEvent}s in this stockpile.
	 * @return The number of {@link XMLEvent}s in this stockpile.
	 */
	public int size() {
		return events.size();
	}
}
