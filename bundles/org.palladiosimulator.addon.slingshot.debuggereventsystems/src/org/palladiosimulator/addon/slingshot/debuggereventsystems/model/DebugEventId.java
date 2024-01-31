package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.io.Serializable;

/**
 * Identifies the {@link IDebugEvent}
 * 
 * @author Julijan Katic
 */
public record DebugEventId(String id) implements Identifiable<String>, Serializable {

	@Override
	public String getId() {
		return id;
	}

}
