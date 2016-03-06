package com.mikebull94.svg4j;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.util.PathUtils;
import com.mikebull94.svg4j.xml.XmlDocument;
import com.mikebull94.svg4j.xml.XmlDocumentFactory;
import com.mikebull94.svg4j.xml.XmlEventProcessor;
import com.mikebull94.svg4j.xml.svg.SvgDocument;
import com.mikebull94.svg4j.xml.svg.SvgViewBox;
import com.mikebull94.svg4j.xml.svg.processor.EndElementProcessor;
import com.mikebull94.svg4j.xml.svg.processor.FilterXmlEventProcessor;
import com.mikebull94.svg4j.xml.svg.processor.StartElementProcessor;
import com.mikebull94.svg4j.xml.svg.processor.SvgTagProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.google.common.io.Files.getNameWithoutExtension;

/**
 * Defines an {@link XmlDocumentFactory} which will produce optimized and stacked SVG {@link XmlDocument}s via a set of
 * {@link XmlEventProcessor}s that deal with the {@link XMLEvent}s found in an SVG document; optimizing, filtering, and
 * finally stacking where appropriate.
 */
public final class Svg4j {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Svg4j.class);

	/**
	 * The entry point of the program.
	 * @param args The program's arguments.
	 */
	public static void main(String... args) {
		try {
			logger.info("Starting svg4j...");
			Svg4j svg4j = new Svg4j();

			Path inputDir = Paths.get("api", "src", "main", "resources");
			ImmutableList<Path> input = PathUtils.filterPathsIn(inputDir, PathUtils::hasSvgExtension);

			SvgViewBox viewBox = new SvgViewBox(0, 0, 500, 500);
			XmlDocument stacked = svg4j.stack(viewBox, input);

			Path output = stacked.write(Paths.get("api", "build", "resources", "main", "output.svg"));
			logger.info("svg4j complete: {}", output);
		} catch (Throwable t) {
			logger.error("Failed to run svg4j.", t);
		}
	}

	/**
	 * The {@link XmlDocumentFactory} used to create optimized and stacked SVG {@link XmlDocument}s.
	 */
	private final XmlDocumentFactory factory = new XmlDocumentFactory(
		new FilterXmlEventProcessor(),
		new SvgTagProcessor(),
		new StartElementProcessor(),
		new EndElementProcessor()
	);

	/**
	 * Stacks {@link XMLEvent}s read from an array of {@link Path}s into an optimized SVG {@link XmlDocument}.
	 * @param viewBox The stacked SVG's view-box.
	 * @param paths The {@link Path}s to stack.
	 * @return The stacked and optimized SVG {@link XmlDocument}.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public XmlDocument stack(SvgViewBox viewBox, Path... paths) throws IOException, XMLStreamException {
		return stack(viewBox, Arrays.asList(paths));
	}

	/**
	 * Stacks {@link XMLEvent}s read from an {@link Iterable} of {@link Path}s into an optimized SVG {@link XmlDocument}.
	 * @param viewBox The stacked SVG's view-box.
	 * @param paths The {@link Path}s to stack.
	 * @return The stacked and optimized SVG {@link XmlDocument}.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public XmlDocument stack(SvgViewBox viewBox, Iterable<Path> paths) throws IOException, XMLStreamException {
		Preconditions.checkNotNull(viewBox);
		Preconditions.checkNotNull(paths);

		ImmutableList.Builder<XMLEvent> builder = ImmutableList.builder();

		builder.add(SvgDocument.svgStart(viewBox));
		builder.addAll(SvgDocument.hideEmbeddedSvgsStyle());

		for (Path path : paths) {
			builder.addAll(stack(path).getEvents());
		}

		builder.add(SvgDocument.svgEnd());

		return new XmlDocument(builder.build());
	}

	/**
	 * Stacks {@link XMLEvent}s read from a {@link Path} into an optimized SVG {@link XmlDocument}.
	 * @param path The {@link Path} to stack.
	 * @return The stacked and optimized SVG {@link XmlDocument}.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public XmlDocument stack(Path path) throws IOException, XMLStreamException {
		logger.info("Stacking: {}", path);

		Path fileName = path.getFileName();

		if (fileName == null) {
			throw new IllegalArgumentException("Path " + path + " has zero elements.");
		}

		String id = getNameWithoutExtension(fileName.toString());

		try (InputStream inputStream = Files.newInputStream(path)) {
			return stack(id, inputStream);
		}
	}

	/**
	 * Stacks {@link XMLEvent}s read from an {@link InputStream} into an optimized SVG {@link XmlDocument}.
	 * @param inputStream The {@link InputStream} to read {@link XMLEvent}s from.
	 * @return The stacked and optimized SVG {@link XmlDocument}.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	public XmlDocument stack(String id, InputStream inputStream) throws XMLStreamException {
		XmlDocument document = factory.create(id, inputStream);
		logger.info("Stacked {} XML events into #{}", document.getEvents().size(), id);
		return document;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("factory", factory)
			.toString();
	}
}
