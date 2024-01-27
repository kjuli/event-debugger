package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.AbstractIDebugEventHandler;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerStatus;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

public record NewEventHandlerFinished(String eventId, String eventHandlerName, HandlerStatus handlerStatus)
		implements Message {

	public IDebugEventHandler asDebugHandler() {
		return new AbstractIDebugEventHandler(eventId, eventHandlerName, handlerStatus);
	}

	public static NewEventHandlerFinished from(final AbstractIDebugEventHandler abstractIDebugEventHandler) {
		return new NewEventHandlerFinished(abstractIDebugEventHandler.ofEvent(), abstractIDebugEventHandler.getName(),
				abstractIDebugEventHandler.getStatus());
	}

}
