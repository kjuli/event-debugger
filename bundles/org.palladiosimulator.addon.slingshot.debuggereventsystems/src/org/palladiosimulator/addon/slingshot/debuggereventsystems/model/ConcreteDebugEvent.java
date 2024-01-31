package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.cache.EventTreeNode;

/**
 * This class is used to represent a {@link IDebugEvent}. It is mostly used
 * either by the front-end to save the retrieved information from a message, or
 * to serialize it into a message.
 * 
 * @author Julijan Katic
 */
public class ConcreteDebugEvent implements IDebugEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8858142961660060153L;
	private final DebugEventId id;
	private final String name;
	private final String eventType;
	private final TimeInformation timeInformation;
	private final EventTreeNode parentEvent;

	public ConcreteDebugEvent(final DebugEventId id, final String name, final String eventType,
			final TimeInformation timeInformation, final EventTreeNode parent) {
		this.id = id;
		this.name = name;
		this.timeInformation = timeInformation;
		this.eventType = eventType;
		parentEvent = parent;
	}

	@Override
	public DebugEventId getId() {
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
	public Optional<EventTreeNode> getParentEvent() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(parentEvent);
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

	@Override
	public String getEventType() {
		// TODO Auto-generated method stub
		return eventType;
	}

}
