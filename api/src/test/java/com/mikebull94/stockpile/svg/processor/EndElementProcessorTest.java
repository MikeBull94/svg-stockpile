package com.mikebull94.stockpile.svg.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static com.mikebull94.stockpile.svg.processor.EndElementBehaviour.eventIsEndElement;
import static com.mikebull94.stockpile.svg.processor.EndElementBehaviour.eventIsNotEndElement;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.acceptanceCheck;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.eventAccepted;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.eventProcessed;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.eventRejected;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.process;

/**
 * Contains unit tests for the {@link EndElementProcessor}.
 */
public final class EndElementProcessorTest {
	private XmlEventProcessorTester test;

	@Before
	public void setUp() {
		test = XmlEventProcessorTester.test(new EndElementProcessor());
		MockitoAnnotations.initMocks(test);
	}

	@Test
	public void rejectNonEndElement() {
		test.given(eventIsNotEndElement())
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void rejectUnoptimizedEvent() {
		test.given(eventIsEndElement("defs"))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void rejectSvgTag() {
		test.given(eventIsEndElement("svg"))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void rejectSvgGroupTag() {
		test.given(eventIsEndElement("g"))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void testAccept() {
		test.given(eventIsEndElement("rect"))
			.when(acceptanceCheck())
			.then(eventAccepted());
	}

	@Test
	public void testProcess() {
		test.given(eventIsEndElement("rect"))
			.when(process())
			.then(eventProcessed());
	}
}
