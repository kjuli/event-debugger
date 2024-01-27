package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/** This is a generic event that is published during *runtime* */
public interface IDebugEvent extends Serializable {

	public static final String TYPE_KEY = "EVENT_TYPE";
	
	public String getId();
	
	public String getName();
	
	public Object getEvent();
	
	public Optional<Object> getParentEvent();
	
	public Map<String, Object> getMetaInformation();
	
	public TimeInformation getTimeInformation();
	
}
