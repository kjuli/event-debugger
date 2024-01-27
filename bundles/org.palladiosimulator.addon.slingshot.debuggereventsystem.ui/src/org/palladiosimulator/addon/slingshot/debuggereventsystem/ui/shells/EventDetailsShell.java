package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.shells;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.utils.UiUtils;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

import com.google.common.base.Preconditions;

public class EventDetailsShell extends Shell {
	
	private static final String EVENT_HANDLERS_TEXT = "Event Handlers";
	private static final String TRACE_TEXT = "Trace";
	private static final String RESTART_TEXT = "Restart system from here..";
	private static final String NEXT_EVENT_TEXT = "Next Event";
	private static final String EVENT_DETAILS_TEXT = "Event Details";
	private static final String INFORMATION_TEXT = "Information";
	private static final String PREVIOUS_EVENT_TEXT = "Previous Event";
	
	private Label lblEventDetails;
	private Label lblEventName;
	
	private TabFolder tbfInformation;
	private TabItem tbiEventInformation;
	private TabItem tbiEventTrace;
	private TabItem tbiEventHandlers;
	
	private Composite cmpEventTrace;
	
	private Table tblTrace;
	private TableColumn eventNameColumn;
	private TableColumn eventTimeColumn;
	
	private Table tblEventHandlers;
	private TableColumn handlerName;
	private TableColumn handlerStatus;

	private Label lblDetails;
	private Button btnPreviousEvent;
	private Button btnNextEvent;
	private Button btnRestartSystemFromHere;
	
	private IDebugEvent currentEvent;
	
	private boolean isOpened;
	
	private final int insetX = 4;
	private final int insetY = 4;

	public EventDetailsShell(final Display display, final int style) {
		super(display, style | SWT.SHELL_TRIM);
		
		final FormLayout layout = new FormLayout();
		layout.marginWidth = insetX;
		layout.marginHeight = insetY;
		setLayout(layout);
		
		createContents();
	}
	
	public void setEvent(final IDebugEvent event) {
		currentEvent = event;
		
		setEventName(event.getName());
		// addEventHandler(EventHolder.getHandlerBy(event.getId()));
	}
	
	public IDebugEvent getCurrentEvent() {
		return currentEvent;
	}

	public void setEventName(final String name) {
		lblEventName.setText(name);
	}
	
	public void addEventTracePoint(final IDebugEvent event) {
		final TableItem item = new TableItem(tblTrace, SWT.NONE);
		item.setText(new String[] { event.getName(), Double.toString(event.getTimeInformation().getTime()) });
	}
	
	public void addEventHandler(final Collection<IDebugEventHandler> handlers) {
		for (final IDebugEventHandler handler : handlers) {
			Preconditions.checkArgument(handler.ofEvent().equals(currentEvent.getId()),
					"The handler must be from the event");

			final TableItem item = new TableItem(tblEventHandlers, SWT.NONE);
			item.setText(new String[] { handler.getName(), handler.getStatus().toString() });
		}
	}
	
	

	/* *******************************************  */
	/* 			Widget and Form creation 			*/
	/* ******************************************** */
	
	@Override
	public void close() {
		isOpened = false;
		super.close();
	}

	@Override
	public void open() {
		isOpened = true;
		super.open();
	}
	
	public boolean isOpened() {
		return isOpened;
	}

	private void createContents() {
		this.createLblEventDetails();
		this.createLblEventName();
		this.createBtnPreviousEvent();
		this.createBtnNextEvent();
		this.createBtnRestartSystemFromHere();
		this.createTabFolder();
		this.createTbiInformation();
		this.createTbiEventTrace();
		this.createTbiEventHandlers();
		this.createTblTrace();
		cmpEventTrace.addControlListener(getTableResizer(tblTrace, List.of(eventNameColumn, eventTimeColumn)));
		this.createLblDetails();
	}
	
	private void createTbiEventTrace() {
		tbiEventTrace = new TabItem(tbfInformation, SWT.NONE);
		tbiEventTrace.setText(TRACE_TEXT);
		
		cmpEventTrace = new Composite(tbfInformation, SWT.NONE);
		cmpEventTrace.setLayout(new FillLayout());
		
		tbiEventTrace.setControl(cmpEventTrace);
	}
	
	private void createTbiEventHandlers() {
		tbiEventHandlers = new TabItem(tbfInformation, SWT.NONE);
		tbiEventHandlers.setText(EVENT_HANDLERS_TEXT);

		cmpEventHandlers = new Composite(tbfInformation, SWT.NONE);
		cmpEventHandlers.setLayout(new FillLayout());

		tbiEventHandlers.setControl(cmpEventHandlers);

		createTblEventHandlers();

		cmpEventHandlers.addControlListener(getTableResizer(tblEventHandlers, List.of(handlerName, handlerStatus)));
	}

	private void createTblEventHandlers() {
		tblEventHandlers = new Table(cmpEventHandlers, SWT.V_SCROLL | SWT.BORDER);
		tblEventHandlers.setHeaderVisible(true);
		tblEventHandlers.setLinesVisible(true);

		handlerName = new TableColumn(tblEventHandlers, SWT.NONE);
		handlerName.setText("Handler name");
		handlerName.setWidth(400);

		handlerStatus = new TableColumn(tblEventHandlers, SWT.NONE);
		handlerStatus.setText("Status");
		handlerStatus.setWidth(200);
	}

	private void createLblEventDetails() {
		lblEventDetails = new Label(this, SWT.NONE);
		lblEventDetails.setText(EVENT_DETAILS_TEXT);
		
		UiUtils.formLayout()
			   .setTop(0, 0)
			   .setLeft(0, 0)
			   .attach(lblEventDetails);
	}
	
	private void createTblTrace() {
		tblTrace = new Table(cmpEventTrace, SWT.V_SCROLL | SWT.BORDER);
		tblTrace.setHeaderVisible(true);
		tblTrace.setLinesVisible(true);
		
		eventNameColumn = new TableColumn(tblTrace, SWT.NONE);
		eventNameColumn.setText("Event");
		eventNameColumn.setWidth(400);
		
		eventTimeColumn = new TableColumn(tblTrace, SWT.NONE);
		eventTimeColumn.setText("Time");
		eventNameColumn.setWidth(100);
		
		
	}
	
	private void createLblEventName() {
		lblEventName = new Label(this, SWT.NONE);
		UiUtils.setFontSize(lblEventName, 16);
		
		UiUtils.formLayout()
			   .setTop(lblEventDetails)
			   .setLeft(0, 0)
			   .attach(lblEventName);
	}
	
	private void createTabFolder() {
		tbfInformation = new TabFolder(this, SWT.NONE);
		
		UiUtils.formLayout()
			   .setTop(lblEventName, insetY)
			   .setLeft(0, 0)
			   .setRight(100, 0)
			   .setBottom(btnNextEvent, -170)
			   .attach(tbfInformation);
	}
	
	private void createTbiInformation() {
		tbiEventInformation = new TabItem(tbfInformation, SWT.NONE);
		tbiEventInformation.setText(INFORMATION_TEXT);
		final Composite comp = new Composite(tbfInformation, SWT.NONE);
		comp.setLayout(new FillLayout());
		// comp.addControlListener(tableResizer);

		tbiEventInformation.setControl(comp);

		final Label exampleLabel = new Label(comp, SWT.NONE);
		exampleLabel.setText("Here, future information about the event will be placed");
	}
	
	private void createLblDetails() {
		lblDetails = new Label(this, SWT.NONE);
		lblDetails.setText("Currently, no details available");
		
		final Point size = lblDetails.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		final FormData data = UiUtils.formLayout(size.x, SWT.DEFAULT)
									   .setLeft(0, 0)
									   .setRight(100, 0)
									   .setTop(tbfInformation, insetY)
									   .attach(lblDetails);
		addListener(SWT.RESIZE, e -> {
			final Rectangle rect = getClientArea();
			data.width = rect.width - insetX * 2;
			layout();
		});
	}
	
	private void createBtnPreviousEvent() {
		btnPreviousEvent = new Button(this, SWT.PUSH);
		btnPreviousEvent.setText(PREVIOUS_EVENT_TEXT);
		
		UiUtils.formLayout()
			   .setLeft(0, 0)
				.setBottom(100, 0)
			   .attach(btnPreviousEvent);
	}
	
	private void createBtnNextEvent() {
		btnNextEvent = new Button(this, SWT.PUSH);
		btnNextEvent.setText(NEXT_EVENT_TEXT);
		
		UiUtils.formLayout()
				.setRight(100, 0).setBottom(100, 0)
			   .attach(btnNextEvent);
	}
	
	private void createBtnRestartSystemFromHere() {
		btnRestartSystemFromHere = new Button(this, SWT.PUSH);
		btnRestartSystemFromHere.setText(RESTART_TEXT);
		btnRestartSystemFromHere.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				EventDebugSystem.startFromHere(currentEvent);
			}

		});
		
		UiUtils.formLayout()
			   .setRight(btnNextEvent)
				.setBottom(100, 0)
			   .attach(btnRestartSystemFromHere);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	/* ******************************************** */
	/* 		        	Listeners		 			*/
	/* ******************************************** */
	private final ControlListener getTableResizer(final Table tbl, final List<TableColumn> columns) {
		return ControlListener.controlResizedAdapter(e -> {
			final Rectangle area = cmpEventTrace.getClientArea();
			final Point preferredSize = tbl.computeSize(SWT.DEFAULT, SWT.DEFAULT);

			int width = area.width - 2 * tbl.getBorderWidth();

			if (preferredSize.y > area.height + tbl.getHeaderHeight()) {
				final Point vBarSize = tbl.getVerticalBar().getSize();
				width -= vBarSize.x;
			}

			final Point oldSize = tbl.getSize();

			if (oldSize.x > area.width) {
				for (int i = 0; i < columns.size() - 1 && i < 3; ++i) {
					columns.get(i).setWidth(width / 3);
				}
				columns.get(columns.size() - 1).setWidth(width - eventNameColumn.getWidth());
				tbl.setSize(area.width, area.height);
			} else {
				tbl.setSize(area.width, area.height);
				for (int i = 0; i < columns.size() - 1 && i < 3; ++i) {
					columns.get(i).setWidth(width / 3);
				}
				columns.get(columns.size() - 1).setWidth(width - eventNameColumn.getWidth());
			}
		});
	}
	private Composite cmpEventHandlers;
}
