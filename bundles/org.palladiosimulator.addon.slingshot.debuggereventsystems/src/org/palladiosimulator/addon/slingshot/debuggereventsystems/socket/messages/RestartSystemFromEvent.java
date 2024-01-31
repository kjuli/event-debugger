package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public record RestartSystemFromEvent(DebugEventId eventId) implements Message {

	public static RestartSystemFromEvent from(final IDebugEvent event) {
		return new RestartSystemFromEvent(event.getId());
	}

}
