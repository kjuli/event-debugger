package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.view;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

/**
 * Factory class ensuring that only one instance of the {@link EventView}
 * exists.
 * 
 * @author Julijan Katic
 */
public class EventViewFactory implements IExecutableExtensionFactory {
	
	private static EventView eventView;

	public static EventView getInstance() {
		if (eventView == null) {
			eventView = new EventView();
		}
		return eventView;
	}

	@Override
	public Object create() throws CoreException {
		return getInstance();
	}

}
