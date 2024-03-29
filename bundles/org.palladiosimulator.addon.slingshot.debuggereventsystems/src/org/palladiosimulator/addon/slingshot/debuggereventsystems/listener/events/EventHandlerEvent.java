package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

public class EventHandlerEvent extends ListenerEvent {

	private final DebugEventId eventId;
	private final EventHandlerDetail eventDetail;

	public EventHandlerEvent(final IDebugEventHandler source, final DebugEventId eventId,
			final EventHandlerDetail detail) {
		super(source, detail.ordinal());
		this.eventId = eventId;
		eventDetail = detail;
	}

	public EventHandlerEvent(final IDebugEventHandler source, final EventHandlerDetail detail) {
		super(source, detail.ordinal());
		eventId = source.ofEvent();
		eventDetail = detail;
	}

	public IDebugEventHandler getHandler() {
		return (IDebugEventHandler) getSource();
	}

	public DebugEventId getEventId() {
		return eventId;
	}

	public EventHandlerDetail getEventHandlerDetail() {
		return eventDetail;
	}

	public enum EventHandlerDetail {
		STARTED, UPDATED
	}

}