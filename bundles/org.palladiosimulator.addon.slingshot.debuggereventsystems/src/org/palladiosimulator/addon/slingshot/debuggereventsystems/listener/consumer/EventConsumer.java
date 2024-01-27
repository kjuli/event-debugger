package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.consumer;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public interface EventConsumer {
	
	public void consumeEvent(final IDebugEvent event);
	
}
