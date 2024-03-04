package org.palladiosimulator.addon.slingshot.debuggereventsystems.settings;

import java.io.Serializable;

/**
 * Class to maintain the settings of the application.
 * <p>
 * The front-end should use this class to save preferences and settings, or to
 * display them to the user.
 * <p>
 * The back-end can use this information to make certain adjustments, for
 * example to cache only a certain amount of events.
 * 
 * @author Julijan Katic
 */
public class Settings implements Serializable {

	private String eventOutputFile = "";
	private boolean activateEventOutput = false;

	private int maxCache = 10;
	private EventCacheMode eventCacheMode = EventCacheMode.BOUNDED;
	private OutputFormat outputFormat = OutputFormat.XML;
	
	private int port = 9865;
	
	/**
	 * Sets whether the cache can be unbounded, bounded or deactivated.
	 */
	public enum EventCacheMode {
		DEACTIVATED, BOUNDED, UNBOUNDED
	}
	
	/**
	 * Sets the output format of the event graph
	 */
	public enum OutputFormat {
		XML, GRAPH_VIZ
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

	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}
	
}
