package com.mikebull94.svg4j;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mikebull94.svg4j.svg.SvgViewBox;
import com.mikebull94.svg4j.svg.processor.EndElementProcessor;
import com.mikebull94.svg4j.svg.processor.FilterXmlEventProcessor;
import com.mikebull94.svg4j.svg.processor.StartElementProcessor;
import com.mikebull94.svg4j.svg.processor.SvgTagProcessor;
import com.mikebull94.svg4j.util.PathUtils;
import com.mikebull94.svg4j.xml.XmlDocument;
import com.mikebull94.svg4j.xml.XmlEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.io.Files.getNameWithoutExtension;
import static com.mikebull94.svg4j.svg.SvgDocument.EMBEDDED_NAMESPACE;
import static com.mikebull94.svg4j.svg.SvgDocument.NAMESPACE_URI;
import static com.mikebull94.svg4j.svg.SvgDocument.STYLE_TAG;
import static com.mikebull94.svg4j.svg.SvgDocument.SVG_TAG;
import static com.mikebull94.svg4j.xml.XmlDocument.NAMESPACE;
import static java.util.Collections.emptyIterator;

/**
 * Allows for usage of a predefined {@link Set} of {@link XmlEventProcessor}s designed to stack and optimize an SVG
 * document, or a user-supplied {@link Set} of {@link XmlEventProcessor}s, that will be applied to SVG documents
 * passed into {@link #read(String, InputStream)}. Once {@link XMLEvent}s have finished reading the user may then write
 * them to the file system (using {@link #write(File)} or {@link #write(Path)}) or any {@link OutputStream} using
 * {@link #write(OutputStream)}.
 */
public final class Svg4j {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Svg4j.class);

	/**
	 * Used to create {@link XMLEventWriter}s.
	 */
	private static final XMLOutputFactory output = XMLOutputFactory.newFactory();

	/**
	 * Used to create {@link XMLEventReader}s.
	 */
	private static final XMLInputFactory input = XMLInputFactory.newFactory();

	/**
	 * Used to create SVG {@link StartElement}s and {@link EndElement}s.
	 */
	private static final XMLEventFactory events = XMLEventFactory.newFactory();

	/**
	 * The expected program arguments format.
	 */
	private static final String EXPECTED_ARGUMENTS =
		"Expecting program arguments: <inputDir> <output> <viewBoxMinX> <viewBoxMinY> <viewBoxWidth> <viewBoxHeight>";

	/**
	 * The entry point of the program.
	 * @param args The program's arguments.
	 */
	public static void main(String... args) {
		try {
			logger.info("Starting svg4j...");

			Preconditions.checkArgument(args.length == 6, EXPECTED_ARGUMENTS);

			Path inputDir = Paths.get(args[0]);
			Path output = Paths.get(args[1]);
			int minX = Integer.parseInt(args[2]);
			int minY = Integer.parseInt(args[3]);
			int width = Integer.parseInt(args[4]);
			int height = Integer.parseInt(args[5]);

			SvgViewBox viewBox = new SvgViewBox(minX, minY, width, height);

			logger.info("Stacking into: {}", viewBox);
			logger.info("Searching input directory: {}", inputDir);
			logger.info("Stacking to file: {}", output);

			ImmutableList<Path> input = PathUtils.filterPathsIn(inputDir, PathUtils::hasSvgExtension);

			Svg4j stacker = createStacker(viewBox)
				.createSvgStart()
				.hideEmbeddedSvgs()
				.read(input)
				.svgEnd()
				.write(output);

			logger.info("Stacked {} XML events.", stacker.size());
		} catch (Throwable t) {
			logger.error("Failed to run svg4j.", t);
		}
	}

	/**
	 * Creates an {@link Svg4j} with a {@link Set} of {@link XmlEventProcessor}s configured to stack and optimize SVG
	 * documents.
	 * @param viewBox The {@link SvgViewBox} to provide embedded SVGs with.
	 * @return The {@link Svg4j}.
	 * @throws NullPointerException If the {@link SvgViewBox} is null.
	 */
	public static Svg4j createStacker(SvgViewBox viewBox) {
		return create(viewBox,
			new FilterXmlEventProcessor(),
			new SvgTagProcessor(),
			new StartElementProcessor(),
			new EndElementProcessor()
		);
	}

	/**
	 * Creates an {@link Svg4j} with a {@link Set} of registered {@link XmlEventProcessor}s.
	 * @param viewBox The {@link SvgViewBox} to provide embedded SVGs with.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link Svg4j}.
	 * @throws NullPointerException If the {@link SvgViewBox} is null.
	 */
	public static Svg4j create(SvgViewBox viewBox, XmlEventProcessor... processors) {
		return create(viewBox, ImmutableSet.copyOf(processors));
	}

	/**
	 * Creates an {@link Svg4j} with a {@link Set} of registered {@link XmlEventProcessor}s.
	 * @param viewBox The {@link SvgViewBox} to provide embedded SVGs with.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 * @return The {@link Svg4j}.
	 * @throws NullPointerException If the {@link SvgViewBox} or {@link XmlEventProcessor}s are null.
	 */
	public static Svg4j create(SvgViewBox viewBox, ImmutableSet<XmlEventProcessor> processors) {
		return new Svg4j(viewBox, processors);
	}

	/**
	 * The processed {@link XMLEvent}s.
	 */
	private final List<XMLEvent> processed = new ArrayList<>();

	/**
	 * The {@link SvgViewBox} to provide embedded SVGs with.
	 */
	private final SvgViewBox viewBox;

	/**
	 * The {@link XmlEventProcessor}s used to process incoming {@link XMLEvent}s.
	 */
	private final ImmutableSet<XmlEventProcessor> processors;

	/**
	 * Creates an {@link Svg4j} with a {@link Set} of registered {@link XmlEventProcessor}s.
	 * @param viewBox The {@link SvgViewBox} to provide embedded SVGs with.
	 * @param processors The {@link XmlEventProcessor}s to register.
	 */
	private Svg4j(SvgViewBox viewBox, ImmutableSet<XmlEventProcessor> processors) {
		this.viewBox = Preconditions.checkNotNull(viewBox);
		this.processors = Preconditions.checkNotNull(processors);
	}

	/**
	 * Adds a {@link StartElement} with the {@code <svg>} tag to the {@link List} of processed {@link XMLEvent}s.
	 * @return The {@link Svg4j} instance for chaining.
	 */
	public Svg4j createSvgStart() {
		Collection<Attribute> attributes = new ArrayList<>();
		attributes.add(events.createAttribute(NAMESPACE, NAMESPACE_URI));
		attributes.add(events.createAttribute(EMBEDDED_NAMESPACE, NAMESPACE_URI));
		attributes.addAll(viewBox.attributes());

		StartElement svgStart = events.createStartElement(SVG_TAG, attributes.iterator(), emptyIterator());
		processed.add(svgStart);
		return this;
	}

	/**
	 * Adds {@link XMLEvent}s related to hiding embedded SVGs to the {@link List} of processed {@link XMLEvent}s.
	 * @return The {@link Svg4j} instance for chaining.
	 */
	public Svg4j hideEmbeddedSvgs() {
		processed.add(events.createStartElement(STYLE_TAG, emptyIterator(), emptyIterator()));
		processed.add(events.createCharacters(".i {display:none;}.i:target {display:block;}"));
		processed.add(events.createEndElement(STYLE_TAG, emptyIterator()));
		return this;
	}

	/**
	 * Adds an {@link EndElement} with the {@code <svg>} tag to the {@link List} of processed {@link XMLEvent}s.
	 * @return The {@link Svg4j} instance for chaining.
	 */
	public Svg4j svgEnd() {
		processed.add(events.createEndElement(SVG_TAG, emptyIterator()));
		return this;
	}

	/**
	 * Reads {@link XMLEvent}s from a file located at a {@link Path}.
	 * @param path The {@link Path} from which to read the file.
	 * @return The {@link Svg4j} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 * @throws NullPointerException If the {@link SvgViewBox} or {@link Path} are null.
	 */
	public Svg4j read(Path path) throws IOException, XMLStreamException {
		Preconditions.checkNotNull(path);

		logger.info("Stacking: {}", path);

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
	 * @param paths The {@link Path}s from which to read the files.
	 * @return The {@link Svg4j} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 * @throws NullPointerException If the {@link SvgViewBox} is null.
	 */
	public Svg4j read(Path... paths) throws IOException, XMLStreamException {
		return read(Arrays.asList(paths));
	}

	/**
	 * Reads {@link XMLEvent}s from files located by an {@link Iterable} of {@link Path}s.
	 * @param paths The {@link Path}s from which to read the files.
	 * @return The {@link Svg4j} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 * @throws NullPointerException If the {@link SvgViewBox} or {@link Path}s are null.
	 */
	public Svg4j read(Iterable<Path> paths) throws IOException, XMLStreamException {
		for (Path path : paths) {
			read(path);
		}

		return this;
	}

	/**
	 * Reads {@link XMLEvent}s from an {@link InputStream}.
	 * @param id The fragment identifier of this embedded SVG.
	 * @param inputStream The {@link InputStream} to read {@link XMLEvent}s from.
	 * @return The {@link Svg4j} instance for chaining.
	 * @throws XMLStreamException If an XML error occurs.
	 * @throws NullPointerException If the {@link SvgViewBox} or {@code id} are null.
	 */
	public Svg4j read(String id, InputStream inputStream) throws XMLStreamException {
		Preconditions.checkNotNull(id);

		XMLEventReader reader = input.createXMLEventReader(inputStream);

		try {
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();

				processors.stream()
					.parallel()
					.filter(processor -> processor.accepts(event))
					.forEach(processor -> processed.addAll(processor.process(id, event)));
			}
		} finally {
			reader.close();
		}

		return this;
	}

	/**
	 * Writes the {@link XMLEvent}s in this {@link XmlDocument} to a {@link File}.
	 * @param file The {@link File} to write to.
	 * @return The {@link Svg4j} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws FileNotFoundException If the file exists but is a directory rather than a regular file, does not exist
	 * but cannot be created, or cannot be opened for any other reason.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public Svg4j write(File file) throws IOException, XMLStreamException {
		return write(file.toPath());
	}

	/**
	 * Writes the {@link XMLEvent}s in this {@link XmlDocument} to a {@link File}.
	 * @param path The {@link Path} at which to write the {@link File}.
	 * @return The {@link Svg4j} instance for chaining.
	 * @throws IOException If an I/O error occurs.
	 * @throws FileNotFoundException If the file exists but is a directory rather than a regular file, does not exist
	 * but cannot be created, or cannot be opened for any other reason.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public Svg4j write(Path path) throws IOException, XMLStreamException {
		try (OutputStream outputStream = Files.newOutputStream(path)) {
			return write(outputStream);
		}
	}

	/**
	 * Writes the {@link XMLEvent}s in this {@link XmlDocument} to an {@link OutputStream}.
	 * @param outputStream The {@link OutputStream} to write the {@link XMLEvent}s to.
	 * @return The {@link Svg4j} instance for chaining.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public Svg4j write(OutputStream outputStream) throws XMLStreamException {
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
	 * Adds all of the {@link XMLEvent}s in this {@link XmlDocument} to an {@link XMLEventConsumer}.
	 * @param consumer The {@link XMLEventConsumer} to add the {@link XMLEvent}s to.
	 * @return The {@link Svg4j} instance for chaining.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public Svg4j addEventsTo(XMLEventConsumer consumer) throws XMLStreamException {
		for (XMLEvent event : processed) {
			consumer.add(event);
		}
		return this;
	}

	/**
	 * Gets the number of processed {@link XMLEvent}s.
	 * @return The number of processed {@link XMLEvent}s.
	 */
	public int size() {
		return processed.size();
	}
}
