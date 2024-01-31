package org.palladiosimulator.addon.slingshot.debuggereventsystems.handler;

/**
 * Implementations of this interface are used to check whether a given object is
 * an event-handler.
 * 
 * This should be particularly be implemented by the descriptor of the system.
 * The front-end uses this whenever it analyzes the static code and thinks
 * something might be an event-handler.
 * 
 * @author Julijan Katic
 */
public interface EventHandlerChecker {

	/**
	 * Checks whether the given object is an event-handler of the system.
	 * 
	 * @param object The object to identify.
	 * @return true if it is an event-handler, false otherwise.
	 */
	public boolean isEventHandler(final Object object);

}
