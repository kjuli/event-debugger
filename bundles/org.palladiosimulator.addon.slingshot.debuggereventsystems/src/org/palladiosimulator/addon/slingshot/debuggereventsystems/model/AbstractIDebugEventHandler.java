package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

public class AbstractIDebugEventHandler implements IDebugEventHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8733475972609283539L;
	private final String eventId;
	private final String name;
	private final HandlerStatus status;
	private final String id;
	
	
	public AbstractIDebugEventHandler(final String eventId, final String name, final HandlerStatus status) {
		this(eventId + ":" + name, eventId, name, status);
	}
	
	public AbstractIDebugEventHandler(final String id, final String eventId, final String name,
			final HandlerStatus status) {
		this.id = id;
		this.eventId = eventId;
		this.name = name;
		this.status = status;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public String ofEvent() {
		// TODO Auto-generated method stub
		return eventId;
	}

	@Override
	public HandlerStatus getStatus() {
		// TODO Auto-generated method stub
		return status;
	}

}
