package org.palladiosimulator.addon.slingshot.debuggereventsystems;

import java.util.function.Consumer;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.DebugEventPublished;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerEvent.EventHandlerDetail;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.StartSystemFromHereEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.Message;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.NewEventHandlerFinished;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.NewEventHandlerStarted;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.NewEventProvidedMessage;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.RestartSystemFromEvent;

/**
 * This class is used to map socket messages to their respective
 * event-listeners.
 * 
 * @author Julijan Katic
 */
public class MessageToDebugEventMapper implements Consumer<Message> {

	@Override
	public void accept(final Message message) {
		if (message instanceof final NewEventProvidedMessage newEvent) {
			EventDebugSystem.callEvent(new DebugEventPublished(newEvent.toDebugEvent()));
		} else if (message instanceof final NewEventHandlerStarted started) {
			EventDebugSystem.callEvent(new EventHandlerEvent(started.asDebugEventHandler(), started.eventId(),
					EventHandlerDetail.STARTED));
		} else if (message instanceof final RestartSystemFromEvent rsfe) {
			EventDebugSystem.callEvent(new StartSystemFromHereEvent(rsfe.eventId()));
		} else if (message instanceof final NewEventHandlerFinished hf) {
			EventDebugSystem.callEvent(new EventHandlerEvent(hf.asDebugHandler(), EventHandlerDetail.UPDATED));
		}
	}


}
