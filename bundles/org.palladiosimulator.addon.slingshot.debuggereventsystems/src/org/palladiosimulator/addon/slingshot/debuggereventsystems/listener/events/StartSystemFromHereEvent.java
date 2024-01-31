package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public class StartSystemFromHereEvent extends ListenerEvent {

	public StartSystemFromHereEvent(final IDebugEvent debuggedEvent) {
		super(debuggedEvent, 0);
	}

	public StartSystemFromHereEvent(final DebugEventId eventId) {
		super(eventId, 0);
	}

	public boolean hasActualEvent() {
		return getSource() instanceof IDebugEvent;
	}

	public DebugEventId getEventId() {
		if (hasActualEvent()) {
			return getEvent().getId();
		} else {
			return (DebugEventId) getSource();
		}
	}

	public IDebugEvent getEvent() {
		if (hasActualEvent()) {
			return (IDebugEvent) getSource();
		}
		return null;
	}
}
