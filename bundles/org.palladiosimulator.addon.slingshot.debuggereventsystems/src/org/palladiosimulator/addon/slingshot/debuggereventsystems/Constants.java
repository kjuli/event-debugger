package org.palladiosimulator.addon.slingshot.debuggereventsystems;

public final class Constants {
	
	public static final String CONSUMER_EXTENSION_POINT_ID = "org.palladiosimulator.addon.slingshot.debuggereventsystem.consumer";
	public static final String CONSUMER_EXTENSION_POINT_ELEMENT_NAME = "consumer";
	public static final String CONSUMER_EXTENSION_POINT_ATTRIBUTE_NAME = "class";
	
	public static final String HANDLER_EXTENSION_POINT_ID = "org.palladiosimulator.addon.slingshot.debuggereventsystem.eventhandlerfinder";
	public static final String HANDLER_EXTENSION_POINT_ELEMENT_NAME = "handlerfinder";
	public static final String HANDLER_EXTENSION_POINT_CLASS = "class";
	public static final String HANDLER_EXTENSION_POINT_JAVA_METHOD_TYPE = "javaMethodType";
	public static final String HANDLER_EXTENSION_POINT_JAVA_EVENT_TYPE = "javaEventType";
	
	public static final String LISTENER_EXTENSION_POINT_ID = "org.palladiosimulator.addon.slingshot.debuggereventsystem.listener";
	public static final String LISTENER_EXTENSION_POINT_ELEMENT_NAME = "listener";
	public static final String LISTENER_EXTENSION_POINT_EVENT_TYPE = "eventType";
	public static final String LISTENER_EXTENSION_POINT_CLASS = "class";

	public static final Runnable NO_ACTION = () -> {
	};

	private Constants() { }
	
}
