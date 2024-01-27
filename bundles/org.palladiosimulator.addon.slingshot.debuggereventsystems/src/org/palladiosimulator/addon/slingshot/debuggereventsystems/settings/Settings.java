package org.palladiosimulator.addon.slingshot.debuggereventsystems.settings;

import java.io.Serializable;

public class Settings implements Serializable {

	private String eventOutputFile = "";
	private boolean activateEventOutput = false;

	private int maxCache = 10;
	private EventCacheMode eventCacheMode = EventCacheMode.BOUNDED;

	private int port = 9865;

	public enum EventCacheMode {
		DEACTIVATED, BOUNDED, UNBOUNDED
	}

	public String getEventOutputFile() {
		return eventOutputFile;
	}

	public void setEventOutputFile(final String eventOutputFile) {
		this.eventOutputFile = eventOutputFile;
	}

	public boolean isActivateEventOutput() {
		return activateEventOutput;
	}

	public void setActivateEventOutput(final boolean activateEventOutput) {
		this.activateEventOutput = activateEventOutput;
	}

	public int getMaxCache() {
		return maxCache;
	}

	public void setMaxCache(final int maxCache) {
		this.maxCache = maxCache;
	}

	public EventCacheMode getEventCacheMode() {
		return eventCacheMode;
	}

	public void setEventCacheMode(final EventCacheMode eventCacheMode) {
		this.eventCacheMode = eventCacheMode;
	}

	public int getPort() {
		return port;
	}

	public void setPort(final int port) {
		this.port = port;
	}

}
