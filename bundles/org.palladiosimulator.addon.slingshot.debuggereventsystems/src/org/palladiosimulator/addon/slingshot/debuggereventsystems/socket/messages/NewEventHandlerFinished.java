package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.ConcreteDebugEventHandler;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerStatus;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

/**
 * Message indicating that a given event-handler has finished with a certain
 * status.
 * 
 * @author Julijan Katic
 *
 */
public record NewEventHandlerFinished(HandlerId handlerId, DebugEventId eventId, String eventHandlerName,
		HandlerStatus handlerStatus)
		implements Message {

	public IDebugEventHandler asDebugHandler() {
		return new ConcreteDebugEventHandler(handlerId, eventId, eventHandlerName, handlerStatus);
	}

	public static NewEventHandlerFinished from(final IDebugEventHandler handler) {
		return new NewEventHandlerFinished(handler.getId(), handler.ofEvent(), handler.getName(), handler.getStatus());
	}

}
