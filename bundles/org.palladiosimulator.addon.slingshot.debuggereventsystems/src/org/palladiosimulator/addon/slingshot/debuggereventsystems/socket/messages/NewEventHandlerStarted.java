package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.ConcreteDebugEventHandler;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerStatus;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

/**
 * Indicates that a new event handler has started to be executed at the
 * back-end.
 * 
 * @author Julijan Katic
 */
public record NewEventHandlerStarted(HandlerId handlerId, DebugEventId eventId, String eventHandlerName)
		implements Message {

	public IDebugEventHandler asDebugEventHandler() {
		return new ConcreteDebugEventHandler(handlerId, eventId, eventHandlerName, HandlerStatus.STARTED);
	}

	public static NewEventHandlerStarted from(final IDebugEventHandler handler) {
		return new NewEventHandlerStarted(handler.getId(), handler.ofEvent(), handler.getName());
	}

}
