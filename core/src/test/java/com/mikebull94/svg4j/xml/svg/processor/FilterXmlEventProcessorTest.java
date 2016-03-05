package com.mikebull94.svg4j.xml.svg.processor;

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
		rejectEventOfType(XMLStreamConstants.START_ELEMENT);
		rejectEventOfType(XMLStreamConstants.END_ELEMENT);
		rejectEventOfType(XMLStreamConstants.PROCESSING_INSTRUCTION);
		rejectEventOfType(XMLStreamConstants.CHARACTERS);
		rejectEventOfType(XMLStreamConstants.COMMENT);
		rejectEventOfType(XMLStreamConstants.SPACE);
		rejectEventOfType(XMLStreamConstants.START_DOCUMENT);
		rejectEventOfType(XMLStreamConstants.END_DOCUMENT);
		rejectEventOfType(XMLStreamConstants.DTD);
	}

	@Test
	public void acceptUnfiltered() {
		acceptEventOfType(XMLStreamConstants.ENTITY_REFERENCE);
		acceptEventOfType(XMLStreamConstants.ATTRIBUTE);
		acceptEventOfType(XMLStreamConstants.CDATA);
		acceptEventOfType(XMLStreamConstants.NAMESPACE);
		acceptEventOfType(XMLStreamConstants.NOTATION_DECLARATION);
		acceptEventOfType(XMLStreamConstants.ENTITY_DECLARATION);
	}

	@Test
	public void processes() {
		XmlEventProcessorTester test = XmlEventProcessorTester.test(new FilterXmlEventProcessor());
		MockitoAnnotations.initMocks(test);

		test.given(eventIsOfType(XMLStreamConstants.ENTITY_REFERENCE))
			.when(process())
			.then(eventProcessed());
	}

	private static void rejectEventOfType(int type) {
		XmlEventProcessorTester test = XmlEventProcessorTester.test(new FilterXmlEventProcessor());
		MockitoAnnotations.initMocks(test);

		test.given(eventIsOfType(type))
			.when(acceptanceCheck())
			.then(eventRejected());
	}

	private static void acceptEventOfType(int type) {
		XmlEventProcessorTester test = XmlEventProcessorTester.test(new FilterXmlEventProcessor());
		MockitoAnnotations.initMocks(test);

		test.given(eventIsOfType(type))
			.when(acceptanceCheck())
			.then(eventAccepted());
	}
}
