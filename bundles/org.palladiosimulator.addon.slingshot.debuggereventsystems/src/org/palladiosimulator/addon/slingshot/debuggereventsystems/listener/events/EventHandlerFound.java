package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

public class EventHandlerFound<METHOD_TYPE> extends ListenerEvent {

	private final Class<METHOD_TYPE> methodType;

	public EventHandlerFound(final METHOD_TYPE method) {
		super(method, 0);
		methodType = (Class<METHOD_TYPE>) method.getClass();
	}

	public METHOD_TYPE getMethod() {
		return (METHOD_TYPE) getSource();
	}

	public Class<METHOD_TYPE> getType() {
		return methodType;
	}

}