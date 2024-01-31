package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.Constants;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.common.ExtensionHelper;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.common.RethrowAsRuntime;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ListenerEvent;

/**
 * Manages and invokes event listeners based on the type of events occurring
 * within the Event Debug System.
 * <p>
 * This class maintains a mapping between event types and their corresponding
 * listeners, facilitating the dynamic dispatch of events to the appropriate
 * listeners. It supports the registration of listeners for specific types of
 * events and invokes these listeners when an event of the corresponding type is
 * triggered.
 * </p>
 */
public final class ListenerHolder {

	private final Map<Class<?>, List<EventListener>> eventListeners = new HashMap<>();
	
	/**
	 * Triggers an event, notifying all registered listeners of the event type.
	 * <p>
	 * Iterates through all listeners that have registered interest in the type of
	 * the event being triggered and calls their {@code onEvent} method, passing the
	 * event as an argument.
	 * </p>
	 *
	 * @param listenerEvent The event to trigger.
	 */
	public void callEvent(final ListenerEvent listenerEvent) {
		eventListeners.entrySet().stream()
				.filter(entry -> entry.getKey().isAssignableFrom(listenerEvent.getClass()))
				.flatMap(entry -> entry.getValue().stream())
				.forEach(listener -> listener.onEvent(listenerEvent));
	}

	/**
	 * Dynamically discovers and registers event listeners based on extension
	 * points.
	 * <p>
	 * Loads event listener extensions from the Eclipse plugin architecture,
	 * creating instances of the listeners and registering them for the specified
	 * event types. This allows for the decoupled addition of event listeners
	 * through the plugin's extension points.
	 * </p>
	 */
	public void findListeners() {
		for (final IExtension extension : ExtensionHelper.loadExtensions(Constants.LISTENER_EXTENSION_POINT_ID)) {
			final IConfigurationElement element = ExtensionHelper.obtainConfigurationElement(extension,
					Constants.LISTENER_EXTENSION_POINT_ELEMENT_NAME);

			try {
				final Class<? extends ListenerEvent> eventType = ExtensionHelper.loadClassFromElement(element,
						Constants.LISTENER_EXTENSION_POINT_EVENT_TYPE, ListenerEvent.class,
						RethrowAsRuntime.from(() -> "Class could not be found."),
						RethrowAsRuntime.from(() -> "Class couldn't be cast."));

				final EventListener eventListener = (EventListener) element
						.createExecutableExtension(Constants.LISTENER_EXTENSION_POINT_CLASS);

				addListener(eventType, eventListener);
			} catch (final NumberFormatException e) {
				throw new RuntimeException("The eventType attribute must be a number >= 0", e);
			} catch (final CoreException e) {
				throw new RuntimeException("Could not create event listener instance.", e);
			}

		}
	}

	/**
	 * Registers an event listener for a specific type of event.
	 * <p>
	 * Adds a listener to the internal mapping based on the event type it listens
	 * to. This allows for the listener to be notified when an event of the
	 * corresponding type is triggered within the system.
	 * </p>
	 *
	 * @param eventType     The class of the event type the listener is interested
	 *                      in.
	 * @param eventListener The event listener to register.
	 * @param <T>           The type of the event the listener is registered for.
	 */
	public <T extends ListenerEvent> void addListener(final Class<T> eventType, final EventListener<T> eventListener) {
		eventListeners.computeIfAbsent(eventType, et -> new LinkedList<EventListener>())
					  .add(eventListener);
	}
}
