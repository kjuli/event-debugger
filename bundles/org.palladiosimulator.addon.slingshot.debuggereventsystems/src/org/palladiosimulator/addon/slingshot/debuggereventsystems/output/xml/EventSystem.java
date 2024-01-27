package org.palladiosimulator.addon.slingshot.debuggereventsystems.output.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "event-system")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventSystem {

	@XmlElement(name = "events")
	private List<Event> events;

	@XmlElement(name = "handlers")
	private List<Handler> handlers;

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(final List<Event> events) {
		this.events = events;
	}

	public List<Handler> getHandlers() {
		return handlers;
	}

	public void setHandlers(final List<Handler> handlers) {
		this.handlers = handlers;
	}

}
