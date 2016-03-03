package com.mikebull94.svg4j;

import com.google.common.base.MoreObjects;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

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
	private static final Path DIRECTORY = Paths.get("core/src/main/resources");
	private static final Path OUTPUT = Paths.get("core/build/resources/main/output.svg");
	private static final Predicate<Path> HAS_SVG_EXTENSION = path -> path.toString().endsWith(".svg");

	public static void main(String... args) {
		try {
			logger.info("Starting svg4j...");
			Svg4j svg4j = new Svg4j();

			XmlDocument stacked = svg4j.stack(VIEW_BOX, PathUtils.listRecursive(DIRECTORY, HAS_SVG_EXTENSION));
			Path output = stacked.write(OUTPUT);
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
		Path filename = path.getFileName();

		if (filename == null) {
			throw new IOException("Path filename has zero elements " + path);
		}

		logger.info("Stacking: {}", path);
		String id = filename.toString().substring(0, filename.toString().lastIndexOf('.'));

		try (InputStream inputStream = new FileInputStream(path.toFile())) {
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Svg4j svg4j = (Svg4j) o;
		return Objects.equals(factory, svg4j.factory);
	}

	@Override
	public int hashCode() {
		return Objects.hash(factory);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("factory", factory)
			.toString();
	}
}
