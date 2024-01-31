package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventHandlerRetrievedListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerEvent;

/**
 * Implementation for starting or updating the event-handler status.
 * <p>
 * This listens to {@link EventHandlerEvent}, telling that something happened to
 * the event-handler. In particular, it updates its internal cache.
 * 
 * @author Julijan Katic
 */
public class EclipseEventHandlerRetrieved implements EventHandlerRetrievedListener {

	@Override
	public void onEvent(final EventHandlerEvent listenerEvent) {
		switch (listenerEvent.getEventHandlerDetail()) {
		case STARTED:
			EventDebugSystem.getEventHolder().addHandler(listenerEvent.getHandler());
			break;
		case UPDATED:
			EventDebugSystem.getEventHolder().updateEventHandler(listenerEvent.getHandler());
			break;
		}
	}

}
