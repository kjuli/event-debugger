package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.AbstractIDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.AbstractTimeInformation;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public record NewEventProvidedMessage(String id, String eventName, double time) implements Message {

	public static NewEventProvidedMessage fromEvent(final IDebugEvent from) {
		return new NewEventProvidedMessage(from.getId(), from.getName(), from.getTimeInformation().getTime());
	}

	public IDebugEvent toDebugEvent() {
		return new AbstractIDebugEvent(id, eventName, new AbstractTimeInformation(time));
	}

}
