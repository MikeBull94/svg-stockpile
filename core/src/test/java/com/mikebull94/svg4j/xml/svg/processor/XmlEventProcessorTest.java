package com.mikebull94.svg4j.xml.svg.processor;

import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.xml.svg.SvgDocument;
import org.junit.Before;
import org.mockito.Mock;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;
import java.util.Objects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Base test class for {@link XmlEventProcessor}s.
 */
public abstract class XmlEventProcessorTest {
	protected static final XMLEventFactory events = XMLEventFactory.newFactory();

	protected XmlEventProcessor testee;
	protected Boolean accepted;
	protected ImmutableList<XMLEvent> processResult;

	@Mock
	protected XMLEvent event;

	@Mock
	private StartElement startElement;

	@Mock
	private EndElement endElement;

	public abstract XmlEventProcessor createTestee();

	@Before
	public void setUp() {
		accepted = null;
		processResult = null;
		testee = createTestee();
	}

	protected final void givenEventIsOfType(int type) {
		when(event.getEventType()).thenReturn(type);
	}

	protected final void givenEventIsStartElement() {
		givenEventIsOfType(XMLStreamConstants.START_ELEMENT);
		when(event.isStartElement()).thenReturn(true);
		when(event.asStartElement()).thenReturn(startElement);
	}

	protected final void givenEventIsNotStartElement() {
		when(event.isStartElement()).thenReturn(false);
	}

	protected final void givenStartElementHasName(String name) {
		when(startElement.getName()).thenReturn(new QName(SvgDocument.NAMESPACE_URI, name));
	}

	protected final void givenEventIsEndElement() {
		givenEventIsOfType(XMLStreamConstants.END_ELEMENT);
		when(event.isEndElement()).thenReturn(true);
		when(event.asEndElement()).thenReturn(endElement);
	}

	protected final void givenEventIsNotEndElement() {
		when(event.isEndElement()).thenReturn(false);
	}

	protected final void givenEndElementHasName(String name) {
		when(endElement.getName()).thenReturn(new QName(SvgDocument.NAMESPACE_URI, name));
	}

	protected final void whenAcceptanceCheck() {
		accepted = testee.accepts(event);
	}

	protected final void whenEventProcessed() {
		processResult = testee.process("test", event);
	}

	protected final void thenEventAccepted() {
		assertTrue(accepted);
	}

	protected final void thenEventRejected() {
		assertFalse(accepted);
	}

	protected final void thenEventProcessed() {
		thenResultContains(event);
	}

	protected final void thenResultContains(XMLEvent event) {
		for (XMLEvent processedEvent : processResult) {
			if (processedEvent.toString().equals(event.toString())) {
				return;
			}
		}
		fail();
	}

	@SuppressWarnings("unchecked")
	protected final void thenStartElementHasAttribute(String name, String value) {
		Iterator<Attribute> attributes = startElement.getAttributes();

		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			String localPart = attribute.getName().getLocalPart();

			if (Objects.equals(name, localPart) && Objects.equals(value, attribute.getValue())) {
				return;
			}
		}

		fail("Attributes " + attributes + " does not contain Attribute{name=\"" + name + "\", value=\"" + value + "\"}");
	}
}
