package org.palladiosimulator.addon.slingshot.debuggereventsystems.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public interface EventHandlerFinder<EVENT_TYPE, METHOD_TYPE> {
	
	public void retrieveMethods(final EVENT_TYPE eventType, final Consumer<METHOD_TYPE> handlerRetrieved,
			final Runnable onCompletion);

	default void asList(final EVENT_TYPE eventType, final Consumer<List<METHOD_TYPE>> onCompletion) {
		final List<METHOD_TYPE> result = new LinkedList<>();
		retrieveMethods(eventType, result::add, () -> onCompletion.accept(result));
	}

}
