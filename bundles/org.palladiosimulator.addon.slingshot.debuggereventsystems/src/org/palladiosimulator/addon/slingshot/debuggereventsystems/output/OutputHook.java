package org.palladiosimulator.addon.slingshot.debuggereventsystems.output;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.SystemClearUpListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.SystemClearedUp;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.output.xml.Event;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.output.xml.EventSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.output.xml.Handler;

/**
 * Hooks into the clear up event of the debugger system to output
 * the event system.
 */
public class OutputHook implements SystemClearUpListener {

	@Override
	public void onEvent(SystemClearedUp listenerEvent) {
		final String outputFilePath = EventDebugSystem.getSettings().getEventOutputFile();
		
		if (outputFilePath == null || outputFilePath.isEmpty() || outputFilePath.isBlank()) {
			return;
		}
		
		final EventSystem eventSystem = buildSystem();
		
		switch (EventDebugSystem.getSettings().getOutputFormat()) {
		case GRAPH_VIZ:
			outputViz(outputFilePath, eventSystem);
			break;
		case XML:
			outputXML(outputFilePath, eventSystem);
			break;
		default:
			break;
		
		}
	}
	
	private void outputXML(final String outputFilePath, final EventSystem eventSystem) {
		try {
			final JAXBContext context = JAXBContext.newInstance(EventSystem.class);
			final Marshaller mar = context.createMarshaller();
			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			final File file = new File(outputFilePath);
			mar.marshal(eventSystem, file);
		} catch (JAXBException e) {
			throw new RuntimeException("Could not output XML file due to some exception.", e);
		}
	}
	
	private void outputViz(final String outputFilePath, final EventSystem eventSystem) {
		final StringBuilder viz = new StringBuilder();
		viz.append("strict digraph {");
		
		/* Specify node types and nodes */
			viz.append("\t{");
				viz.append("\t\tnode [shape=hexagon]");
				eventSystem.getEvents().forEach(event -> {
					viz.append("\t\t");
					viz.append(event.getId());
					viz.append(" [name=");
					viz.append(event.getName());
					viz.append("]\r\n");
				});
			viz.append("\t}");
		
		/* add edges */
		eventSystem.getHandlers().forEach(handler -> {
			viz.append(handler.getEventId());
			viz.append(" -> ");
		});
			
		viz.append("}");
	}
	
	/**
	 * Builds the event graph from the cache.
	 * 
	 * @return The event graph.
	 */
	private EventSystem buildSystem() {
		final EventSystem result = new EventSystem();
		
		final List<Event> events = new LinkedList<>();
		for (final Map.Entry<DebugEventId, IDebugEvent> event : EventDebugSystem.getEventHolder().eventIterator()) {
			events.add(Event.fromDebugEvent(event.getValue()));
		}
		
		final List<Handler> handlers = new LinkedList<>();
		for (final Map.Entry<HandlerId, IDebugEventHandler> handler : EventDebugSystem.getEventHolder().handlerIterator()) {
			handlers.add(Handler.from(handler.getValue()));
		}
		
		result.setEvents(events);
		result.setHandlers(handlers);
		
		return result;
	}
}
