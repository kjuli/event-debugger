package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.io.Serializable;

/**
 * Represents an entity that can be uniquely identified.
 * 
 * @param <T> The type of the identifier, which must be serializable.
 * 
 * @author Julijan Katic
 */
public interface Identifiable<T extends Serializable> {

	/**
	 * Retrieves the unique identifier of this entity.
	 * 
	 * @return The unique identifier.
	 */
	public T getId();

}
