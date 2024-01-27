package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.BreakpointEvent;

public interface BreakpointEventListener extends EventListener<BreakpointEvent> {
	
	@Deprecated
	public void breakpointEvent(final BreakpointEvent ev);
	
	@Override
	default void onEvent(final BreakpointEvent listenerEvent) {
		breakpointEvent(listenerEvent);
	}
	
}
