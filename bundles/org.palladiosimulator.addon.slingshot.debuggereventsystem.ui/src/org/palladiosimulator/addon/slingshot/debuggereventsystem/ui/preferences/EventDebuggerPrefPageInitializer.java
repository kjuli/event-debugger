package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.Constants;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.EclipseEventDebugUiPlugin;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;

public class EventDebuggerPrefPageInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				EclipseEventDebugUiPlugin.PLUGIN_ID);

		scopedPreferenceStore.setDefault(Constants.Preference.CACHE,
				EventDebugSystem.getSettings().getEventCacheMode().name());
		scopedPreferenceStore.setDefault(Constants.Preference.CACHE_NUM, EventDebugSystem.getSettings().getMaxCache());
		scopedPreferenceStore.setDefault(Constants.Preference.PORT, EventDebugSystem.getSettings().getPort());
		scopedPreferenceStore.setDefault(Constants.Preference.PATH,
				EventDebugSystem.getSettings().getEventOutputFile());
	}

}
