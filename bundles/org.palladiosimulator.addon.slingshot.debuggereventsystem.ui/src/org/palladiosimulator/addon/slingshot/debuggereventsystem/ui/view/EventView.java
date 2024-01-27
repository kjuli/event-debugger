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
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.SystemClearUpListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.consumer.EventConsumer;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.SystemClearedUp;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public class EventView extends ViewPart implements EventConsumer, SystemClearUpListener {

	public static final String TITLE = "Debugged Events";

	private TableViewer tableViewer;
	private final List<IDebugEvent> debuggedEvents = new ArrayList<>(1000);
	
	// private EventDetailsShell detailsShell;
	private Composite parent;
	
	EventView() {
		setPartName(TITLE);
	}

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent; // new Composite(parent, SWT.NONE);
		parent.setLayout(new FillLayout());
		System.out.println("My instance is " + this);
		
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

			// Check if there is a selection
			if (selectedItem instanceof final IDebugEvent debugEvent) {
				// Handle the double-click event
				// For example, open a dialog, display details, etc.
				System.out.println("Double-clicked: " + debugEvent.toString());
				EventDebugSystem.showRuntimeEventInformation(debugEvent);
			}
		});
	}

	private void createColumns(final Composite parent) {
		final List<ColumnConverter> columns = List.of(
				new ColumnConverter("Event", 400, event -> event.getName()),
				new ColumnConverter("Time", 100, event -> Double.toString(event.getTimeInformation().getTime()))
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

	@Override
	public void consumeEvent(final IDebugEvent event) {
		Display.getDefault().asyncExec(() -> {
			if (tableViewer != null) {
				debuggedEvents.add(event);
				tableViewer.setInput(debuggedEvents);
				tableViewer.refresh();
			}
		});
		EventHolder.addEvent(event);
	}

	private static record ColumnConverter(String columnName, int columnWidth, Function<IDebugEvent, String> converter) {
	}

	@Override
	public void onEvent(final SystemClearedUp listenerEvent) {
		Display.getDefault().asyncExec(() -> {
			debuggedEvents.clear();
			tableViewer.refresh();
		});
		EventHolder.clear();
	}

} 
