package com.mikebull94.svg4j.xml.svg.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.stream.XMLStreamConstants;

/**
 * Contains unit tests for the {@link EndElementProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class FilterXmlEventProcessorTest extends XmlEventProcessorTest {
	@Override
	public XmlEventProcessor createTestee() {
		return new FilterXmlEventProcessor();
	}

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
		givenEventIsOfType(XMLStreamConstants.ENTITY_REFERENCE);
		whenEventProcessed();
		thenEventProcessed();
	}

	private void rejectEventOfType(int type) {
		givenEventIsOfType(type);
		whenAcceptanceCheck();
		thenEventRejected();
	}

	private void acceptEventOfType(int type) {
		givenEventIsOfType(type);
		whenAcceptanceCheck();
		thenEventAccepted();
	}
}
