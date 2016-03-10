package com.mikebull94.svg4j.xml.svg.processor;

import com.mikebull94.svg4j.svg.SvgDocument;
import org.mockito.Mockito;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.mikebull94.svg4j.svg.SvgDocument.EMBEDDED_SVG_TAG;
import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.eventIsOfType;
import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.resultContains;
import static java.util.Collections.emptyIterator;

/**
 * Contains utility methods for dictating/reacting to the behaviour of an {@link EndElement}.
 */
final class EndElementBehaviour {
	private static final XMLEventFactory events = XMLEventFactory.newFactory();

	public static BiConsumer<XmlEventProcessorTester, XMLEvent> eventIsEndElement(String name) {
		return (tester, event) -> {
			eventIsOfType(XMLStreamConstants.END_ELEMENT);
			Mockito.when(event.isEndElement()).thenReturn(true);
			Mockito.when(event.asEndElement()).thenReturn(tester.getEndElement());
			Mockito.when(event.asEndElement().getName()).thenReturn(new QName(SvgDocument.NAMESPACE_URI, name));
		};
	}

	public static Consumer<XMLEvent> eventIsNotEndElement() {
		return event -> Mockito.when(event.isEndElement()).thenReturn(false);
	}

	public static Predicate<XmlEventProcessorTester> resultContainsSvgEndElement() {
		return tester -> {
			EndElement expected = events.createEndElement(EMBEDDED_SVG_TAG, emptyIterator());
			return resultContains(expected).test(tester);
		};
	}

	private EndElementBehaviour() {
		/* empty */
	}
}
