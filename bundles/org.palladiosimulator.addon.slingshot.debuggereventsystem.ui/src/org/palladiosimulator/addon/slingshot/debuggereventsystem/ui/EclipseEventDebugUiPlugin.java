package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui;

import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers.EclipseDebugEventListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers.EclipseDebugWorkbenchListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers.EclipseEventHandlerRetrieved;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers.EclipseJDTBreakpointListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers.JavaLineBreakpointAdder;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.preferences.ActivateByPreference;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.shells.EclipseShowRuntimeInformationListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.shells.EventDetailsShell;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.view.EventViewFactory;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.AbstractIDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.AbstractIDebugEventHandler;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.HandlerStatus;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.settings.Settings;

/**
 * The activator class controls the plug-in life cycle
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
		final ActivateByPreference abp = new ActivateByPreference();
		final EclipseDebugEventListener edel = new EclipseDebugEventListener(abp);

		DebugPlugin.getDefault().addDebugEventListener(edel);
		JDIDebugModel.addJavaBreakpointListener(new EclipseJDTBreakpointListener());
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(new JavaLineBreakpointAdder());

		EventDebugSystem.addStartFromHereListener(edel);
		EventDebugSystem.addActivationVoter(abp);
		EventDebugSystem.addShowEventInformationListener(new EclipseShowRuntimeInformationListener());
		EventDebugSystem.addEventHandlerRetrieved(new EclipseEventHandlerRetrieved());
		EventDebugSystem.addClearUpListener(EventViewFactory.getInstance());

		if (PlatformUI.isWorkbenchRunning()) {
			// makeExample();
			PlatformUI.getWorkbench().addWorkbenchListener(new EclipseDebugWorkbenchListener());
		}
		
		addSettingsChangeListener(ev -> {
			switch (ev.getProperty()) {
			case Constants.Preference.PATH:
				EventDebugSystem.getSettings().setEventOutputFile((String) ev.getNewValue());
				break;
			case Constants.Preference.PORT:
				EventDebugSystem.getSettings().setPort((Integer) ev.getNewValue());
				break;
			case Constants.Preference.CACHE:
				EventDebugSystem.getSettings().setEventCacheMode(Settings.EventCacheMode.valueOf((String) ev.getNewValue()));
			case Constants.Preference.CACHE_NUM:
				EventDebugSystem.getSettings().setMaxCache((Integer) ev.getNewValue());
			}
		});
	}

	private void makeExample() {
		final EventDetailsShell shell = new EventDetailsShell(Display.getDefault(), SWT.NONE);
		shell.setEvent(new AbstractIDebugEvent("Sample ID", "Sample Event Name", () -> 0.0));
		shell.addEventHandler(
				List.of(new AbstractIDebugEventHandler("Sample ID", "EventHandlerName", HandlerStatus.STARTED)));
		shell.open();
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

	public static void addSettingsChangeListener(final IPropertyChangeListener listener) {
		getDefault().scopedPreferenceStore.addPropertyChangeListener(listener);
	}

}
