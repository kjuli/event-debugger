package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.Constants;

public class EventHandlerFound<METHOD_TYPE> extends ListenerEvent {

	private final Class<METHOD_TYPE> methodType;

	public EventHandlerFound(final METHOD_TYPE method) {
		super(method, Constants.METHOD_RETRIEVED_EVENT);
		methodType = (Class<METHOD_TYPE>) method.getClass();
	}

	public METHOD_TYPE getMethod() {
		return (METHOD_TYPE) getSource();
	}

	public Class<METHOD_TYPE> getType() {
		return methodType;
	}

}