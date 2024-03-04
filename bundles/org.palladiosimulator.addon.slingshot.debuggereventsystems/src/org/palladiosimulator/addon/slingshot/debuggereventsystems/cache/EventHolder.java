package org.palladiosimulator.addon.slingshot.debuggereventsystems.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;


/**
 * Manages and stores debug events and their associated handlers.
 * <p>
 * This class acts as a central repository for debug events and event handlers
 * within the debugging system. It facilitates efficient retrieval and
 * management of debug events and handlers, allowing for quick access based on
 * their unique identifiers. This is particularly useful in scenarios where
 * front-end and back-end components need to exchange information about events
 * and handlers with minimal data transfer.
 * </p>
 * It also maps each debugged event to the handlers they are belonging to.
 * 
 * @author Julijan Katic
 */
public class EventHolder {

	private final Map<DebugEventId, IDebugEvent> events = new HashMap<>();
	private final Map<HandlerId, IDebugEventHandler> handlers = new HashMap<>();
	private final Map<DebugEventId, List<HandlerId>> eventToHandlersMap = new HashMap<>();
	/**
	 * Adds a debug event to the repository.
	 * <p>
	 * Stores the debug event using its unique identifier for later retrieval.
	 * </p>
	 *
	 * @param event The debug event to add.
	 */
	public void addEvent(final IDebugEvent event) {
		events.put(event.getId(), event);
	}

	/**
	 * Adds an event handler to the repository.
	 * <p>
	 * Associates the handler with its corresponding debug event and stores it using
	 * its unique identifier.
	 * </p>
	 *
	 * @param handler The event handler to add.
	 */
	public void addHandler(final IDebugEventHandler handler) {
		eventToHandlersMap.computeIfAbsent(handler.ofEvent(), id -> new LinkedList<>())
						  .add(handler.getId());
		handlers.put(handler.getId(), handler);
	}

	/**
	 * Retrieves a cached debug event based on its identifier.
	 * <p>
	 * Returns an {@link Optional} containing the debug event if it exists;
	 * otherwise, an empty {@link Optional}.
	 * </p>
	 *
	 * @param event The unique identifier of the debug event.
	 * @return An {@link Optional} containing the debug event or empty if not found.
	 */
	public Optional<IDebugEvent> getCachedEvent(final DebugEventId event) {
		return Optional.of(events.get(event));
	}

	/**
	 * Retrieves a cached event handler based on its identifier.
	 * <p>
	 * Returns an {@link Optional} containing the event handler if it exists;
	 * otherwise, an empty {@link Optional}.
	 * </p>
	 *
	 * @param handler The unique identifier of the event handler.
	 * @return An {@link Optional} containing the event handler or empty if not
	 *         found.
	 */
	public Optional<IDebugEventHandler> getCachedHandler(final HandlerId handler) {
		return Optional.of(handlers.get(handler));
	}
	
	/**
	 * Retrieves all event handlers associated with a specific debug event.
	 * <p>
	 * Returns a list of event handlers for the given debug event identifier. If no
	 * handlers are found, returns an empty list.
	 * </p>
	 *
	 * @param eventId The unique identifier of the debug event.
	 * @return A list of {@link IDebugEventHandler} associated with the debug event.
	 */
	public List<IDebugEventHandler> getHandlersByEvent(final DebugEventId eventId) {
		return eventToHandlersMap
				.getOrDefault(eventId, Collections.emptyList())
				.stream()
				.map(handlerId -> handlers.get(handlerId))
				.filter(handler -> handler != null)
				.collect(Collectors.toList());
	}

	/**
	 * Updates an existing event handler in the repository.
	 * <p>
	 * If the handler exists, it is updated with the provided handler object;
	 * otherwise, no action is taken.
	 * </p>
	 * This method is especially used if the status of the handler has changed.
	 *
	 * @param handler The event handler to update.
	 */
	public void updateEventHandler(final IDebugEventHandler handler) {
		if (handlers.containsKey(handler.getId())) {
			handlers.put(handler.getId(), handler);
		}
	}

	/**
	 * Removes a debug event and its associated handlers from the repository.
	 *
	 * @param eventId The unique identifier of the debug event to remove.
	 */
	public void removeEvent(final DebugEventId eventId) {
		events.remove(eventId);
		eventToHandlersMap.remove(eventId);
	}
	
	public Iterable<Map.Entry<DebugEventId, IDebugEvent>> eventIterator() {
		return this.events.entrySet();
	}
	
	public Iterable<Map.Entry<HandlerId, IDebugEventHandler>> handlerIterator() {
		return this.handlers.entrySet();
	}
	
}
