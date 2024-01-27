package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public class ListenerDebugEvent extends ListenerEvent {
	
	public ListenerDebugEvent(final IDebugEvent debuggedEvent, final int eventType) {
		super(debuggedEvent, eventType);
	}
	
	public IDebugEvent getDebuggedEvent() {
		return (IDebugEvent) getSource();
	}
	
}
