package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.cache.EventTreeNode;

/**
 * Represents a debug event in the runtime debugging system.
 * 
 * Each debug event carries information about a specific occurrence within the
 * target application being debugged, including metadata and timing information.
 * It can optionally have a parent event, establishing a hierarchical
 * relationship between events.
 */
public interface IDebugEvent extends Serializable, Identifiable<DebugEventId> {

	/**
	 * Retrieves the name of the debug event.
	 * 
	 * @return The name of the event.
	 */
	public String getName();
	
	/**
	 * Retrieves the event object if possible.
	 * 
	 * @return The event object.
	 */
	public Object getEvent();
	
	/**
	 * Retrieves the type of the debug event. This is specified by the application
	 * directly, and can be any value to ease the debugging for the developer
	 * 
	 * @return The event type.
	 */
	public String getEventType();

	/**
	 * Retrieves the parent event of this debug event, if any.
	 * 
	 * @return An {@link Optional} containing the parent event or empty if there is
	 *         no parent.
	 */
	public Optional<EventTreeNode> getParentEvent();
	
	/**
	 * Retrieves additional meta-information about the debug event.
	 * 
	 * @return A map containing key-value pairs of meta-information.
	 */
	public Map<String, Object> getMetaInformation();
	
	/**
	 * Retrieves timing information associated with the debug event.
	 * 
	 * @return The {@link TimeInformation} instance containing timing data.
	 */
	public TimeInformation getTimeInformation();
	
}
