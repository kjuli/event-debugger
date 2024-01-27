package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.io.Serializable;

public interface IDebugEventHandler extends Serializable {
	
	public String getId();

	public String getName();
	
	public String ofEvent();
	
	public HandlerStatus getStatus();

}
