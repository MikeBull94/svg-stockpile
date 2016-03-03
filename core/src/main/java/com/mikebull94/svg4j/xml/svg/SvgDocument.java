package com.mikebull94.svg4j.xml.svg;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mikebull94.svg4j.xml.XmlDocument;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Utility class containing constants related to the <a href="https://www.w3.org/TR/SVG/">SVG</a> document specification.
 * @see <a href="https://www.w3.org/Graphics/SVG/">Scalable Vector Graphics</a>
 * @see <a href="https://www.w3.org/2000/svg">SVG namespace</a>
 */
public final class SvgDocument {

	/**
	 * Used to create SVG {@link StartElement}s and {@link EndElement}s.
	 */
	private static final XMLEventFactory events = XMLEventFactory.newFactory();

	/**
	 * The namespace for SVG documents.
	 */
	public static final String NAMESPACE = "svg";

	/**
	 * The URI to the namespace for SVG documents.
	 */
	public static final String NAMESPACE_URI = "http://www.w3.org/2000/svg";

	/**
	 * The {@code <svg>} tag.
	 */
	public static final QName SVG_TAG = new QName(NAMESPACE_URI, "svg");

	/**
	 * The {@code <g>} (group) tag.
	 */
	public static final QName GROUP_TAG = new QName(NAMESPACE_URI, "g");

	/**
	 * The {@code <metadata>} tag.
	 */
	public static final QName METADATA_TAG = new QName(NAMESPACE_URI, "metadata");

	/**
	 * The {@code <defs>} (definitions) tag.
	 */
	public static final QName DEFS_TAG = new QName(NAMESPACE_URI, "defs");

	/**
	 * The {@link <svg:svg>} tag.
	 */
	public static final QName EMBEDDED_SVG_TAG = new QName(NAMESPACE_URI, "svg", "svg");

	/**
	 * The {@code <svg:style>} tag.
	 */
	public static final QName STYLE_TAG = new QName(NAMESPACE_URI, "style", "svg");

	/**
	 * An {@link ImmutableSet} of {@link QName}s to deem invalid with regards to an optimized SVG specification.
	 */
	private static final ImmutableSet<QName> INVALID = ImmutableSet.of(
		METADATA_TAG,
		DEFS_TAG,
		GROUP_TAG
	);

	/**
	 * A flag indicating whether a {@link QName} is valid for the optimized SVG specification.
	 * @param name The {@link QName}.
	 * @return {@code true} if so, {@code false} otherwise.
	 */
	public static boolean valid(QName name) {
		return name.getNamespaceURI().equals(NAMESPACE_URI) && !INVALID.contains(name);
	}

	/**
	 * Creates a {@link StartElement} with the {@code <svg>} tag.
	 * @param viewBox The view-box of the SVG
	 * @return The {@link StartElement}.
	 */
	public static XMLEvent svgStart(SvgViewBox viewBox) {
		Collection<Attribute> attributes = new ArrayList<>();
		attributes.add(events.createAttribute(XmlDocument.NAMESPACE, NAMESPACE_URI));
		attributes.add(events.createAttribute(XmlDocument.NAMESPACE + ":" + NAMESPACE, NAMESPACE_URI));
		attributes.add(events.createAttribute("width", String.valueOf(viewBox.getWidth())));
		attributes.add(events.createAttribute("height", String.valueOf(viewBox.getHeight())));
		attributes.add(events.createAttribute("viewBox", viewBox.format()));
		return events.createStartElement(SVG_TAG, attributes.iterator(), Collections.emptyIterator());
	}

	/**
	 * Creates an {@link ImmutableList} of {@link XMLEvent}s related to hiding embedded SVGs.
	 * @return The {@link ImmutableList} of {@link XMLEvent}s.
	 */
	public static ImmutableList<XMLEvent> hideEmbeddedSvgsStyle() {
		return ImmutableList.of(
			events.createStartElement(STYLE_TAG, Collections.emptyIterator(), Collections.emptyIterator()),
			events.createCharacters(".i {display:none;}.i:target {display:block;}"),
			events.createEndElement(STYLE_TAG, Collections.emptyIterator())
		);
	}

	/**
	 * Creates a {@link EndElement} with the {@code <svg>} tag.
	 * @return The {@link EndElement}.
	 */
	public static XMLEvent svgEnd() {
		return events.createEndElement(SVG_TAG, Collections.emptyIterator());
	}

	private SvgDocument() {
		/* empty */
	}
}
