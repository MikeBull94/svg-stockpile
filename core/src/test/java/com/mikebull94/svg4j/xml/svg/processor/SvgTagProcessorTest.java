package com.mikebull94.svg4j.xml.svg.processor;

import com.mikebull94.svg4j.xml.svg.SvgDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.emptyIterator;

/**
 * Contains unit tests for the {@link SvgTagProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class SvgTagProcessorTest extends XmlEventProcessorTest {
	@Override
	public XmlEventProcessor createTestee() {
		return new SvgTagProcessor();
	}

	@Test
	public void rejectNonStartOrEndElement() {
		givenEventIsNotEndElement();
		givenEventIsNotStartElement();
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void rejectUnoptimized() {
		givenEventIsStartElement();
		givenStartElementHasName("metadata");
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void rejectNonSvgTag() {
		givenEventIsEndElement();
		givenEndElementHasName("path");
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void acceptSvgTag() {
		givenEventIsStartElement();
		givenStartElementHasName("svg");
		whenAcceptanceCheck();
		thenEventAccepted();
	}

	@Test
	public void processesStartElement() {
		givenEventIsStartElement();
		givenStartElementHasName("svg");
		whenEventProcessed();

		Collection<Attribute> attributes = new ArrayList<>();
		attributes.add(events.createAttribute("id", "test"));
		attributes.add(events.createAttribute("class", "i"));
		StartElement expected = events.createStartElement(SvgDocument.EMBEDDED_SVG_TAG, attributes.iterator(), emptyIterator());

		thenResultContains(expected);
	}

	@Test
	public void processesEndElement() {
		givenEventIsEndElement();
		givenEndElementHasName("svg");
		whenEventProcessed();

		EndElement expected = events.createEndElement(SvgDocument.EMBEDDED_SVG_TAG, emptyIterator());

		thenResultContains(expected);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsToProcessNonStartOrEndElement() {
		givenEventIsNotStartElement();
		givenEventIsNotEndElement();
		whenEventProcessed();
	}
}
