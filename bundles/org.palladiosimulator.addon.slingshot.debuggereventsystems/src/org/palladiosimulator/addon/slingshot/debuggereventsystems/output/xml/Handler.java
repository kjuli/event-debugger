package org.palladiosimulator.addon.slingshot.debuggereventsystems.output.xml;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

@XmlAccessorType(XmlAccessType.FIELD)
public class Handler {
	
	@XmlAttribute(name = "id")
	private String handlerId;

	@XmlElement(name = "event-ref")
	private String eventId;

	@XmlAttribute
	private String name;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(final String eventId) {
		this.eventId = eventId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getHandlerId() {
		return handlerId;
	}

	public void setHandlerId(String handlerId) {
		this.handlerId = handlerId;
	}

	public static Handler from(IDebugEventHandler handler) {
		final Handler result = new Handler();
		result.setName(handler.getName());
		result.setEventId(handler.ofEvent().getId());
		result.setHandlerId(handler.getId().getId());
		return result;
	}

}
