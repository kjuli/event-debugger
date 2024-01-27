package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerFound;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ListenerEvent;

public interface EventHandlerMethodRetrieved<EVENT_TYPE, METHOD_TYPE> extends EventListener {

	public void methodRetrieved(final METHOD_TYPE method);

	public Class<METHOD_TYPE> getMethodType();

	public Class<EVENT_TYPE> getEventType();

	@Override
	default void onEvent(final ListenerEvent listenerEvent) {
		if (listenerEvent instanceof final EventHandlerFound<?> foundEv && foundEv.getType().equals(getMethodType())) {
			methodRetrieved((METHOD_TYPE) foundEv.getMethod());
		}
	}
}
