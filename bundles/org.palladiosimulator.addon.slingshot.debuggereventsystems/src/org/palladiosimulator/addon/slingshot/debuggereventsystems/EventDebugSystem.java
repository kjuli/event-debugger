package org.palladiosimulator.addon.slingshot.debuggereventsystems;

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
import org.palladiosimulator.addon.slingshot.debuggereventsystems.common.ExtensionHelper;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.common.RethrowAsRuntime;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.handler.EventHandlerChecker;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.handler.EventHandlerFinder;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.BreakpointEventListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventHandlerMethodRetrieved;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventHandlerRetrievedListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.ShowEventInformationListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.StartEventFromHereListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.SystemClearUpListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.consumer.EventConsumer;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.BreakpointEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerEvent.EventHandlerDetail;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerFound;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ListenerEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ShowEventRequested;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.StartSystemFromHereEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.SystemClearedUp;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.provider.EventProvider;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.system.EventDebugActivationVote;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.AbstractIDebugEventHandler;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.EventBreakpoint;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.settings.Settings;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.EventDebugClient;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.EventDebugServer;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.Message;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.NewEventHandlerFinished;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.NewEventHandlerStarted;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.NewEventProvidedMessage;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.RestartSystemFromEvent;

public final class EventDebugSystem {
	
	private final List<EventBreakpoint> breakpoints = new LinkedList<>();
	private final EventProvider providers;
	
	private final Map<EventHandlerType, List<EventHandlerFinder<?, ?>>> handlers = new HashMap<>();
	private final List<EventConsumer> consumers = new LinkedList<>();
	private final Map<Class<?>, List<EventListener>> eventListeners = new HashMap<>();
	private final List<EventDebugActivationVote> activationVoters = new LinkedList<>();
	private final List<EventHandlerChecker> handlerChecker = new LinkedList<>();
	private final Settings settings = new Settings();
	
	
	EventDebugSystem() {
		providers = event -> {
			EventDebugClient.sendMessage(NewEventProvidedMessage.fromEvent(event));
		};

		addClientEventListener(message -> {
			if (message instanceof final NewEventProvidedMessage newEvent) {
				consumers.forEach(c -> c.consumeEvent(newEvent.toDebugEvent()));
			} else if (message instanceof final NewEventHandlerStarted started) {
				callEvent(new EventHandlerEvent(started.asDebugEventHandler(), started.eventId(),
						EventHandlerDetail.STARTED));
			} else if (message instanceof final RestartSystemFromEvent rsfe) {
				callEvent(new StartSystemFromHereEvent(rsfe.eventId()));
			} else if (message instanceof final NewEventHandlerFinished hf) {
				callEvent(new EventHandlerEvent(hf.asDebugHandler(), EventHandlerDetail.UPDATED));
			}
		});

	}
	
	void init() {
		ExtensionHelper.addExecutableExtensions(Constants.CONSUMER_EXTENSION_POINT_ID,
				Constants.CONSUMER_EXTENSION_POINT_ELEMENT_NAME, Constants.CONSUMER_EXTENSION_POINT_ATTRIBUTE_NAME,
				consumers);
		findEventHandlerFinders();
		findListeners();
	}

	public static void listenToDebugEvents() {
		EventDebugServer.start();
		EventDebugClient.getInstance();
	}

	public static void addEventHandlerChecker(final EventHandlerChecker checker) {
		getDefaultInstance().handlerChecker.add(checker);
	}

	public static void addClientEventListener(final Consumer<Message> onMessage) {
		EventDebugClient.addListener(onMessage);
	}

	private void findEventHandlerFinders() {
		for (final IExtension extension : ExtensionHelper.loadExtensions(Constants.HANDLER_EXTENSION_POINT_ID)) {
			final IConfigurationElement element = ExtensionHelper.obtainConfigurationElement(extension, Constants.HANDLER_EXTENSION_POINT_ELEMENT_NAME);
			
			final String javaMethodTypeName = element.getAttribute(Constants.HANDLER_EXTENSION_POINT_JAVA_METHOD_TYPE);
			final String javaEventTypeName = element.getAttribute(Constants.HANDLER_EXTENSION_POINT_JAVA_EVENT_TYPE);
			
			if (javaMethodTypeName != null && javaEventTypeName != null) {
				try {
					final Bundle bundle = Platform.getBundle(element.getContributor().getName());
					final Class<?> javaMethodType = bundle.loadClass(javaMethodTypeName);
					final Class<?> javaEventType = bundle.loadClass(javaEventTypeName);
					final EventHandlerFinder<?, ?> finder = (EventHandlerFinder<?, ?>) element.createExecutableExtension(Constants.HANDLER_EXTENSION_POINT_CLASS);
					
					handlers.computeIfAbsent(new EventHandlerType(javaEventType, javaMethodType), eht -> new LinkedList<>())
							.add(finder);
				} catch (ClassNotFoundException | CoreException e) {
					throw new RuntimeException("Could not load class in the event debugger system.", e);
				}
			}
		}
	}

	private void findListeners() {
		for (final IExtension extension : ExtensionHelper.loadExtensions(Constants.LISTENER_EXTENSION_POINT_ID)) {
			final IConfigurationElement element = ExtensionHelper.obtainConfigurationElement(extension,
					Constants.LISTENER_EXTENSION_POINT_ELEMENT_NAME);
			// final String eventTypeStr =
			// element.getAttribute(Constants.LISTENER_EXTENSION_POINT_EVENT_TYPE);

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

	public static void addBreakpoint(final EventBreakpoint eventBreakpoint) {
		getDefaultInstance().breakpoints.add(eventBreakpoint);
		callEvent(new BreakpointEvent(eventBreakpoint, BreakpointEvent.BreakpointEventType.ADDED));
	}
	
	public static void addConsumer(final EventConsumer consumer) {
		getDefaultInstance().consumers.add(consumer);
	}

	public static void removeBreakpoint(final EventBreakpoint eventBreakpoint) {
		getDefaultInstance().breakpoints.remove(eventBreakpoint);
		callEvent(new BreakpointEvent(eventBreakpoint, BreakpointEvent.BreakpointEventType.REMOVED));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <JAVA_METHOD_TYPE, JAVA_EVENT_TYPE> List<EventHandlerFinder<JAVA_EVENT_TYPE, JAVA_METHOD_TYPE>> getEventHandlerFinders(
			final Class<JAVA_EVENT_TYPE> javaEventType, final Class<JAVA_METHOD_TYPE> javaMethodType) {
		final List handlers = getDefaultInstance().handlers.getOrDefault(new EventHandlerType(javaEventType, javaMethodType), Collections.emptyList());
		return handlers;
	}
	
	public static boolean isDebugEnabled() {
		return (getDefaultInstance().activationVoters.isEmpty()
				|| getDefaultInstance().activationVoters.stream().anyMatch(voter -> voter.shouldActivateDebugger()));
	}

	//
	// ADD LISTENER EVENTS
	//
	
	public static void addStartFromHereListener(final StartEventFromHereListener fromHereListener) {
		addListener(StartSystemFromHereEvent.class, fromHereListener);
	}
	
	public static void addShowEventInformationListener(final ShowEventInformationListener listener) {
		addListener(ShowEventRequested.class, listener);
	}
	
	public static void addBreakpointEventListener(final BreakpointEventListener listener) {
		addListener(BreakpointEvent.class, listener);
	}

	public static void addClearUpListener(final SystemClearUpListener listener) {
		addListener(SystemClearedUp.class, listener);
	}

	@SuppressWarnings("unchecked")
	public static void addEventHandlerFoundListener(final EventHandlerMethodRetrieved<?, ?> listener) {
		addListener(EventHandlerFound.class, listener);
	}
	
	@SuppressWarnings("unchecked")
	public static <E, M> void addEventHandlerFoundListener(final Class<E> eventType, final Class<M> methodType,
			final Consumer<M> methodRetrieved) {
		addListener(EventHandlerFound.class, new EventHandlerMethodRetrieved<E, M>() {

			@Override
			public void methodRetrieved(final M method) {
				methodRetrieved.accept(method);
			}

			@Override
			public Class<M> getMethodType() {
				return methodType;
			}

			@Override
			public Class<E> getEventType() {
				return eventType;
			}

		});
	}

	public static void addEventHandlerRetrieved(final EventHandlerRetrievedListener listener) {
		addListener(EventHandlerEvent.class, listener);
	}

	public static <T extends ListenerEvent> void addListener(final Class<T> eventType,
			final EventListener<T> eventListener) {
		System.out.println("Add listener for " + eventType.getSimpleName() + ": " + eventListener.getClass().getName());
		getDefaultInstance().eventListeners
							.computeIfAbsent(eventType, et -> new LinkedList<EventListener>())
							.add(eventListener);
	}
	
	public static void addActivationVoter(final EventDebugActivationVote voter) {
		getDefaultInstance().activationVoters.add(voter);
	}

	public static boolean isEventHandler(final Object isHandler) {
		return getDefaultInstance().handlerChecker.stream().anyMatch(checker -> checker.isEventHandler(isHandler));
	}

	//
	// CALLING EVENTS
	//
	
	public static void callEvent(final ListenerEvent listenerEvent) {
		getDefaultInstance().eventListeners.entrySet().stream()
				.filter(entry -> entry.getKey().isAssignableFrom(listenerEvent.getClass()))
				.flatMap(entry -> entry.getValue().stream())
							.forEach(listener -> listener.onEvent(listenerEvent));
	}
	
	public static void startFromHere(final IDebugEvent debugEvent) {
		EventDebugClient.sendMessage(RestartSystemFromEvent.from(debugEvent));
		callEvent(new StartSystemFromHereEvent(debugEvent));
	}
	
	public static void showRuntimeEventInformation(final IDebugEvent debugEvent) {
		callEvent(new ShowEventRequested(debugEvent, 0));
	}
	
	public static void pushHandler(final AbstractIDebugEventHandler abstractIDebugEventHandler) {
		EventDebugClient.sendMessage(NewEventHandlerStarted.from(abstractIDebugEventHandler));
	}

	public static <JAVA_METHOD_TYPE, JAVA_EVENT_TYPE> void findEventHandlers(final JAVA_EVENT_TYPE event,
			final Class<? super JAVA_EVENT_TYPE> handlerType, final Class<JAVA_METHOD_TYPE> javaMethodType) {
		findEventHandlers(event, handlerType, javaMethodType, null, null);
	}
	
	public static <JAVA_METHOD_TYPE, JAVA_EVENT_TYPE> void findEventHandlers(
			final JAVA_EVENT_TYPE event, 
			final Class<? super JAVA_EVENT_TYPE> eventType,
			final Class<JAVA_METHOD_TYPE> javaMethodType, 
			final Consumer<JAVA_METHOD_TYPE> methodFound, 
			final Runnable onCompletion) {
		final Consumer<JAVA_METHOD_TYPE> doOnMethodFound = method -> {
			if (methodFound != null) {
				methodFound.accept(method);
			}
			callEvent(new EventHandlerFound<>(method));
		};
		final Runnable doOnCompletion = () -> {
			if (onCompletion != null) {
				onCompletion.run();
			}
		};
		// final Class<JAVA_EVENT_TYPE> eventType = (Class<JAVA_EVENT_TYPE>)
		// event.getClass();

		getEventHandlerFinders(eventType, javaMethodType).stream()
			.findAny()
				.ifPresentOrElse(finder -> finder.retrieveMethods(event, doOnMethodFound, doOnCompletion),
						() -> System.out.println("No finder found for " + eventType.getSimpleName() + " and "
								+ javaMethodType.getSimpleName()));
	}
	
	//
	// PROVIDER AND INSTANCES
	//
	
	public static EventProvider getEventProvider() {
		return getDefaultInstance().providers;
	}
	
	public static EventDebugSystem getDefaultInstance() {
		return Activator.getDefault().getDefaultInstance();
	}
	
	public static void clear() {
		callEvent(new SystemClearedUp());
	}

	public static void close() {
		EventDebugServer.closeServer();
	}

	public static void updateHandler(final AbstractIDebugEventHandler abstractIDebugEventHandler) {
		EventDebugClient.sendMessage(NewEventHandlerFinished.from(abstractIDebugEventHandler));
	}

	public static Settings getSettings() {
		return getDefaultInstance().settings;
	}

	private static record EventHandlerType(Class<?> javaEventType, Class<?> javaMethodType) {
	}

}
