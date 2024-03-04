package org.palladiosimulator.addon.slingshot.debuggereventsystems.output.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerStatus;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

@XmlAccessorType(XmlAccessType.FIELD)
public class HandlerRef {

	@XmlAttribute(name = "ref")
	private String handlerId;

	@XmlAttribute
	private boolean success;

	public String getHandlerId() {
		return handlerId;
	}

	public void setHandlerId(final String handlerId) {
		this.handlerId = handlerId;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(final boolean success) {
		this.success = success;
	}
	
	public static HandlerRef fromHandler(final IDebugEventHandler handler) {
		final HandlerRef ref = new HandlerRef();
		
		ref.setHandlerId(handler.getId().getId());
		ref.setSuccess(handler.getStatus().equals(HandlerStatus.SUCCESS));
		
		return ref;
	}
	
}
