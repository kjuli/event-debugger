package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui;

/**
 * Defines all the constants of this plugin that can be used to retrieve them.
 * 
 * 
 * @author Julijan Katic
 * @see Preference
 */
public class Constants {

	/** The id of the preference page */
	public static final String PREFERENCE_ID = "org.palladiosimulator.addon.slingshot.debuggereventsystem.page";

	/**
	 * Defines the keys for each preference type.
	 * 
	 * @author Julijan Katic
	 */
	public static class Preference {
		public static final String PATH = "PATH";
		public static final String PREFERENCE_ACTIVATE_DEBUGGER = "ACTIVATE_DEBUGGER";
		public static final String CACHE = "CACHE_MODE";
		public static final String PORT = "PORT";
		public static final String CACHE_NUM = "MAX_CACHE_NUM";
		public static final String OUTPUT_TYPE = "OUTPUT_TYPE";
	}
}
