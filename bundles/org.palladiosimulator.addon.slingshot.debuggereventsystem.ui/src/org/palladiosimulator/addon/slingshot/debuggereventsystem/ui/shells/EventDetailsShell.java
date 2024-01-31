package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.shells;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
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
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.view.EventView;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.cache.EventTreeNode;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEventHandler;

import com.google.common.base.Preconditions;

/**
 * A GUI shell for displaying detailed information about an event in a debug
 * session.
 * <p>
 * This shell provides a comprehensive view of the event within a debugging
 * session, focusing on displaying detailed information, trace data, and event
 * handler details across multiple tabs. It is designed to aid developers in
 * analyzing and understanding the flow and handling of events within their
 * application, offering insights into event details, the sequence of events
 * leading to the current state, and the handlers involved in processing the
 * event.
 * </p>
 * Normally, this shell is opened when double clicking on an event in the
 * {@link EventView}.
 * 
 * @author Julijan Katic
 */
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
	private Composite cmpEventInformation;
	
	private TableViewer tblTrace;
	
	private Table tblEventHandlers;
	private TableColumn handlerName;
	private TableColumn handlerStatus;

	private TableViewer tblInformation;

	private Label lblDetails;
	private Button btnPreviousEvent;
	private Button btnNextEvent;
	private Button btnRestartSystemFromHere;
	
	private IDebugEvent currentEvent;
	private Map<String, Object> information;
	
	private boolean isOpened;
	
	private final int insetX = 4;
	private final int insetY = 4;

	public EventDetailsShell(final Display display, final int style) {
		super(display, style | SWT.SHELL_TRIM);
		
		final FormLayout layout = new FormLayout();
		layout.marginWidth = insetX;
		layout.marginHeight = insetY;
		setLayout(layout);
		setText("Event Details");
		
		createContents();
	}
	
	public void setEvent(final IDebugEvent event) {
		currentEvent = event;

		setText("Event Details: " + event.getName());
		
		setEventName(event.getName());
		information = new HashMap<>();

		information.put("ID", event.getId());
		information.put("Event Time", event.getTimeInformation().getTime());
		information.put("Event Type", event.getEventType());

		if (event.getMetaInformation() != null) {
			event.getMetaInformation().forEach((key, value) -> {
				if (!key.startsWith(".")) {
					information.put(key, value);
				}
			});
		}

		tblInformation.setInput(information.entrySet());
		tblInformation.refresh();

		final List<ParentNode> parents = getParents();
		tblTrace.setInput(parents);
		tblTrace.refresh();
		// addEventHandler(EventHolder.getHandlerBy(event.getId()));
	}
	
	public IDebugEvent getCurrentEvent() {
		return currentEvent;
	}

	public void setEventName(final String name) {
		lblEventName.setText(name);
	}
	
	public void addEventHandler(final Collection<IDebugEventHandler> handlers) {
		for (final IDebugEventHandler handler : handlers) {
			Preconditions.checkArgument(handler.ofEvent().equals(currentEvent.getId()),
					"The handler " + handler.getId() + " must have the same event " + currentEvent.getId() + ", but is "
							+ handler.ofEvent());

			final TableItem item = new TableItem(tblEventHandlers, SWT.NONE);
			item.setText(new String[] { handler.getName(), handler.getStatus().toString() });
		}
	}
	
	private List<ParentNode> getParents() {
		final List<ParentNode> result = new ArrayList<>(10);
		final List<EventTreeNode> tree = EventDebugSystem.getEventTree().getLatestParents(currentEvent.getId(), 10);
		for (final EventTreeNode treeNode : tree) {
			final Optional<IDebugEvent> debuggedEvent = EventDebugSystem.getEventHolder().getCachedEvent(treeNode.debuggedEvent());
			final Optional<IDebugEventHandler> handler = EventDebugSystem.getEventHolder()
					.getCachedHandler(treeNode.handler());
			
			if (debuggedEvent.isPresent() && handler.isPresent()) {
				result.add(new ParentNode(debuggedEvent.get(), handler.get()));
			}
		}

		return result;
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
		this.createTblInformation();
		this.createTbiEventTrace();
		this.createTbiEventHandlers();
		this.createTblTrace();
		// cmpEventTrace.addControlListener(getTableResizer(tblTrace,
		// List.of(eventNameColumn, eventTimeColumn)));
		this.createLblDetails();
	}
	
	private void createTblInformation() {
		tblInformation = UiUtils.createTableViewer(cmpEventInformation, List.of(
				new UiUtils.ColumnConverter("Key", 500,
						entry -> ((Map.Entry<String, Object>) entry).getKey()),
				new UiUtils.ColumnConverter("Value", 500,
						entry -> ((Map.Entry<String, Object>) entry).getValue().toString())),
				null);


		tblInformation.setContentProvider(ArrayContentProvider.getInstance());

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
		tblTrace = UiUtils.createTableViewer(cmpEventTrace,
				List.of(
						new UiUtils.ColumnConverter("Name", 600, e -> ((ParentNode) e).event().getName()),
						new UiUtils.ColumnConverter("From", 600, e -> ((ParentNode) e).byHandler().getName())),
				null);
		tblTrace.setContentProvider(ArrayContentProvider.getInstance());
	}
	
	private static record ParentNode(IDebugEvent event, IDebugEventHandler byHandler) {
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
		cmpEventInformation = new Composite(tbfInformation, SWT.NONE);
		cmpEventInformation.setLayout(new FillLayout());
		// comp.addControlListener(tableResizer);

		tbiEventInformation.setControl(cmpEventInformation);

	}
	
	private void createLblDetails() {
		lblDetails = new Label(this, SWT.NONE);
		lblDetails.setText("");
		
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
				.setBottom(100, 500)
			   .attach(btnPreviousEvent);
	}
	
	private void createBtnNextEvent() {
		btnNextEvent = new Button(this, SWT.PUSH);
		btnNextEvent.setText(NEXT_EVENT_TEXT);
		
		UiUtils.formLayout()
				.setRight(100, 0).setBottom(100, 500)
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
				.setBottom(100, 500)
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
			int sum = 0;
			if (oldSize.x > area.width) {
				for (int i = 0; i < columns.size() - 1; ++i) {
					columns.get(i).setWidth(width / columns.size());
					sum += columns.get(i).getWidth();
				}
				columns.get(columns.size() - 1).setWidth(width - sum);
				tbl.setSize(area.width, area.height);
			} else {
				tbl.setSize(area.width, area.height);
				for (int i = 0; i < columns.size() - 1; ++i) {
					columns.get(i).setWidth(width / columns.size());
					sum += columns.get(i).getWidth();
				}
				columns.get(columns.size() - 1).setWidth(width - sum);
			}
		});
	}
	private Composite cmpEventHandlers;
}
