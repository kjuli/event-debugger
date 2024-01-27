package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.wizard.addeventbreakpoint;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

public class AddEventBreakpointWizard extends Wizard {
	
	public static final String TITLE = "Add Event Breakpoint";
	
	private final List<WizardPage> pages;

	public AddEventBreakpointWizard() {
		this.pages = List.of(new EventTypeAndHandlersPage());
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addPages() {
		this.pages.forEach(this::addPage);
	}

	@Override
	public String getWindowTitle() {
		return TITLE;
	}
	
}
