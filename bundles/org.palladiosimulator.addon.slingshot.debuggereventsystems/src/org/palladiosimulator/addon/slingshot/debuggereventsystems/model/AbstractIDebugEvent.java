package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class AbstractIDebugEvent implements IDebugEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8858142961660060153L;
	private final String id;
	private final String name;
	private final TimeInformation timeInformation;

	public AbstractIDebugEvent(final String id, final String name, final TimeInformation timeInformation) {
		this.id = id;
		this.name = name;
		this.timeInformation = timeInformation;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public Object getEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Object> getParentEvent() {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Map<String, Object> getMetaInformation() {
		// TODO Auto-generated method stub
		return Collections.emptyMap();
	}

	@Override
	public TimeInformation getTimeInformation() {
		// TODO Auto-generated method stub
		return timeInformation;
	}

}
