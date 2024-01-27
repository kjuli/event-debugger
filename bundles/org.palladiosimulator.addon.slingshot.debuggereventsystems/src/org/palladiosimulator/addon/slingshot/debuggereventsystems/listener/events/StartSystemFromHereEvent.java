package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public class StartSystemFromHereEvent extends ListenerEvent {

	public StartSystemFromHereEvent(final IDebugEvent debuggedEvent) {
		super(debuggedEvent, 0);
	}

	public StartSystemFromHereEvent(final String eventId) {
		super(eventId, 0);
	}

	public boolean hasActualEvent() {
		return getSource() instanceof IDebugEvent;
	}

	public String getEventId() {
		if (hasActualEvent()) {
			return getEvent().getId();
		} else {
			return (String) getSource();
		}
	}

	public IDebugEvent getEvent() {
		if (hasActualEvent()) {
			return (IDebugEvent) getSource();
		}
		return null;
	}
}
