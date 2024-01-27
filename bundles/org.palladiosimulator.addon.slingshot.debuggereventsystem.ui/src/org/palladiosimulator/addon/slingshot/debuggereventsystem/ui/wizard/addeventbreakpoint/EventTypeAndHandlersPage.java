package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.wizard.addeventbreakpoint;

import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

public class EventTypeAndHandlersPage extends WizardPage {
	
	private static final String LBLEVENTTYPE_TEXT = "Event Type";
	public static final String TITLE = "Event Type and Handlers";
	public static final String DESCRIPTION = "Specify the type of the event and check for the handlers.";
	
	private Composite container;
	private FormLayout layout;
	
	private Label lblEventType;
	private Text txtEventType;
	private Button btnFindType;
	private Label lblEventHandlers;
	// TODO: Table
	
	public EventTypeAndHandlersPage() {
		super(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		this.container = new Composite(parent, SWT.NONE);
		this.layout = new FormLayout();
		container.setLayout(layout);
		
		this.createLblEventType();
		this.createTxtEventType();
		this.createBtnFindType();
	}
	
	private void createLblEventType() {
		lblEventType = new Label(container, SWT.NONE);
		lblEventType.setText(LBLEVENTTYPE_TEXT);
		
		final FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.top = new FormAttachment(0, 0);
		lblEventType.setLayoutData(formData);
	}
	
	private void createTxtEventType() {
		txtEventType = new Text(container, SWT.NONE);
		
		final FormData formData = new FormData();
		formData.top = new FormAttachment(lblEventType);
		formData.left = new FormAttachment(0, 0);
		txtEventType.setLayoutData(formData);
	}
	
	private void createBtnFindType() {
		btnFindType = new Button(container, SWT.PUSH);
		
		btnFindType.setText("Find Type...");
		btnFindType.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::findType));
		
		final FormData formData = new FormData();
		formData.left = new FormAttachment(txtEventType);
		btnFindType.setLayoutData(formData);
	}

	@SuppressWarnings("restriction")
	private void findType(SelectionEvent event) {
		final SelectionDialog dialog = new OpenTypeSelectionDialog(container.getShell(), true, PlatformUI.getWorkbench().getProgressService(), null, IJavaSearchConstants.TYPE);
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);
		dialog.open(); // Save the type
	}
	
	
}
