package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.view;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

public class EventHolder {

	private static final EventHolder INSTANCE = new EventHolder();

	private final Map<String, IDebugEvent> events = new ConcurrentHashMap<>();
	private final Map<String, List<String>> eventToHandlersMap = new ConcurrentHashMap<>();
	private final Map<String, IDebugEventHandler> handlers = new ConcurrentHashMap<>();

	public static void addEvent(final IDebugEvent event) {
		INSTANCE.events.put(event.getId(), event);
	}

	public static void addHandler(final String eventId, final IDebugEventHandler handler) {
		INSTANCE.eventToHandlersMap.computeIfAbsent(eventId, e -> new LinkedList<>())
				.add(handler.getId());
		INSTANCE.handlers.put(handler.getId(), handler);
	}


	public static List<IDebugEventHandler> getHandlersByEvent(final String eventId) {
		return INSTANCE.eventToHandlersMap
				.getOrDefault(eventId, Collections.emptyList())
				.stream()
				.map(handlerId -> INSTANCE.handlers.get(handlerId))
				.filter(handler -> handler != null)
				.collect(Collectors.toList());
	}

	public static boolean updateHandler(final IDebugEventHandler handler) {
		if (INSTANCE.handlers.containsKey(handler.getId())) {
			INSTANCE.handlers.put(handler.getId(), handler);
			return true;
		}
		return false;
	}

	public static void clear() {
		INSTANCE.events.clear();
		INSTANCE.eventToHandlersMap.clear();
	}
}
