package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ListenerEvent;

@FunctionalInterface
public interface EventListener<T extends ListenerEvent> {

	public void onEvent(final T listenerEvent);
	
}
