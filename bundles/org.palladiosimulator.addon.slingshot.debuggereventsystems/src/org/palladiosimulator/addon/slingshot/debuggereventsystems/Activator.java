package org.palladiosimulator.addon.slingshot.debuggereventsystems;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.palladiosimulator.addon.slingshot.debuggereventsystems"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	private static EventDebugSystem system;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		if (system == null) {
			system = new EventDebugSystem();
			system.init();
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		system = null;
		super.stop(context);
	}

	public EventDebugSystem getDefaultInstance() {
		return system;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
