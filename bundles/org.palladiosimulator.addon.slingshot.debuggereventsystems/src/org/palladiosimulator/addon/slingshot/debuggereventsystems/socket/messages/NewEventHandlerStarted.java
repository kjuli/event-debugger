package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.AbstractIDebugEventHandler;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerStatus;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

public record NewEventHandlerStarted(String eventId, String eventHandlerName) implements Message {

	public IDebugEventHandler asDebugEventHandler() {
		return new AbstractIDebugEventHandler(eventId, eventHandlerName, HandlerStatus.STARTED);
	}

	public static NewEventHandlerStarted from(final AbstractIDebugEventHandler abstractIDebugEventHandler) {
		return new NewEventHandlerStarted(abstractIDebugEventHandler.ofEvent(), abstractIDebugEventHandler.getName());
	}

}
