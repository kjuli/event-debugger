package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers;

import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.view.EventHolder;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventHandlerRetrievedListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerEvent;

public class EclipseEventHandlerRetrieved implements EventHandlerRetrievedListener {

	@Override
	public void onEvent(final EventHandlerEvent listenerEvent) {
//		System.out.println("Retrieved event handler: " + listenerEvent.getEventId() + " with handler "
//				+ listenerEvent.getHandler().getName());
//		EventHolder.addHandler(listenerEvent.getEventId(), listenerEvent.getHandler());
		switch (listenerEvent.getEventHandlerDetail()) {
		case STARTED:
			EventHolder.addHandler(listenerEvent.getEventId(), listenerEvent.getHandler());
			break;
		case UPDATED:
			EventHolder.updateHandler(listenerEvent.getHandler());
			break;
		}
	}

//	@Override
//	public void retrieveEventHandler(final String eventId, final IDebugEventHandler handler) {
//		EventHolder.addHandler(eventId, handler);
//	}

}
