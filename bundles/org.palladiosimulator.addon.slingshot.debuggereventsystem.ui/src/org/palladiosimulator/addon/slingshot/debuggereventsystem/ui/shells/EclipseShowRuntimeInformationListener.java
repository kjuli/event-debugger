package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.shells;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.view.EventHolder;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.ShowEventInformationListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.ShowEventRequested;

public class EclipseShowRuntimeInformationListener implements ShowEventInformationListener {

	@Override
	public void onEvent(final ShowEventRequested event) {
		System.out.println("Let's show for " + event.getDebuggedEvent().getId());
		final EventDetailsShell detailsShell = new EventDetailsShell(Display.getCurrent(), SWT.NONE);
		detailsShell.setEvent(event.getDebuggedEvent());
		detailsShell.addEventHandler(EventHolder.getHandlersByEvent(event.getDebuggedEvent().getId()));
		detailsShell.open();
	}

}
