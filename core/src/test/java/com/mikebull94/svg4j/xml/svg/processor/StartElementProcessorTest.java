package com.mikebull94.svg4j.xml.svg.processor;

import com.mikebull94.svg4j.xml.svg.SvgDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * Contains unit tests for the {@link StartElementProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class StartElementProcessorTest extends XmlEventProcessorTest {
	@Override
	public XmlEventProcessor createTestee() {
		return new StartElementProcessor();
	}

	@Test
	public void rejectNonStartElement() {
		givenEventIsNotStartElement();
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void rejectUnoptimized() {
		givenEventIsStartElement();
		givenStartElementHasName("g");
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void rejectSvgTag() {
		givenEventIsStartElement();
		givenStartElementHasName("svg");
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void rejectSvgGroupTag() {
		givenEventIsStartElement();
		givenStartElementHasName("g");
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void acceptStartElement() {
		givenEventIsStartElement();
		givenStartElementHasName("rect");
		whenAcceptanceCheck();
		thenEventAccepted();
	}

	@Test
	public void processRemovesNonSvgAttributes() {
		givenEventIsStartElement();
		givenStartElementHasName("path");
		givenStartElementHasAttributes();
		whenEventProcessed();
		thenNonSvgAttributesRemoved();
	}

	@Test
	public void processRetainsSvgAttributes() {
		givenEventIsStartElement();
		givenStartElementHasName("square");
		givenStartElementHasAttributes();
		whenEventProcessed();
		thenSvgAttributesRemain();
	}

	private void givenStartElementHasAttributes() {
		Collection<Attribute> attributes = new ArrayList<>();
		QName name = new QName("http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd", "name", "sodipodi");

		attributes.add(events.createAttribute("", SvgDocument.NAMESPACE_URI, "path", "0"));
		attributes.add(events.createAttribute("", SvgDocument.NAMESPACE_URI, "rect", "0"));
		attributes.add(events.createAttribute(name, "inkscape:something"));

		when(event.asStartElement().getAttributes()).thenReturn(attributes.iterator());
	}

	private void thenSvgAttributesRemain() {
		StartElement element = processResult.get(0).asStartElement();
		Attribute path = element.getAttributeByName(new QName(SvgDocument.NAMESPACE_URI, "path"));
		Attribute rect = element.getAttributeByName(new QName(SvgDocument.NAMESPACE_URI, "rect"));

		assertNotNull("Missing path attribute", path);
		assertNotNull("Missing rect attribute", rect);
	}

	private void thenNonSvgAttributesRemoved() {
		StartElement element = processResult.get(0).asStartElement();
		QName name = new QName("http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd", "name", "sodipodi");
		Attribute attribute = element.getAttributeByName(name);

		assertNull("Non-svg attribute still remains", attribute);
	}
}
