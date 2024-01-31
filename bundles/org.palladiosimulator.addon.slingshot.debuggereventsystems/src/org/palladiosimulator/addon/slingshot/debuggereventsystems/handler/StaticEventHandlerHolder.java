package org.palladiosimulator.addon.slingshot.debuggereventsystems.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.Constants;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.common.ExtensionHelper;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerFound;

/**
 * Holds and manages event handler finders and checkers within the Event Debug
 * System.
 * <p>
 * This class is responsible for storing event handler finders that are used to
 * locate methods that can handle specific types of events within the
 * application. It also maintains a list of event handler checkers to determine
 * if a given object is an event handler. The class provides functionality to
 * dynamically find event handlers based on event type and method type,
 * supporting the registration and retrieval of handler finders and checkers.
 * </p>
 * 
 * @author Julijan Katic
 */
public class StaticEventHandlerHolder {

	private final Map<EventHandlerType, List<EventHandlerFinder<?, ?>>> handlers = new HashMap<>();
	private final List<EventHandlerChecker> handlerChecker = new LinkedList<>();

	/**
	 * Finds event handlers for a given event and invokes callbacks upon finding
	 * methods or completing the search.
	 * <p>
	 * This overloaded version allows specifying an event, its handler type, and the
	 * Java method type without callbacks.
	 * </p>
	 *
	 * @param event              The event instance to find handlers for.
	 * @param handlerType        The class of the handler type.
	 * @param javaMethodType     The class of the Java method type expected to
	 *                           handle the event.
	 * @param <JAVA_METHOD_TYPE> The generic type of the method expected to handle
	 *                           the event.
	 * @param <JAVA_EVENT_TYPE>  The generic type of the event.
	 */
	public <JAVA_METHOD_TYPE, JAVA_EVENT_TYPE> void findEventHandlers(final JAVA_EVENT_TYPE event,
			final Class<? super JAVA_EVENT_TYPE> handlerType, final Class<JAVA_METHOD_TYPE> javaMethodType) {
		findEventHandlers(event, handlerType, javaMethodType, null, null);
	}

	/**
	 * Finds event handlers for a given event, invoking specified callbacks upon
	 * finding methods and completion.
	 *
	 * @param event              The event instance to find handlers for.
	 * @param eventType          The class of the event type.
	 * @param javaMethodType     The class of the Java method type expected to
	 *                           handle the event.
	 * @param methodFound        Callback to invoke for each method found.
	 * @param onCompletion       Callback to invoke upon completion of the search.
	 * @param <JAVA_METHOD_TYPE> The generic type of the method expected to handle
	 *                           the event.
	 * @param <JAVA_EVENT_TYPE>  The generic type of the event.
	 */
	public <JAVA_METHOD_TYPE, JAVA_EVENT_TYPE> void findEventHandlers(final JAVA_EVENT_TYPE event,
			final Class<? super JAVA_EVENT_TYPE> eventType, final Class<JAVA_METHOD_TYPE> javaMethodType,
			final Consumer<JAVA_METHOD_TYPE> methodFound, final Runnable onCompletion) {
		final Consumer<JAVA_METHOD_TYPE> doOnMethodFound = method -> {
			if (methodFound != null) {
				methodFound.accept(method);
			}
			EventDebugSystem.callEvent(new EventHandlerFound<>(method));
		};
		final Runnable doOnCompletion = () -> {
			if (onCompletion != null) {
				onCompletion.run();
			}
		};

		getEventHandlerFinders(eventType, javaMethodType).stream().findAny().ifPresent(
				finder -> finder.retrieveMethods(event, doOnMethodFound, doOnCompletion));
	}

	/**
	 * Dynamically loads and registers event handler finders from extensions.
	 * <p>
	 * This method is used to initialize event handler finders based on extension
	 * points, facilitating dynamic discovery and registration of event handlers
	 * within the system.
	 * </p>
	 */
	public void findEventHandlerFinders() {
		for (final IExtension extension : ExtensionHelper.loadExtensions(Constants.HANDLER_EXTENSION_POINT_ID)) {
			final IConfigurationElement element = ExtensionHelper.obtainConfigurationElement(extension,
					Constants.HANDLER_EXTENSION_POINT_ELEMENT_NAME);

			final String javaMethodTypeName = element.getAttribute(Constants.HANDLER_EXTENSION_POINT_JAVA_METHOD_TYPE);
			final String javaEventTypeName = element.getAttribute(Constants.HANDLER_EXTENSION_POINT_JAVA_EVENT_TYPE);

			if (javaMethodTypeName != null && javaEventTypeName != null) {
				try {
					final Bundle bundle = Platform.getBundle(element.getContributor().getName());
					final Class<?> javaMethodType = bundle.loadClass(javaMethodTypeName);
					final Class<?> javaEventType = bundle.loadClass(javaEventTypeName);
					final EventHandlerFinder<?, ?> finder = (EventHandlerFinder<?, ?>) element
							.createExecutableExtension(Constants.HANDLER_EXTENSION_POINT_CLASS);

					handlers.computeIfAbsent(new EventHandlerType(javaEventType, javaMethodType),
							eht -> new LinkedList<>()).add(finder);
				} catch (ClassNotFoundException | CoreException e) {
					throw new RuntimeException("Could not load class in the event debugger system.", e);
				}
			}
		}
	}


	/**
	 * Retrieves event handler finders based on the event and method types.
	 *
	 * @param javaEventType  The class of the Java event type.
	 * @param javaMethodType The class of the Java method type expected to handle
	 *                       the event.
	 * @return A list of event handler finders compatible with the specified event
	 *         and method types.
	 * @param <JAVA_METHOD_TYPE> The generic type of the method expected to handle
	 *                           the event.
	 * @param <JAVA_EVENT_TYPE>  The generic type of the event.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <JAVA_METHOD_TYPE, JAVA_EVENT_TYPE> List<EventHandlerFinder<JAVA_EVENT_TYPE, JAVA_METHOD_TYPE>> getEventHandlerFinders(
			final Class<JAVA_EVENT_TYPE> javaEventType, final Class<JAVA_METHOD_TYPE> javaMethodType) {
		final List handlers = this.handlers.getOrDefault(new EventHandlerType(javaEventType, javaMethodType),
				Collections.emptyList());
		return handlers;
	}

	/**
	 * Registers an event handler finder for a specific event handler type.
	 *
	 * @param type   The event handler type.
	 * @param finder The event handler finder to register.
	 */
	public void addEventHandlerType(final EventHandlerType type, final EventHandlerFinder<?, ?> finder) {
		handlers.computeIfAbsent(type, t -> new LinkedList<>())
		.add(finder);
	}

	/**
	 * Adds an event handler checker to the holder.
	 *
	 * @param checker The event handler checker to add.
	 */
	public void addEventHandlerChecker(final EventHandlerChecker checker) {
		handlerChecker.add(checker);
	}

	/**
	 * Checks if a given object is recognized as an event handler according to some
	 * of the given event handler checkers. It returns true as soon as one of the
	 * checkers returns true.
	 *
	 * @param object The object to check.
	 * @return True if the object is an event handler, false otherwise.
	 * 
	 * @see EventHandlerChecker
	 */
	public boolean isEventHandler(final Object object) {
		return handlerChecker.stream().anyMatch(checker -> checker.isEventHandler(object));
	}
}
