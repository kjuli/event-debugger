package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.shells;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.ShowEventInformationListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ShowEventRequested;

/**
 * This listener is used to open a new {@link EventDetailsShell} as soon as this
 * is requested.
 * 
 * @author Julijan Katic
 */
public class EclipseShowRuntimeInformationListener implements ShowEventInformationListener {

	@Override
	public void onEvent(final ShowEventRequested event) {
		final EventDetailsShell detailsShell = new EventDetailsShell(Display.getCurrent(), SWT.NONE);
		detailsShell.setEvent(event.getDebuggedEvent());
		detailsShell.addEventHandler(
				EventDebugSystem.getEventHolder().getHandlersByEvent(event.getDebuggedEvent().getId()));
		detailsShell.open();
	}

}
