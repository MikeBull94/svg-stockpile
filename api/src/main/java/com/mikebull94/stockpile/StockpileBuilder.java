package com.mikebull94.stockpile;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mikebull94.stockpile.xml.XmlDocument;
import com.mikebull94.stockpile.xml.XmlEventProcessor;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

import static com.google.common.io.Files.getNameWithoutExtension;

/**
 * Used to build {@link Stockpile}s.
 */
public final class StockpileBuilder {

	/**
	 * Used to create {@link XMLEventReader}s.
	 */
	private static final XMLInputFactory input = XMLInputFactory.newFactory();

	/**
	 * Used to create an {@link ImmutableList} of processed {@link XMLEvent}s.
	 */
	private final ImmutableList.Builder<XMLEvent> events = ImmutableList.builder();

	/**
	 * Used to create an {@link ImmutableSet} of {@link XmlEventProcessor}s.
	 */
	private final ImmutableSet<XmlEventProcessor> processors;

	/**
	 * Creates a new {@link StockpileBuilder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @throws NullPointerException If {@code processors} is {@code null}.
	 */
	StockpileBuilder(ImmutableSet<XmlEventProcessor> processors) {
		this.processors = Preconditions.checkNotNull(processors);
	}

	/**
	 * Processes an {@link XMLEvent} and adds the result of {@link XmlEventProcessor#process(String, XMLEvent)}.
	 * @param id The id of the {@link XmlDocument} this {@link XMLEvent} belongs to.
	 * @param event The {@link XMLEvent} to process.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 */
	public StockpileBuilder process(String id, XMLEvent event) {
		processors.stream().parallel()
			.filter(processor -> processor.accepts(event))
			.forEach(processor -> add(processor.process(id, event)));
		return this;
	}

	/**
	 * Adds an {@link XMLEvent}.
	 * @param event The {@link XMLEvent} to add.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 */
	public StockpileBuilder add(XMLEvent event) {
		events.add(event);
		return this;
	}

	/**
	 * Adds all of the {@link XMLEvent}s in an array.
	 * @param events The array of {@link XMLEvent}s to add.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 */
	public StockpileBuilder add(XMLEvent... events) {
		this.events.add(events);
		return this;
	}

	/**
	 * Adds all of the {@link XMLEvent}s in an {@link Iterable}.
	 * @param events The {@link Iterable} containing the {@link XMLEvent}s to add.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 */
	public StockpileBuilder add(Iterable<XMLEvent> events) {
		this.events.addAll(events);
		return this;
	}

	/**
	 * Adds all of the {@link XMLEvent}s in an {@link Iterator}.
	 * @param events The {@link Iterator} containing the {@link XMLEvent}s to add.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 */
	public StockpileBuilder add(Iterator<XMLEvent> events) {
		this.events.addAll(events);
		return this;
	}

	/**
	 * Reads {@link XMLEvent}s from an {@link InputStream} and passes them to {@link #process(String, XMLEvent)}.
	 * @param id The fragment identifier of this embedded SVG.
	 * @param inputStream The {@link InputStream} to read {@link XMLEvent}s from.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 * @throws NullPointerException If {@code id} is {@code null} or {@code inputStream} is {@code null}.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public StockpileBuilder read(String id, InputStream inputStream) throws XMLStreamException {
		Preconditions.checkNotNull(id);
		XMLEventReader reader = input.createXMLEventReader(Preconditions.checkNotNull(inputStream));

		try {
			while (reader.hasNext()) {
				process(id, reader.nextEvent());
			}
		} finally {
			reader.close();
		}

		return this;
	}

	/**
	 * Reads {@link XMLEvent}s from a file located at a {@link Path}.
	 * @param path The {@link Path} from which to read the file.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 * @throws NullPointerException If {@code path} is null.
	 * @throws IllegalArgumentException If {@link Path#getFileName()} on {@code path} returns {@code null}.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public StockpileBuilder read(Path path) throws IOException, XMLStreamException {
		Preconditions.checkNotNull(path);

		Path fileName = path.getFileName();

		if (fileName == null) {
			throw new IllegalArgumentException("Path " + path + " has zero elements.");
		}

		String id = getNameWithoutExtension(fileName.toString());

		try (InputStream inputStream = Files.newInputStream(path)) {
			return read(id, inputStream);
		}
	}

	/**
	 * Reads {@link XMLEvent}s from files located by an array of {@link Path}s.
	 * @param paths The array of {@link Path}s.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public StockpileBuilder read(Path... paths) throws IOException, XMLStreamException {
		return read(Arrays.asList(paths));
	}

	/**
	 * Reads {@link XMLEvent}s from files in an {@link Iterable} of {@link Path}s.
	 * @param paths The {@link Iterable} of {@link Path}s.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public StockpileBuilder read(Iterable<Path> paths) throws IOException, XMLStreamException {
		for (Path path : paths) {
			read(path);
		}

		return this;
	}

	/**
	 * Reads {@link XMLEvent}s from files in an {@link Iterator} of {@link Path}s.
	 * @param paths The {@link Iterator} of {@link Path}s.
	 * @return The {@link StockpileBuilder} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public StockpileBuilder read(Iterator<Path> paths) throws IOException, XMLStreamException {
		while (paths.hasNext()) {
			read(paths.next());
		}

		return this;
	}

	/**
	 * Builds a new {@link Stockpile}.
	 * @return The built {@link Stockpile}.
	 */
	public Stockpile build() {
		return new Stockpile(events.build());
	}
}
