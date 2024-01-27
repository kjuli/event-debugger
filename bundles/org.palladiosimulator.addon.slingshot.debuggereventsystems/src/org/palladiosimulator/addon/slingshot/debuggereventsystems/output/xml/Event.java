package org.palladiosimulator.addon.slingshot.debuggereventsystems.output.xml;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public final class Event implements Serializable {

	@XmlAttribute
	private String id;

	@XmlAttribute
	private String name;

	@XmlElement
	private double time;

	@XmlElementWrapper(name = "handlers")
	@XmlElement
	private List<HandlerRef> handlers;

	@XmlElementWrapper
	@XmlElement
	private List<EventRef> causedBy;

	@XmlElement(name = "parent")
	private EventRef parent;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public double getTime() {
		return time;
	}

	public void setTime(final double time) {
		this.time = time;
	}

	public List<HandlerRef> getHandlers() {
		return handlers;
	}

	public void setHandlers(final List<HandlerRef> handlers) {
		this.handlers = handlers;
	}

	public List<EventRef> getCausedBy() {
		return causedBy;
	}

	public void setCausedBy(final List<EventRef> causedBy) {
		this.causedBy = causedBy;
	}

	public EventRef getParent() {
		return parent;
	}

	public void setParent(final EventRef parent) {
		this.parent = parent;
	}

}
