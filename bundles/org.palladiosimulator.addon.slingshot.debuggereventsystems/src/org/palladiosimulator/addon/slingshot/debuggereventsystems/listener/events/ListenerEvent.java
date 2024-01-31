package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

/**
 * A generic event for the listener system.
 * <p>
 * Specific events need to subtype this class in order to create custom
 * listeners for it.
 * 
 * @author Julijan Katic
 */
public class ListenerEvent {

	private final Object source;
	private final int eventDetail;

	public ListenerEvent(final Object source, final int eventDetail) {
		this.source = source;
		this.eventDetail = eventDetail;
	}

	public Object getSource() {
		return source;
	}

	public int getEventDetail() {
		return eventDetail;
	}

}
