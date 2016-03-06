package com.mikebull94.svg4j.xml.svg.processor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.xml.XmlEventProcessor;
import com.mikebull94.svg4j.xml.svg.SvgDocument;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.emptyIterator;

/**
 * An {@link XmlEventProcessor} that accepts {@link StartElement}s/{@link EndElement}s whose {@link QName} equals
 * {@link SvgDocument#SVG_TAG}.
 * <p>
 * If the {@link XMLEvent} is a {@link StartElement} then the processing returns an {@link ImmutableList} containing a
 * modified version of the provided {@link StartElement} with a CSS class {@link Attribute} which will prevent it from
 * being rendered unless accessed by a fragment identifier.
 * <p>
 * If the {@link XMLEvent} is an {@link EndElement} then the processing returns an {@link ImmutableList} containing
 * the {@link EndElement} itself.
 */
public final class SvgTagProcessor implements XmlEventProcessor {

	/**
	 * Used to create {@link StartElement}s and {@link EndElement}s.
	 */
	private static final XMLEventFactory events = XMLEventFactory.newFactory();

	/**
	 * The attribute key for the {@code id} {@link Attribute}.
	 */
	private static final String ID_KEY = "id";

	/**
	 * The attribute key for the hidden {@code class} {@link Attribute}.
	 */
	private static final String HIDDEN_CLASS_KEY = "class";

	/**
	 * The attribute value for the hidden {@code class} {@link Attribute}.
	 */
	private static final String HIDDEN_CLASS_VALUE = "i";

	/**
	 * Creates a {@link StartElement} with the {@link SvgDocument#EMBEDDED_SVG_TAG}.
	 * @param id The id {@link Attribute} value.
	 * @return The {@link StartElement}.
	 */
	private static XMLEvent embeddedSvgStartElement(String id) {
		Collection<Attribute> attributes = new ArrayList<>();
		attributes.add(events.createAttribute(ID_KEY, id));
		attributes.add(events.createAttribute(HIDDEN_CLASS_KEY, HIDDEN_CLASS_VALUE));
		return events.createStartElement(SvgDocument.EMBEDDED_SVG_TAG, attributes.iterator(), emptyIterator());
	}

	/**
	 * Creates an {@link EndElement} with the {@link SvgDocument#EMBEDDED_SVG_TAG}.
	 * @return The {@link EndElement}.
	 */
	private static XMLEvent embeddedSvgEndElement() {
		return events.createEndElement(SvgDocument.EMBEDDED_SVG_TAG, emptyIterator());
	}

	@Override
	public boolean accepts(XMLEvent event) {
		QName name;

		if (event.isStartElement()) {
			name = event.asStartElement().getName();
		} else if (event.isEndElement()) {
			name = event.asEndElement().getName();
		} else {
			return false;
		}

		return SvgDocument.optimized(name) && name.equals(SvgDocument.SVG_TAG);
	}

	@Override
	public ImmutableList<XMLEvent> process(String id, XMLEvent event) {
		Preconditions.checkNotNull(id);

		if (event.isStartElement()) {
			return ImmutableList.of(embeddedSvgStartElement(id));
		} else if (event.isEndElement()) {
			return ImmutableList.of(embeddedSvgEndElement());
		} else {
			throw new IllegalArgumentException("Event must be start or end element.");
		}
	}
}
