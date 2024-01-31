package org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.cache.EventTreeNode;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.ConcreteDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.ConcreteTimeInformation;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public record NewEventProvidedMessage(DebugEventId id, String eventName, String eventType, double time,
		EventTreeNode parent) implements Message {

	public static NewEventProvidedMessage fromEvent(final IDebugEvent from) {
		return new NewEventProvidedMessage(from.getId(), from.getName(), from.getEventType(),
				from.getTimeInformation().getTime(), from.getParentEvent().orElse(null));
	}

	public IDebugEvent toDebugEvent() {
		return new ConcreteDebugEvent(id, eventName, eventType, new ConcreteTimeInformation(time), parent);
	}

}
