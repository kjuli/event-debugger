package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ListenerEvent;

/**
 * A generic listener for a specific event.
 * <p>
 * A listener needs to implement this class and must specify its event type in
 * the generic. It will be called as soon as such event is published.
 * 
 * @author Julijan Katic
 *
 * @param <T> The listener event type
 */
@FunctionalInterface
public interface EventListener<T extends ListenerEvent> {

	/**
	 * Consumes the listener event.
	 * 
	 * @param listenerEvent
	 */
	public void onEvent(final T listenerEvent);
	
}
