package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

/**
 * This class is used to represent a {@link IDebugEventHandler}. It is mostly
 * used either by the front-end to save the retrieved information from a
 * message, or to serialize it into a message.
 * 
 * @author Julijan Katic
 */
public class ConcreteDebugEventHandler implements IDebugEventHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8733475972609283539L;
	private final DebugEventId eventId;
	private final String name;
	private final HandlerStatus status;
	private final HandlerId id;
	
	
	public ConcreteDebugEventHandler(final HandlerId id, final DebugEventId eventId, final String name,
			final HandlerStatus status) {
		this.id = id;
		this.eventId = eventId;
		this.name = name;
		this.status = status;
	}

	@Override
	public HandlerId getId() {
		return id;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public DebugEventId ofEvent() {
		// TODO Auto-generated method stub
		return eventId;
	}

	@Override
	public HandlerStatus getStatus() {
		// TODO Auto-generated method stub
		return status;
	}

}
