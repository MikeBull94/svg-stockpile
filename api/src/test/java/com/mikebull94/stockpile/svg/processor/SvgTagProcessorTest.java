package com.mikebull94.stockpile.svg.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static com.mikebull94.stockpile.svg.processor.EndElementBehaviour.eventIsEndElement;
import static com.mikebull94.stockpile.svg.processor.EndElementBehaviour.eventIsNotEndElement;
import static com.mikebull94.stockpile.svg.processor.EndElementBehaviour.resultContainsSvgEndElement;
import static com.mikebull94.stockpile.svg.processor.StartElementBehaviour.eventIsNotStartElement;
import static com.mikebull94.stockpile.svg.processor.StartElementBehaviour.eventIsStartElement;
import static com.mikebull94.stockpile.svg.processor.StartElementBehaviour.resultContainsSvgStartElement;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.acceptanceCheck;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.eventAccepted;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.eventRejected;
import static com.mikebull94.stockpile.svg.processor.XmlEventBehaviour.process;

/**
 * Contains unit tests for the {@link SvgTagProcessor}.
 */
public final class SvgTagProcessorTest {
	private XmlEventProcessorTester test;

	@Before
	public void setUp() {
		test = XmlEventProcessorTester.test(new SvgTagProcessor());
		MockitoAnnotations.initMocks(test);
	}

	@Test
	public void rejectNonStartOrEndElement() {
		test.given(eventIsNotStartElement())
			.given(eventIsNotEndElement())
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void rejectUnoptimizedEvent() {
		test.given(eventIsStartElement("metadata"))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void rejectNonSvgTag() {
		test.given(eventIsEndElement("path"))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	@Test
	public void acceptSvgTag() {
		test.given(eventIsStartElement("svg"))
			.when(acceptanceCheck())
			.then(eventAccepted());
	}

	@Test
	public void processesStartElement() {
		test.given(eventIsStartElement("svg"))
			.when(process())
			.then(resultContainsSvgStartElement());
	}

	@Test
	public void processEndElement() {
		test.given(eventIsEndElement("svg"))
			.when(process())
			.then(resultContainsSvgEndElement());
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsToProcessNonStartOrEndElement() {
		test.given(eventIsNotStartElement())
			.given(eventIsNotEndElement())
			.when(process());
	}
}
