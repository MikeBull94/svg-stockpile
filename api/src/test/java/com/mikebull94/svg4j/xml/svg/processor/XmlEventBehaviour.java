package com.mikebull94.svg4j.xml.svg.processor;

import com.mikebull94.svg4j.xml.XmlEventProcessor;
import org.mockito.Mockito;

import javax.xml.stream.events.XMLEvent;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Contains utility methods for dictating/reacting to the behaviour of an {@link XMLEvent}.
 */
final class XmlEventBehaviour {
	public static final String ATTRIBUTE_TEST_ID = "test";

	public static Consumer<XMLEvent> eventIsOfType(int type) {
		return event -> Mockito.when(event.getEventType()).thenReturn(type);
	}

	public static BiPredicate<XmlEventProcessor, XMLEvent> acceptanceCheck() {
		return XmlEventProcessor::accepts;
	}

	public static BiFunction<XmlEventProcessor, XMLEvent, ? extends List<XMLEvent>> process() {
		return (processor, event) -> processor.process(ATTRIBUTE_TEST_ID, event);
	}

	public static Predicate<XmlEventProcessorTester> eventAccepted() {
		return XmlEventProcessorTester::isEventAccepted;
	}

	public static Predicate<XmlEventProcessorTester> eventRejected() {
		return tester -> !tester.isEventAccepted();
	}

	public static BiPredicate<XmlEventProcessorTester, XMLEvent> eventProcessed() {
		return (tester, event) -> resultContains(event).test(tester);
	}

	public static Predicate<XmlEventProcessorTester> resultContains(XMLEvent event) {
		return tester -> tester.processResultContains(event);
	}

	private XmlEventBehaviour() {
		/* empty */
	}
}
