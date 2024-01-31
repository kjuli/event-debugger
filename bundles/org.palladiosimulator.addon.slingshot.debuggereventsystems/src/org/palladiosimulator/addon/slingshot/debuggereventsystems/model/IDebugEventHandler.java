package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.io.Serializable;

/**
 * Represents a handler for a debug event.
 * 
 * Event handlers are responsible for processing debug events. Each handler is
 * associated with a specific event and has a status indicating the result of
 * its processing.
 */
public interface IDebugEventHandler extends Serializable, Identifiable<HandlerId> {

	/**
	 * Retrieves the name of the event handler.
	 * 
	 * @return The handler's name.
	 */
	public String getName();
	
	/**
	 * Retrieves the identifier of the debug event this handler is associated with.
	 * 
	 * @return The {@link DebugEventId} of the associated event.
	 */
	public DebugEventId ofEvent();
	
	/**
	 * Retrieves the current status of the event handler.
	 * 
	 * @return The {@link HandlerStatus} of the handler.
	 */
	public HandlerStatus getStatus();

}
