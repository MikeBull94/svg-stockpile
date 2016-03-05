package com.mikebull94.svg4j.xml.svg.processor;

import com.google.common.base.Preconditions;
import org.mockito.Mock;

import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;

/**
 * Provides a behaviour driven development framework for testing {@link XmlEventProcessor}s.
 */
public final class XmlEventProcessorTester {
	private static final String PREDICATE_FAILURE = "Predicate.test returned false";

	public static XmlEventProcessorTester test(XmlEventProcessor testee) {
		return new XmlEventProcessorTester(testee);
	}

	@Mock
	private XMLEvent event;

	@Mock
	private StartElement startElement;

	@Mock
	private EndElement endElement;

	private boolean eventAccepted = true;
	private List<XMLEvent> processedResults;
	private final XmlEventProcessor testee;

	private XmlEventProcessorTester(XmlEventProcessor testee) {
		this.testee = Preconditions.checkNotNull(testee);
	}

	public XmlEventProcessorTester given(BiConsumer<XmlEventProcessorTester, XMLEvent> consumer) {
		consumer.accept(this, event);
		return this;
	}

	public XmlEventProcessorTester given(Consumer<XMLEvent> consumer) {
		consumer.accept(event);
		return this;
	}

	public XmlEventProcessorTester when(BiFunction<XmlEventProcessor, XMLEvent, ? extends List<XMLEvent>> function) {
		processedResults = function.apply(testee, event);
		return this;
	}

	public XmlEventProcessorTester when(BiPredicate<XmlEventProcessor, XMLEvent> predicate) {
		eventAccepted &= predicate.test(testee, event);
		return this;
	}

	public XmlEventProcessorTester then(BiPredicate<XmlEventProcessorTester, XMLEvent> predicate) {
		return then(PREDICATE_FAILURE, predicate);
	}

	public XmlEventProcessorTester then(String message, BiPredicate<XmlEventProcessorTester, XMLEvent> predicate) {
		assertTrue(message, predicate.test(this, event));
		return this;
	}

	public XmlEventProcessorTester then(Predicate<XmlEventProcessorTester> predicate) {
		return then(PREDICATE_FAILURE, predicate);
	}

	public XmlEventProcessorTester then(String message, Predicate<XmlEventProcessorTester> predicate) {
		assertTrue(message, predicate.test(this));
		return this;
	}

	public EndElement getEndElement() {
		return endElement;
	}

	public StartElement getStartElement() {
		return startElement;
	}

	public boolean isEventAccepted() {
		return eventAccepted;
	}

	public XMLEvent getProcessedResult(int index) {
		return processedResults.get(index);
	}

	public boolean processResultContains(XMLEvent event) {
		for (XMLEvent processed : processedResults) {
			if (processed.toString().equals(event.toString())) {
				return true;
			}
		}
		return false;
	}
}
