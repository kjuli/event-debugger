package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

public class EclipseDebugWorkbenchListener implements IWorkbenchListener {

	@Override
	public boolean preShutdown(final IWorkbench workbench, final boolean forced) {
		// EventDebugSystem.close();
		return true;
	}

	@Override
	public void postShutdown(final IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
