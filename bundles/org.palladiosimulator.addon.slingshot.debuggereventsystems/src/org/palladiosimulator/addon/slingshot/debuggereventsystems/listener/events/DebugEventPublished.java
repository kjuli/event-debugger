package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public class DebugEventPublished extends ListenerDebugEvent {

	public DebugEventPublished(final IDebugEvent debuggedEvent) {
		super(debuggedEvent, 0);
	}

}
