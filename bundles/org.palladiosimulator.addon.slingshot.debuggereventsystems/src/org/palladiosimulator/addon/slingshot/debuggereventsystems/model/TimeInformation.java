package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.io.Serializable;

/**
 * Represents timing information associated with a debug event.
 * 
 * This interface provides access to the timing data, such as the timestamp or
 * duration of the event. The reason why the method is not directly included in
 * {@link IDebugEvent} is because that not every event system supports a time
 * information. Instead, a default time information can be used.
 */
public interface TimeInformation extends Serializable {
	
	/**
	 * Retrieves the time of an event.
	 * 
	 * @return The time of the event.
	 */
	public double getTime();
	
}
