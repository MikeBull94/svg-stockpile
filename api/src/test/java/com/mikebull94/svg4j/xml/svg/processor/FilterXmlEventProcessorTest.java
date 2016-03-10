package com.mikebull94.svg4j.xml.svg.processor;

import com.mikebull94.svg4j.svg.processor.FilterXmlEventProcessor;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import javax.xml.stream.XMLStreamConstants;

import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.acceptanceCheck;
import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.eventAccepted;
import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.eventIsOfType;
import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.eventProcessed;
import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.eventRejected;
import static com.mikebull94.svg4j.xml.svg.processor.XmlEventBehaviour.process;

/**
 * Contains unit tests for the {@link FilterXmlEventProcessor}.
 */
public final class FilterXmlEventProcessorTest {
	@Test
	public void rejectFiltered() {
		rejectEventsOfType(XMLStreamConstants.START_ELEMENT,
			XMLStreamConstants.END_ELEMENT,
			XMLStreamConstants.PROCESSING_INSTRUCTION,
			XMLStreamConstants.CHARACTERS,
			XMLStreamConstants.COMMENT,
			XMLStreamConstants.SPACE,
			XMLStreamConstants.START_DOCUMENT,
			XMLStreamConstants.END_DOCUMENT,
			XMLStreamConstants.DTD
		);
	}

	@Test
	public void acceptUnfiltered() {
		acceptEventsOfType(XMLStreamConstants.ENTITY_REFERENCE,
			XMLStreamConstants.ATTRIBUTE,
			XMLStreamConstants.CDATA,
			XMLStreamConstants.NAMESPACE,
			XMLStreamConstants.NOTATION_DECLARATION,
			XMLStreamConstants.ENTITY_DECLARATION
		);
	}

	@Test
	public void processes() {
		XmlEventProcessorTester test = XmlEventProcessorTester.test(new FilterXmlEventProcessor());
		MockitoAnnotations.initMocks(test);

		test.given(eventIsOfType(XMLStreamConstants.ENTITY_REFERENCE))
			.when(process())
			.then(eventProcessed());
	}

	private static void rejectEventsOfType(int... types) {
		for (int type : types) {
			rejectEventOfType(type);
		}
	}

	private static void rejectEventOfType(int type) {
		XmlEventProcessorTester test = XmlEventProcessorTester.test(new FilterXmlEventProcessor());
		MockitoAnnotations.initMocks(test);

		test.given(eventIsOfType(type))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	private static void acceptEventsOfType(int... types) {
		for (int type : types) {
			acceptEventOfType(type);
		}
	}

	private static void acceptEventOfType(int type) {
		XmlEventProcessorTester test = XmlEventProcessorTester.test(new FilterXmlEventProcessor());
		MockitoAnnotations.initMocks(test);

		test.given(eventIsOfType(type))
			.when(acceptanceCheck())
			.then(eventAccepted());
	}
}
