package org.palladiosimulator.addon.slingshot.debuggereventsystems;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.cache.EventHolder;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.cache.EventTree;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.handler.StaticEventHandlerHolder;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.BreakpointEventListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventConsumer;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventHandlerMethodRetrieved;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventHandlerRetrievedListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.ListenerHolder;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.ShowEventInformationListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.StartEventFromHereListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.SystemClearUpListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.BreakpointEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.DebugEventPublished;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.EventHandlerFound;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ListenerEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ShowEventRequested;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.StartSystemFromHereEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.SystemClearedUp;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.system.EventDebugActivationVote;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.EventBreakpoint;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.settings.Settings;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.EventDebugClient;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.EventDebugServer;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.Message;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.NewEventHandlerFinished;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.NewEventHandlerStarted;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.NewEventProvidedMessage;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.socket.messages.RestartSystemFromEvent;

/**
 * Provides centralized access to the Event Debug System for both front-end and
 * back-end debug components.
 * <p>
 * This class is a singleton that facilitates the communication between
 * different parts of the Eclipse IDE and the debuggee. It manages event
 * listeners, event handlers, and settings. It is responsible for initializing,
 * starting, and stopping the event debug system, as well as managing debug
 * events, breakpoints, and event handlers.
 * </p>
 */
public final class EventDebugSystem {
	
	private final ListenerHolder listenerHolder = new ListenerHolder();
	private final List<EventDebugActivationVote> activationVoters = new LinkedList<>();
	private final StaticEventHandlerHolder eventHandlerHolder = new StaticEventHandlerHolder();
	private final Settings settings = new Settings();
	
	
	private EventTree eventTree;
	private EventHolder holder;

	EventDebugSystem() {
		addClientEventListener(new MessageToDebugEventMapper());
	}
	
	/**
	 * Initializes the listener holder and event handler holder used to store and
	 * manage the listeners and event-handler finders.
	 */
	void init() {
		listenerHolder.findListeners();
		eventHandlerHolder.findEventHandlerFinders();
	}

	/**
	 * Starts the socket client and possibly the socket server (if it was not
	 * started before).
	 */
	public static void listenToDebugEvents() {
		EventDebugServer.start();
		EventDebugClient.getInstance();
	}

	/**
	 * Initializes the event holder for storing debug events.
	 */
	public static void initializeEventHolder() {
		getDefaultInstance().holder = new EventHolder();
	}

	/**
	 * Initializes the event tree for organizing debug events.
	 */
	public static void initializeEventTree() {
		getDefaultInstance().eventTree = new EventTree();
	}

	/**
	 * Retrieves the event holder.
	 *
	 * @return The event holder instance.
	 */
	public static EventHolder getEventHolder() {
		return getDefaultInstance().holder;
	}

	/**
	 * Retrieves the event tree.
	 *
	 * @return The event tree instance.
	 */
	public static EventTree getEventTree() {
		return getDefaultInstance().eventTree;
	}

	/**
	 * Retrieves the static event handler holder.
	 *
	 * @return The static event handler holder.
	 */
	public static StaticEventHandlerHolder getEventHandlerFinderHolder() {
		return getDefaultInstance().eventHandlerHolder;
	}

	/**
	 * Registers a client event listener.
	 *
	 * @param onMessage The consumer to handle incoming messages.
	 */
	public static void addClientEventListener(final Consumer<Message> onMessage) {
		EventDebugClient.addListener(onMessage);
	}


	/**
	 * Adds a breakpoint.
	 *
	 * @param eventBreakpoint The event breakpoint to add.
	 */
	public static void addBreakpoint(final EventBreakpoint eventBreakpoint) {
		callEvent(new BreakpointEvent(eventBreakpoint, BreakpointEvent.BreakpointEventType.ADDED));
	}
	

	/**
	 * Registers an event consumer.
	 *
	 * @param consumer The event consumer to register.
	 */
	public static void addConsumer(final EventConsumer consumer) {
		getDefaultInstance().listenerHolder.addListener(DebugEventPublished.class, consumer);
	}

	/**
	 * Removes a breakpoint.
	 *
	 * @param eventBreakpoint The event breakpoint to remove.
	 */
	public static void removeBreakpoint(final EventBreakpoint eventBreakpoint) {
		callEvent(new BreakpointEvent(eventBreakpoint, BreakpointEvent.BreakpointEventType.REMOVED));
	}

	/**
	 * Checks if debug is enabled based on activation voters.
	 *
	 * @return True if debug is enabled, false otherwise.
	 */
	public static boolean isDebugEnabled() {
		return (getDefaultInstance().activationVoters.isEmpty()
				|| getDefaultInstance().activationVoters.stream().anyMatch(voter -> voter.shouldActivateDebugger()));
	}
	
	/**
	 * Registers a listener to handle "start from here" events.
	 *
	 * @param fromHereListener The listener to register.
	 */
	public static void addStartFromHereListener(final StartEventFromHereListener fromHereListener) {
		addListener(StartSystemFromHereEvent.class, fromHereListener);
	}
	
	/**
	 * Registers a listener to display event information.
	 *
	 * @param listener The listener to register.
	 */
	public static void addShowEventInformationListener(final ShowEventInformationListener listener) {
		addListener(ShowEventRequested.class, listener);
	}
	
	/**
	 * Registers a breakpoint event listener.
	 *
	 * @param listener The listener to register.
	 */
	public static void addBreakpointEventListener(final BreakpointEventListener listener) {
		addListener(BreakpointEvent.class, listener);
	}

	/**
	 * Registers a listener to handle system clear-up events.
	 *
	 * @param listener The listener to register.
	 */
	public static void addClearUpListener(final SystemClearUpListener listener) {
		addListener(SystemClearedUp.class, listener);
	}

	/**
	 * Registers a listener to handle event handler found events.
	 *
	 * @param listener The listener to register.
	 */
	@SuppressWarnings("unchecked")
	public static void addEventHandlerFoundListener(final EventHandlerMethodRetrieved<?, ?> listener) {
		addListener(EventHandlerFound.class, listener);
	}
	
	/**
	 * Registers a listener for when an event handler method is retrieved.
	 *
	 * @param eventType       The class of the event type.
	 * @param methodType      The class of the method type.
	 * @param methodRetrieved The consumer to handle the retrieved method.
	 */
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

	/**
	 * Registers a listener to handle event handler retrieved events.
	 *
	 * @param listener The listener to register.
	 */
	public static void addEventHandlerRetrieved(final EventHandlerRetrievedListener listener) {
		addListener(EventHandlerEvent.class, listener);
	}

	/**
	 * Registers an event listener for a specific type of event.
	 *
	 * @param eventType     The class of the event type.
	 * @param eventListener The event listener to register.
	 */
	public static <T extends ListenerEvent> void addListener(final Class<T> eventType,
			final EventListener<T> eventListener) {
		getDefaultInstance().listenerHolder.addListener(eventType, eventListener);
	}
	
	/**
	 * Registers an activation voter.
	 *
	 * @param voter The activation voter to register.
	 */
	public static void addActivationVoter(final EventDebugActivationVote voter) {
		getDefaultInstance().activationVoters.add(voter);
	}

	/**
	 * Checks if the given object is an event handler.
	 *
	 * @param object The object to check.
	 * @return True if the object is an event handler, false otherwise.
	 */
	public static boolean isEventHandler(final Object object) {
		return getDefaultInstance().eventHandlerHolder.isEventHandler(object);
	}

	/**
	 * Triggers a listener event.
	 *
	 * @param listenerEvent The event to trigger.
	 */
	public static void callEvent(final ListenerEvent listenerEvent) {
		getDefaultInstance().listenerHolder.callEvent(listenerEvent);
	}
	
	/**
	 * Replays the debugging process from a specified debug event.
	 *
	 * @param debugEvent The debug event to start from.
	 */
	public static void startFromHere(final IDebugEvent debugEvent) {
		EventDebugClient.sendMessage(RestartSystemFromEvent.from(debugEvent));
		callEvent(new StartSystemFromHereEvent(debugEvent));
	}
	
	/**
	 * Commands the front-end debugger to display runtime information for a debug
	 * event.
	 *
	 * @param debugEvent The debug event for which to display information.
	 */
	public static void showRuntimeEventInformation(final IDebugEvent debugEvent) {
		callEvent(new ShowEventRequested(debugEvent, 0));
	}
	
	/**
	 * Notifies that a new event handler has started processing.
	 *
	 * @param handler The event handler that started.
	 */
	public static void pushHandler(final IDebugEventHandler handler) {
		EventDebugClient.sendMessage(NewEventHandlerStarted.from(handler));
	}

	/**
	 * Provides a new debug event to the system.
	 *
	 * @param event The debug event to provide.
	 */
	public static void provideEvent(final IDebugEvent event) {
		EventDebugClient.sendMessage(NewEventProvidedMessage.fromEvent(event));
	}

	/**
	 * Retrieves the singleton instance of the Event Debug System.
	 *
	 * @return The singleton instance of the Event Debug System.
	 */
	public static EventDebugSystem getDefaultInstance() {
		return Activator.getDefault().getDefaultInstance();
	}
	
	/**
	 * Clears the Event Debug System.
	 * 
	 * @see SystemClearedUp
	 */
	public static void clear() {
		callEvent(new SystemClearedUp());
	}

	/**
	 * Closes the Event Debug Server.
	 */
	public static void close() {
		EventDebugServer.closeServer();
	}

	/**
	 * Notifies that an event handler has finished processing.
	 *
	 * @param handler The event handler that finished.
	 */
	public static void updateHandler(final IDebugEventHandler handler) {
		EventDebugClient.sendMessage(NewEventHandlerFinished.from(handler));
	}

	/**
	 * Retrieves the settings for the Event Debug System.
	 *
	 * @return The settings for the Event Debug System.
	 */
	public static Settings getSettings() {
		return getDefaultInstance().settings;
	}

}
