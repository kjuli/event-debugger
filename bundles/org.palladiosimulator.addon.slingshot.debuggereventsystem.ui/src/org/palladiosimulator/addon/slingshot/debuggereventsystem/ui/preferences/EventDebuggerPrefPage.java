package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.Constants;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.EclipseEventDebugUiPlugin;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.settings.Settings;

/**
 * This class builds the preference GUI and makes changes to the event-debugger
 * settings when applying the preferences.
 * 
 * @author Julijan Katic
 */
public class EventDebuggerPrefPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String DESCRIPTION = """
		Change settings for the general event debug system. Here, you also can change the module\s\
		for finding the right events.""";

	private static final String[][] CACHE_RADIO_SETTINGS = {
			{ "Don't cache events", Settings.EventCacheMode.DEACTIVATED.name() },
			{ "Cache events unboundedly (may cause performance issues)", Settings.EventCacheMode.UNBOUNDED.name() },
			{ "Cache a maximum number of events", Settings.EventCacheMode.BOUNDED.name() }
	};
	
	private static final String[][] OUTPUT_FORMAT_RADIO_SETTINGS = {
			{ "XML", Settings.OutputFormat.XML.name() },
			{ "GraphViz", Settings.OutputFormat.GRAPH_VIZ.name() }
	};

	private DirectoryFieldEditor directoryFE;
	private RadioGroupFieldEditor outputSetting;
	
	private IntegerFieldEditor portFE;
	private IntegerFieldEditor maxCacheNumberFE;
	private RadioGroupFieldEditor cacheSetting;

	public EventDebuggerPrefPage() {
		super(GRID);
	}


	@Override
	public void init(final IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, EclipseEventDebugUiPlugin.PLUGIN_ID));
		setDescription(DESCRIPTION);
	}

	@Override
	protected void createFieldEditors() {
		directoryFE = new DirectoryFieldEditor(Constants.Preference.PATH, "&Output Event Graph",
				getFieldEditorParent());
		addField(directoryFE);

		outputSetting = new RadioGroupFieldEditor(Constants.Preference.OUTPUT_TYPE, "Output &Format", 2, OUTPUT_FORMAT_RADIO_SETTINGS, getFieldEditorParent());
		addField(outputSetting);

		cacheSetting = new RadioGroupFieldEditor(Constants.Preference.CACHE, "&Cache Setting", 1, CACHE_RADIO_SETTINGS,
				getFieldEditorParent());
		addField(cacheSetting);

		maxCacheNumberFE = new IntegerFieldEditor(Constants.Preference.CACHE_NUM, "&Max caching events",
				getFieldEditorParent());
		addField(maxCacheNumberFE);

		portFE = new IntegerFieldEditor(Constants.Preference.PORT, "&Port Number", getFieldEditorParent());
		addField(portFE);
		
	}

	
}
