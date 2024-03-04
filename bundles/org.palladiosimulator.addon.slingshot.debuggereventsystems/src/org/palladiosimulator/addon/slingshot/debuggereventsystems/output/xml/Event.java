package org.palladiosimulator.addon.slingshot.debuggereventsystems.output.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

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

	public static Event fromDebugEvent(final IDebugEvent event) {
		final Event result = new Event();
		
		result.setId(event.getId().getId());
		result.setName(event.getName());
		result.setTime(event.getTimeInformation().getTime());
		
		final List<HandlerRef> handlers = EventDebugSystem.getEventHolder().getHandlersByEvent(event.getId())
			.stream()
			.map(HandlerRef::fromHandler)
			.collect(Collectors.toList());
		
		result.setHandlers(handlers);
		
		final List<EventRef> causedBy = EventDebugSystem
			.getEventTree()
			.getLatestParents(event.getId(), 1)
			.stream()
			.map(node -> EventRef.from(node.debuggedEvent()))
			.collect(Collectors.toList());
		
		result.setCausedBy(causedBy);
		
		return result;
	}
}
