package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.EventConsumer;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.SystemClearUpListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.DebugEventPublished;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.SystemClearedUp;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

/**
 * Represents a view within the Eclipse IDE that displays events captured during
 * the debugging of an application.
 * <p>
 * This view part is designed to integrate with an Eclipse RCP application,
 * providing users with a real-time visualization of debug events as they occur
 * in the target application. It supports interactions such as double-clicking
 * on an event to see more details, facilitated by registering event consumers
 * and system clear-up listeners to handle the dynamic nature of debugging
 * sessions.
 * </p>
 * 
 * To instantiate this class, use the {@link EventViewFactory} class.
 *
 * @author Julijan Katic
 */
public class EventView extends ViewPart {

	public static final String TITLE = "Debugged Events";

	private TableViewer tableViewer;
	private final List<IDebugEvent> debuggedEvents = new ArrayList<>(1000);
	
	// private EventDetailsShell detailsShell;
	private Composite parent;
	
	EventView() {
		setPartName(TITLE);

		EventDebugSystem.addClearUpListener(new OnSystemClearUpCalled());
		EventDebugSystem.addConsumer(new OnDebugEventPublished());
	}

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent; // new Composite(parent, SWT.NONE);
		parent.setLayout(new FillLayout());
		
		tableViewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent);

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(debuggedEvents);
		tableViewer.addDoubleClickListener(event -> {
			final IStructuredSelection selection = tableViewer.getStructuredSelection();
			final Object selectedItem = selection.getFirstElement();
			if (selectedItem instanceof final IDebugEvent debugEvent) {
				EventDebugSystem.showRuntimeEventInformation(debugEvent);
			}
		});
	}

	private void createColumns(final Composite parent) {
		final List<ColumnConverter> columns = List.of(
				new ColumnConverter("Event", 400, event -> event.getName()),
				new ColumnConverter("Time", 100, event -> Double.toString(event.getTimeInformation().getTime())),
				new ColumnConverter("Type", 100, event -> event.getEventType())
		);

		columns.forEach(this::createTableViewerColumn);
	}

	private TableViewerColumn createTableViewerColumn(final ColumnConverter converter) {
		final TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.NONE);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof final IDebugEvent ev) {
					return converter.converter().apply(ev);
				}
				return super.getText(element);
			}
		});

		final TableColumn column = col.getColumn();
		column.setText(converter.columnName());
		column.setWidth(converter.columnWidth());
		column.setResizable(true);
		column.setMoveable(true);
		return col;
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	/**
	 * Defines a column converter for a table viewer column.
	 * <p>
	 * Encapsulates the logic for converting a {@link IDebugEvent} to a string for
	 * display in a table viewer column. Includes the column name, width, and a
	 * function that defines how to convert the event to a string value.
	 * </p>
	 */
	private static record ColumnConverter(String columnName, int columnWidth, Function<IDebugEvent, String> converter) {
	}


	/**
	 * Handles the addition of debug events to the view when they are published.
	 * <p>
	 * This class listens for {@link DebugEventPublished} events and updates the
	 * view asynchronously to display new debug events as they occur. It ensures
	 * that the view remains responsive and reflects the current state of the
	 * debugging session.
	 * </p>
	 */
	public class OnDebugEventPublished implements EventConsumer {
		@Override
		public void onEvent(final DebugEventPublished event) {
			Display.getDefault().asyncExec(() -> {
				if (tableViewer != null) {
					debuggedEvents.add(event.getDebuggedEvent());
					tableViewer.setInput(debuggedEvents);
					tableViewer.refresh();
				}
			});
			EventDebugSystem.getEventHolder().addEvent(event.getDebuggedEvent());
			EventDebugSystem.getEventTree().addNode(event.getDebuggedEvent());
		}
	}

	/**
	 * Clears the view when the debugging session is finished or reset.
	 * <p>
	 * This class listens for {@link SystemClearedUp} events and clears the list of
	 * displayed debug events, ensuring that the view is ready for a new debugging
	 * session or remains clean when not in use.
	 * </p>
	 */
	public class OnSystemClearUpCalled implements SystemClearUpListener {

		@Override
		public void onEvent(final SystemClearedUp listenerEvent) {
			Display.getDefault().asyncExec(() -> {
				debuggedEvents.clear();
				tableViewer.refresh();
			});
		}

	}
} 
