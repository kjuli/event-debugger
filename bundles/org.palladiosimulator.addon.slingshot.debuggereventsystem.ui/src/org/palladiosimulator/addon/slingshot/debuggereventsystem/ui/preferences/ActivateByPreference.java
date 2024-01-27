package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.Constants;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.EclipseEventDebugUiPlugin;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.system.EventDebugActivationVote;

public class ActivateByPreference implements EventDebugActivationVote {

	private final ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
			EclipseEventDebugUiPlugin.PLUGIN_ID);
	private boolean isActive;

	@Override
	public boolean shouldActivateDebugger() {
		return isActive
				&& scopedPreferenceStore.getBoolean(Constants.Preference.PREFERENCE_ACTIVATE_DEBUGGER);
	}

	public void setActive(final boolean b) {
		isActive = b;
	}

}
