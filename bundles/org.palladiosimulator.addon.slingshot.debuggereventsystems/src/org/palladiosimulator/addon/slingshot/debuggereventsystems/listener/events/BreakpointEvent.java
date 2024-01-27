package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.EventBreakpoint;

public class BreakpointEvent extends ListenerEvent {
	
	private final BreakpointEvent.BreakpointEventType specificEventType;
	
	public BreakpointEvent(final EventBreakpoint eventBreakpoint, final BreakpointEvent.BreakpointEventType specificEventType) {
		super(eventBreakpoint, specificEventType.ordinal());
		this.specificEventType = specificEventType;
	}

	public BreakpointEvent.BreakpointEventType getSpecificEventType() {
		return specificEventType;
	}

	public EventBreakpoint getEventBreakpoint() {
		return (EventBreakpoint) getSource();
	}
	
	public enum BreakpointEventType {
		ADDED, REMOVED
	}

}