package com.mikebull94.svg4j.xml.svg.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Contains unit tests for the {@link EndElementProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class EndElementProcessorTest extends XmlEventProcessorTest {
	@Override
	public XmlEventProcessor createTestee() {
		return new EndElementProcessor();
	}

	@Test
	public void rejectNonEndElement() {
		givenEventIsNotEndElement();
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void rejectUnoptimized() {
		givenEventIsEndElement();
		givenEndElementHasName("defs");
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void rejectSvgTag() {
		givenEventIsEndElement();
		givenEndElementHasName("svg");
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void rejectSvgGroupTag() {
		givenEventIsEndElement();
		givenEndElementHasName("g");
		whenAcceptanceCheck();
		thenEventRejected();
	}

	@Test
	public void acceptEndElement() {
		givenEventIsEndElement();
		givenEndElementHasName("rect");
		whenAcceptanceCheck();
		thenEventAccepted();
	}

	@Test
	public void processesEndElement() {
		givenEventIsEndElement();
		givenEndElementHasName("path");
		whenEventProcessed();
		thenEventProcessed();
	}
}
