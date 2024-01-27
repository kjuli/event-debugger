package org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events;

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
