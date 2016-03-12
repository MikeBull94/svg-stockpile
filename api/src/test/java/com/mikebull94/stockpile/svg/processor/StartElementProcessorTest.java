package com.mikebull94.stockpile.svg.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static com.mikebull94.stockpile.svg.processor.StartElementBehaviour.eventIsNotStartElement;
import static com.mikebull94.stockpile.svg.processor.StartElementBehaviour.eventIsStartElement;
import static com.mikebull94.stockpile.svg.processor.StartElementBehaviour.nonSvgAttributesRemoved;
import static com.mikebull94.stockpile.svg.processor.StartElementBehaviour.startElementHasAttributes;
import static com.mikebull94.stockpile.svg.processor.StartElementBehaviour.svgAttributesRemain;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.acceptanceCheck;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.eventAccepted;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.eventRejected;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.process;

/**
 * Contains unit tests for the {@link StartElementProcessor}.
 */
public final class StartElementProcessorTest {
	private XmlEventProcessorTester test;

	@Before
	public void setUp() {
		test = XmlEventProcessorTester.test(new StartElementProcessor());
		MockitoAnnotations.initMocks(test);
	}

	@Test
	public void rejectNonStartElement() {
		test.given(eventIsNotStartElement())
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void rejectUnoptimized() {
		test.given(eventIsStartElement("metadata"))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void rejectSvgTag() {
		test.given(eventIsStartElement("svg"))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void rejectSvgGroupTag() {
		test.given(eventIsStartElement("g"))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void acceptStartElement() {
		test.given(eventIsStartElement("rect"))
			.when(acceptanceCheck())
			.then(eventAccepted());
	}

	@Test
	public void testProcess() {
		test.given(eventIsStartElement("path"))
			.given(startElementHasAttributes())
			.when(process())
			.then(nonSvgAttributesRemoved())
			.then(svgAttributesRemain());
	}
}
