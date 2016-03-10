package com.mikebull94.svg4j.svg.processor;

import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.svg.SvgDocument;
import com.mikebull94.svg4j.xml.XmlEventProcessor;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An {@link XmlEventProcessor} that accepts {@link StartElement}s whose {@link QName} does not equal
 * {@link SvgDocument#SVG_TAG} or {@link SvgDocument#GROUP_TAG}.
 */
public final class StartElementProcessor implements XmlEventProcessor {

	/**
	 * Used to create {@link StartElement}s with non-SVG {@link Attribute}s removed.
	 */
	private static final XMLEventFactory events = XMLEventFactory.newFactory();

	/**
	 * Modifies a {@link StartElement}, removing attributes that do not have a {@link QName#getNamespaceURI()} that
	 * equals {@link SvgDocument#NAMESPACE_URI}.
	 * @param element The {@link StartElement} to remove attributes from.
	 * @return The modified {@link StartElement}.
	 */
	@SuppressWarnings("unchecked")
	private static XMLEvent removeNonSvgAttributes(StartElement element) {
		Iterator<Attribute> original = element.getAttributes();
		Collection<Attribute> modified = new ArrayList<>();

		while (original.hasNext()) {
			Attribute attribute = original.next();
			QName qName = attribute.getName();
			String namespaceUri = qName.getNamespaceURI();

			if (namespaceUri.isEmpty() || namespaceUri.equals(SvgDocument.NAMESPACE_URI)) {
				modified.add(attribute);
			}
		}

		return events.createStartElement(element.getName(), modified.iterator(), element.getNamespaces());
	}

	@Override
	public boolean accepts(XMLEvent event) {
		if (!event.isStartElement()) {
			return false;
		}

		QName name = event.asStartElement().getName();
		return SvgDocument.optimized(name) && !name.equals(SvgDocument.SVG_TAG) && !name.equals(SvgDocument.GROUP_TAG);
	}

	@Override
	public ImmutableList<XMLEvent> process(String id, XMLEvent event) {
		return ImmutableList.of(removeNonSvgAttributes(event.asStartElement()));
	}
}
