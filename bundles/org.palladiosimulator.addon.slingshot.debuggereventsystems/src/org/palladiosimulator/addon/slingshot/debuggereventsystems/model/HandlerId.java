package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.io.Serializable;

/**
 * Identifies a {@link IDebugEventHandler}.
 * 
 * @author Julijan Katic
 */
public record HandlerId(String id) implements Identifiable<String>, Serializable {

	@Override
	public String getId() {
		return id;
	}

}
