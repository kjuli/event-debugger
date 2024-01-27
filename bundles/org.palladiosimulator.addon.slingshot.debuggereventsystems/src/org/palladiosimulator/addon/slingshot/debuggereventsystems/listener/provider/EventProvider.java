package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.provider;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public interface EventProvider {
	
	public void provideEvent(final IDebugEvent event);
	
}
