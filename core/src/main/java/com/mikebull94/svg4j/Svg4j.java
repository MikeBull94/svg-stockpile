package com.mikebull94.svg4j;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.util.PathUtils;
import com.mikebull94.svg4j.xml.XmlDocument;
import com.mikebull94.svg4j.xml.XmlDocumentFactory;
import com.mikebull94.svg4j.xml.svg.SvgDocument;
import com.mikebull94.svg4j.xml.svg.SvgViewBox;
import com.mikebull94.svg4j.xml.svg.processor.EndElementProcessor;
import com.mikebull94.svg4j.xml.svg.processor.FilterXmlEventProcessor;
import com.mikebull94.svg4j.xml.svg.processor.StartElementProcessor;
import com.mikebull94.svg4j.xml.svg.processor.SvgTagProcessor;
import com.mikebull94.svg4j.xml.svg.processor.XmlEventProcessor;
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
import java.util.function.Predicate;

import static com.google.common.io.Files.getNameWithoutExtension;

/**
 * Svg4j is an optimizing and stacking tool for <a href="https://www.w3.org/Graphics/SVG/">Scalable Vector Graphics</a>.
 * <p>
 * This class defines an {@link XmlDocumentFactory} which will produce optimized and stacked SVG {@link XmlDocument}s
 * via a set of {@link XmlEventProcessor}s that deal with the {@link XMLEvent}s found in an SVG document; optimizing,
 * filtering, and finally stacking where appropriate.
 */
public final class Svg4j {
	private static final Logger logger = LoggerFactory.getLogger(Svg4j.class);

	private static final SvgViewBox VIEW_BOX = new SvgViewBox(0, 0, 500, 500);
	private static final Path INPUT_DIRECTORY = Paths.get("core", "src", "main", "resources");
	private static final Path OUTPUT_SVG = Paths.get("core", "build", "resources", "main", "output.svg");

	private static final Predicate<Path> HAS_SVG_EXTENSION = PathUtils::hasSvgExtension;

	public static void main(String... args) {
		try {
			logger.info("Starting svg4j...");

			Svg4j svg4j = new Svg4j();
			ImmutableList<Path> input = PathUtils.filterPathsIn(INPUT_DIRECTORY, HAS_SVG_EXTENSION);
			XmlDocument stacked = svg4j.stack(VIEW_BOX, input);
			Path output = stacked.write(OUTPUT_SVG);

			logger.info("svg4j complete. Output: {}", output);
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
