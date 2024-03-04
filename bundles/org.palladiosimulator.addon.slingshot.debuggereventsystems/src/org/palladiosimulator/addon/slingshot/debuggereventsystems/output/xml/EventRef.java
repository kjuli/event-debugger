package org.palladiosimulator.addon.slingshot.debuggereventsystems.output.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.DebugEventId;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

@XmlAccessorType(XmlAccessType.FIELD)
public class EventRef {

	@XmlAttribute(name = "ref")
	private String eventId;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(final String eventId) {
		this.eventId = eventId;
	}
	
	public static EventRef from(final IDebugEvent event) {
		return from(event.getId());
	}
	
	public static EventRef from(final DebugEventId id) {
		final EventRef result = new EventRef();
		result.setEventId(id.getId());
		return result;
	}
}
