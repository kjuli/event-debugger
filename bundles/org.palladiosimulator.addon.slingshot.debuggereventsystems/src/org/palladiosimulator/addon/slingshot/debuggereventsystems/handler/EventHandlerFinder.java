package org.palladiosimulator.addon.slingshot.debuggereventsystems.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Describes a finder for event-handlers.
 * <p>
 * An event-handler finder is used to find event-handlers. It has an
 * {@code EVENT_TYPE} that describes the static Java AST type of an event (for
 * example, a simple IType, Class<?>, StringType, ...). The same goes for the
 * METHOD_TYPE.
 * <p>
 * This should be implemented by the descriptor, since its used by the front-end
 * to find event-handlers (for example, upon event breakpoint creation).
 * 
 * @author Julijan Katic
 *
 * @param <EVENT_TYPE>  The Java AST Type of the event (i.e. Eclipse JDT's
 *                      IType)
 * @param <METHOD_TYPE> The Java AST Type of the handler.
 */
public interface EventHandlerFinder<EVENT_TYPE, METHOD_TYPE> {
	
	/**
	 * Retrieves all methods of the given EVENT_TYPE.
	 * <p>
	 * It uses a Consumer to push a found event handler into it. This is used since
	 * the operation might be long-running. Implementors are urged to implement this
	 * method in such a way that it is run on a separate thread instead.
	 * 
	 * @param eventType        The event to find its respective event handlers.
	 * @param handlerRetrieved A consumer as soon as an event-handler was found.
	 * @param onCompletion     A (nullable) runnable as soon as the operation as
	 *                         finished. Can be used to make cleanups
	 */
	public void retrieveMethods(final EVENT_TYPE eventType, final Consumer<METHOD_TYPE> handlerRetrieved,
			final Runnable onCompletion);

	/**
	 * Retrieves all the methods as a list instead of calling the retrieve method
	 * for each found handler.
	 * 
	 * @param eventType    The event to find its respective event handlers.
	 * @param onCompletion A consumer to retrieve the final list.
	 */
	default void asList(final EVENT_TYPE eventType, final Consumer<List<METHOD_TYPE>> onCompletion) {
		final List<METHOD_TYPE> result = new LinkedList<>();
		retrieveMethods(eventType, result::add, () -> onCompletion.accept(result));
	}

}
