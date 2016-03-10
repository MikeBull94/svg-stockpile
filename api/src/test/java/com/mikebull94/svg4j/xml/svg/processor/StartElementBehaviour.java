package com.mikebull94.svg4j.xml.svg.processor;

import com.mikebull94.svg4j.svg.SvgDocument;
import org.mockito.Mockito;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.mikebull94.svg4j.svg.SvgDocument.EMBEDDED_SVG_TAG;
import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.eventIsOfType;
import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.resultContains;
import static java.util.Collections.emptyIterator;

/**
 * Contains utility methods for dictating/reacting to the behaviour of an {@link StartElement}.
 */
final class StartElementBehaviour {
	private static final XMLEventFactory events = XMLEventFactory.newFactory();

	public static BiConsumer<XmlEventProcessorTester, XMLEvent> eventIsStartElement(String name) {
		return (tester, event) -> {
			eventIsOfType(XMLStreamConstants.START_ELEMENT);
			Mockito.when(event.isStartElement()).thenReturn(true);
			Mockito.when(event.asStartElement()).thenReturn(tester.getStartElement());
			Mockito.when(event.asStartElement().getName()).thenReturn(new QName(SvgDocument.NAMESPACE_URI, name));
		};
	}

	public static Consumer<XMLEvent> eventIsNotStartElement() {
		return event -> Mockito.when(event.isStartElement()).thenReturn(false);
	}

	public static Consumer<XMLEvent> startElementHasAttributes() {
		return event -> {
			Collection<Attribute> attributes = new ArrayList<>();
			QName name = new QName("http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd", "name", "sodipodi");

			attributes.add(events.createAttribute("", SvgDocument.NAMESPACE_URI, "path", "0"));
			attributes.add(events.createAttribute("", SvgDocument.NAMESPACE_URI, "rect", "0"));
			attributes.add(events.createAttribute(name, "inkscape:something"));

			Mockito.when(event.asStartElement().getAttributes()).thenReturn(attributes.iterator());
		};
	}

	public static Predicate<XmlEventProcessorTester> resultContainsSvgStartElement() {
		return tester -> {
			Collection<Attribute> attributes = new ArrayList<>();
			attributes.add(events.createAttribute("id", XmlEventBehaviour.ATTRIBUTE_TEST_ID));
			attributes.add(events.createAttribute("class", "i"));
			StartElement expected = events.createStartElement(EMBEDDED_SVG_TAG, attributes.iterator(), emptyIterator());
			return resultContains(expected).test(tester);
		};
	}

	public static Predicate<XmlEventProcessorTester> nonSvgAttributesRemoved() {
		return tester -> {
			StartElement element = tester.getProcessedResult(0).asStartElement();
			QName name = new QName("http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd", "name", "sodipodi");
			return element.getAttributeByName(name) == null;
		};
	}

	public static Predicate<XmlEventProcessorTester> svgAttributesRemain() {
		return tester -> {
			StartElement element = tester.getProcessedResult(0).asStartElement();
			Attribute path = element.getAttributeByName(new QName(SvgDocument.NAMESPACE_URI, "path"));
			Attribute rect = element.getAttributeByName(new QName(SvgDocument.NAMESPACE_URI, "rect"));
			return path != null && rect != null;
		};
	}

	private StartElementBehaviour() {
		/* empty */
	}
}
