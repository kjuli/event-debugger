package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers.EclipseDebugEventListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers.EclipseEventHandlerRetrieved;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers.JavaLineBreakpointAdder;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.preferences.ActivateByPreference;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.shells.EclipseShowRuntimeInformationListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.settings.Settings;

/**
 * The plugin is part of the front-end debugger. As soon as the application
 * starts this plugin, it activates itself into the EventDebugSystem.
 * 
 * @author Julijan Katic
 */
public class EclipseEventDebugUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.palladiosimulator.addon.slingshot.debuggereventsystem.ui"; //$NON-NLS-1$

	// The shared instance
	private static EclipseEventDebugUiPlugin plugin;
	private final ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
			PLUGIN_ID);
	
	/**
	 * The constructor
	 */
	public EclipseEventDebugUiPlugin() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registerIntoEventDebugSystem();

		addSettingsChangeListener(new AddSettingsToDebugSystem());
	}

	/**
	 * Helper method to make all the calls to the listener.
	 */
	private void registerIntoEventDebugSystem() {
		final ActivateByPreference abp = new ActivateByPreference();
		final EclipseDebugEventListener edel = new EclipseDebugEventListener(abp);

		DebugPlugin.getDefault().addDebugEventListener(edel);
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(new JavaLineBreakpointAdder());

		EventDebugSystem.initializeEventHolder();
		EventDebugSystem.initializeEventTree();

		EventDebugSystem.addStartFromHereListener(edel);
		EventDebugSystem.addActivationVoter(abp);
		EventDebugSystem.addShowEventInformationListener(new EclipseShowRuntimeInformationListener());
		EventDebugSystem.addEventHandlerRetrieved(new EclipseEventHandlerRetrieved());
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		EventDebugSystem.clear();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EclipseEventDebugUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Adds a property change listener to the scoped preference store. This is
	 * useful to update the system when new changes to the debugger settings have
	 * been made.
	 * 
	 * @param listener The listener to add into the scoped preference store.
	 */
	public static void addSettingsChangeListener(final IPropertyChangeListener listener) {
		getDefault().scopedPreferenceStore.addPropertyChangeListener(listener);
	}

	/**
	 * The listener implementation to set the correct preferences into the
	 * {@link EventDebugSystem}.
	 * 
	 * @author Julijan Katic
	 * @see EventDebugSystem#getSettings()
	 */
	private static final class AddSettingsToDebugSystem implements IPropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent ev) {
			switch (ev.getProperty()) {
			case Constants.Preference.PATH:
				EventDebugSystem.getSettings().setEventOutputFile((String) ev.getNewValue());
				break;
			case Constants.Preference.PORT:
				EventDebugSystem.getSettings().setPort((Integer) ev.getNewValue());
				break;
			case Constants.Preference.CACHE:
				EventDebugSystem.getSettings()
						.setEventCacheMode(Settings.EventCacheMode.valueOf((String) ev.getNewValue()));
			case Constants.Preference.CACHE_NUM:
				EventDebugSystem.getSettings().setMaxCache((Integer) ev.getNewValue());
			}
		}

	}
}
