package com.mikebull94.stockpile;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mikebull94.stockpile.svg.processor.EndElementProcessor;
import com.mikebull94.stockpile.svg.processor.FilterXmlEventProcessor;
import com.mikebull94.stockpile.svg.processor.StartElementProcessor;
import com.mikebull94.stockpile.svg.processor.SvgTagProcessor;
import com.mikebull94.stockpile.xml.XmlDocument;
import com.mikebull94.stockpile.xml.XmlEventProcessor;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static com.google.common.io.Files.getNameWithoutExtension;

/**
 * SVG Stockpile is an optimizing and stacking tool for <a href="https://www.w3.org/Graphics/SVG/">Scalable Vector
 * Graphics</a>. {@link Stockpile}s can be built using the {@link Builder} class, and once created their output may be
 * written to the file system using {@link #write(File)} or {@link #write(Path)}), or any user-supplied
 * {@link OutputStream} using {@link #write(OutputStream)}
 */
public final class Stockpile {

	/**
	 * Used to create {@link XMLEventWriter}s.
	 */
	private static final XMLOutputFactory output = XMLOutputFactory.newFactory();

	/**
	 * Used to create {@link XMLEventReader}s.
	 */
	private static final XMLInputFactory input = XMLInputFactory.newFactory();

	/**
	 * Builds {@link Stockpile}s.
	 */
	public static final class Builder {

		/**
		 * Used to create an {@link ImmutableList} of processed {@link XMLEvent}s.
		 */
		private final ImmutableList.Builder<XMLEvent> events = ImmutableList.builder();

		/**
		 * Used to create an {@link ImmutableSet} of {@link XmlEventProcessor}s.
		 */
		private final ImmutableSet<XmlEventProcessor> processors;

		/**
		 * Creates a new {@link Builder}.
		 * @param processors The {@link XmlEventProcessor}s to register.
		 * @throws NullPointerException If {@code processors} is {@code null}.
		 */
		Builder(ImmutableSet<XmlEventProcessor> processors) {
			this.processors = Preconditions.checkNotNull(processors);
		}

		/**
		 * Processes an {@link XMLEvent} and adds the result of {@link XmlEventProcessor#process(String, XMLEvent)}.
		 * @param id The id of the {@link XmlDocument} this {@link XMLEvent} belongs to.
		 * @param event The {@link XMLEvent} to process.
		 * @return The {@link Builder} instance for chaining.
		 */
		public Builder process(String id, XMLEvent event) {
			processors.stream().parallel()
				.filter(processor -> processor.accepts(event))
				.forEach(processor -> add(processor.process(id, event)));
			return this;
		}

		/**
		 * Adds an {@link XMLEvent}.
		 * @param event The {@link XMLEvent} to add.
		 * @return The {@link Builder} instance for chaining.
		 */
		public Builder add(XMLEvent event) {
			events.add(event);
			return this;
		}

		/**
		 * Adds all of the {@link XMLEvent}s in an array.
		 * @param events The array of {@link XMLEvent}s to add.
		 * @return The {@link Builder} instance for chaining.
		 */
		public Builder add(XMLEvent... events) {
			this.events.add(events);
			return this;
		}

		/**
		 * Adds all of the {@link XMLEvent}s in an {@link Iterable}.
		 * @param events The {@link Iterable} containing the {@link XMLEvent}s to add.
		 * @return The {@link Builder} instance for chaining.
		 */
		public Builder add(Iterable<XMLEvent> events) {
			this.events.addAll(events);
			return this;
		}

		/**
		 * Adds all of the {@link XMLEvent}s in an {@link Iterator}.
		 * @param events The {@link Iterator} containing the {@link XMLEvent}s to add.
		 * @return The {@link Builder} instance for chaining.
		 */
		public Builder add(Iterator<XMLEvent> events) {
			this.events.addAll(events);
			return this;
		}

		/**
		 * Reads {@link XMLEvent}s from an {@link InputStream} and passes them to {@link #process(String, XMLEvent)}.
		 * @param id The fragment identifier of this embedded SVG.
		 * @param inputStream The {@link InputStream} to read {@link XMLEvent}s from.
		 * @return The {@link Builder} instance for chaining.
		 * @throws NullPointerException If {@code id} is {@code null} or {@code inputStream} is {@code null}.
		 * @throws XMLStreamException If an XML error occurs.
		 */
		public Builder read(String id, InputStream inputStream) throws XMLStreamException {
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
		 * @return The {@link Builder} instance for chaining.
		 * @throws NullPointerException If {@code path} is null.
		 * @throws IllegalArgumentException If {@link Path#getFileName()} on {@code path} returns {@code null}.
		 * @throws IOException If an I/O error occurs.
		 * @throws XMLStreamException If an XML error occurs.
		 */
		public Builder read(Path path) throws IOException, XMLStreamException {
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
		 * @return The {@link Builder} instance for chaining.
		 * @throws IOException If an I/O error occurs.
		 * @throws XMLStreamException If an XML error occurs.
		 */
		public Builder read(Path... paths) throws IOException, XMLStreamException {
			return read(Arrays.asList(paths));
		}

		/**
		 * Reads {@link XMLEvent}s from files in an {@link Iterable} of {@link Path}s.
		 * @param paths The {@link Iterable} of {@link Path}s.
		 * @return The {@link Builder} instance for chaining.
		 * @throws IOException If an I/O error occurs.
		 * @throws XMLStreamException If an XML error occurs.
		 */
		public Builder read(Iterable<Path> paths) throws IOException, XMLStreamException {
			for (Path path : paths) {
				read(path);
			}

			return this;
		}

		/**
		 * Reads {@link XMLEvent}s from files in an {@link Iterator} of {@link Path}s.
		 * @param paths The {@link Iterator} of {@link Path}s.
		 * @return The {@link Builder} instance for chaining.
		 * @throws IOException If an I/O error occurs.
		 * @throws XMLStreamException If an XML error occurs.
		 */
		public Builder read(Iterator<Path> paths) throws IOException, XMLStreamException {
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

	/**
	 * Creates a new {@link Builder} with {@link XmlEventProcessor}s registered to stack and optimize SVG documents.
	 * @return The {@link Builder}.
	 */
	public static Builder builder() {
		return builder(
			new FilterXmlEventProcessor(),
			new SvgTagProcessor(),
			new StartElementProcessor(),
			new EndElementProcessor()
		);
	}

	/**
	 * Creates a new {@link Builder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link Builder}.
	 */
	public static Builder builder(ImmutableSet<XmlEventProcessor> processors) {
		return new Builder(processors);
	}

	/**
	 * Creates a new {@link Builder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link Builder}.
	 */
	public static Builder builder(Collection<XmlEventProcessor> processors) {
		return new Builder(ImmutableSet.copyOf(processors));
	}

	/**
	 * Creates a new {@link Builder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link Builder}.
	 */
	public static Builder builder(Iterable<XmlEventProcessor> processors) {
		return new Builder(ImmutableSet.copyOf(processors));
	}

	/**
	 * Creates a new {@link Builder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link Builder}.
	 */
	public static Builder builder(Iterator<XmlEventProcessor> processors) {
		return new Builder(ImmutableSet.copyOf(processors));
	}

	/**
	 * Creates a new {@link Builder}.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link Builder}.
	 */
	public static Builder builder(XmlEventProcessor... processors) {
		return new Builder(ImmutableSet.copyOf(processors));
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
