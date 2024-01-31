package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

public class UiUtils {
	
	/**
	 * Sets the font size for an SWT control.
	 * <p>
	 * This utility method allows for changing the font size of any SWT control. It
	 * creates a new font based on the control's current font but with the specified
	 * size, sets this new font on the control, and ensures the font is properly
	 * disposed of when the control is disposed to avoid resource leaks.
	 * </p>
	 * 
	 * @param control  The control whose font size is to be set.
	 * @param fontSize The new font size.
	 */
	public static void setFontSize(final Control control, final int fontSize) {
		final FontData[] fontData = control.getFont().getFontData();
		for (final FontData fd : fontData) {
			fd.setHeight(fontSize);
		}
		
		final Font newFont = new Font(control.getDisplay(), fontData);
		control.setFont(newFont);
		
		control.addDisposeListener(e -> newFont.dispose());
	}
	
	/**
	 * Executes a long-running operation with a progress monitor.
	 * <p>
	 * This method runs an {@link IRunnableWithProgress} with the ability to display
	 * a progress monitor and optionally allow the user to cancel the operation. It
	 * can run the operation on a separate thread and handle any exceptions that
	 * occur.
	 * </p>
	 * 
	 * @param separateThread Whether the operation should be run in a separate
	 *                       thread.
	 * @param closable       Whether the progress monitor allows closing (cancelling
	 *                       the operation).
	 * @param runnable       The long-running operation to execute.
	 */
	public static void runProgressMonitor(final boolean separateThread, final boolean closable, final IRunnableWithProgress runnable) {
		final IRunnableContext context = PlatformUI.getWorkbench().getProgressService();
		try {
			context.run(separateThread, closable, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			throw new RuntimeException("The operation could not finish.", e);
		}
	}
	
	/**
	 * Creates a {@link TableViewer} with specified columns in an SWT application.
	 * <p>
	 * This method simplifies the creation of a {@link TableViewer} by setting up
	 * columns based on provided {@link ColumnConverter}s. It also allows adding the
	 * created {@link TableViewerColumn}s to a collection for further manipulation.
	 * </p>
	 * 
	 * @param parent        The parent composite for the table viewer.
	 * @param columns       A list of column converters to define the columns.
	 * @param columnViewers A collection to which created table viewer columns are
	 *                      added (optional).
	 * @return The created {@link TableViewer}.
	 */
	public static TableViewer createTableViewer(final Composite parent,
			final List<ColumnConverter> columns,
			final Collection<? super TableViewerColumn> columnViewers) {
		final TableViewer tableViewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		columns.forEach(converter -> {
			final TableViewerColumn columnViewer = createTableViewerColumn(tableViewer, converter);
			if (columnViewers != null) {
				columnViewers.add(columnViewer);
			}
		});

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		return tableViewer;
	}

	/**
	 * Creates a single column for a {@link TableViewer}.
	 * <p>
	 * Utilizes a {@link ColumnConverter} to define the properties of the column,
	 * such as its name, width, and how its labels are provided. The column is made
	 * resizable and movable.
	 * </p>
	 * 
	 * @param tableViewer The table viewer to which the column will be added.
	 * @param converter   The converter that defines the column's properties.
	 * @return The created {@link TableViewerColumn}.
	 */
	public static TableViewerColumn createTableViewerColumn(final TableViewer tableViewer,
			final ColumnConverter converter) {
		final TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.NONE);
		col.setLabelProvider(converter.converter());

		final TableColumn column = col.getColumn();
		column.setText(converter.columnName());
		column.setWidth(converter.columnWidth());
		column.setResizable(true);
		column.setMoveable(true);
		return col;
	}

	

	/**
	 * Represents a converter for table viewer columns, defining the column name,
	 * width, and label provider.
	 * <p>
	 * This record simplifies the creation of table columns for a
	 * {@link TableViewer} by encapsulating the column name, width, and a function
	 * to provide the text for each cell in the column. It allows for easy
	 * customization of table columns.
	 * </p>
	 */
	public static record ColumnConverter(String columnName, int columnWidth, ColumnLabelProvider converter) {

		public ColumnConverter(final String columnName, final int columnWidth,
				final Function<Object, String> converter) {
			this(columnName, columnWidth, new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final String res = converter.apply(element);
					if (res != null) {
						return res;
					}
					return super.getText(element);
				}
			});
		}

	}

	public static FormDataBuilder formLayout() {
		return new FormDataBuilder();
	}

	public static FormDataBuilder formLayout(final int width, final int height) {
		return new FormDataBuilder(width, height);
	}

	public static final class FormDataBuilder {
		
		private final FormData formData;

		private FormDataBuilder() {
			formData = new FormData();
		}
		
		private FormDataBuilder(final int width, final int height) {
			formData = new FormData(width, height);
		}
		
		public FormDataBuilder setTop(final FormAttachment formAttachment) {
			formData.top = formAttachment;
			return this;
		}
		
		public FormDataBuilder setTop(final int numerator) {
			return setTop(new FormAttachment(numerator));
		}
		
		public FormDataBuilder setTop(final int numerator, final int offset) {
			return setTop(new FormAttachment(numerator, offset));
		}
		
		public FormDataBuilder setTop(final int numerator, final int denominator, final int offset) {
			return setTop(new FormAttachment(numerator, denominator, offset));
		}
		
		public FormDataBuilder setTop(final Control control) {
			return setTop(new FormAttachment(control));
		}
		
		public FormDataBuilder setTop(final Control control, final int numerator) {
			return setTop(new FormAttachment(control, numerator));
		}
		
		public FormDataBuilder setTop(final Control control, final int numerator, final int alignment) {
			return setTop(new FormAttachment(control, numerator, alignment));
		}
		
		public FormDataBuilder setLeft(final FormAttachment formAttachment) {
			formData.left = formAttachment;
			return this;
		}
		
		public FormDataBuilder setLeft(final int numerator) {
			return setLeft(new FormAttachment(numerator));
		}
		
		public FormDataBuilder setLeft(final int numerator, final int offset) {
			return setLeft(new FormAttachment(numerator, offset));
		}
		
		public FormDataBuilder setLeft(final int numerator, final int denominator, final int offset) {
			return setLeft(new FormAttachment(numerator, denominator, offset));
		}
		
		public FormDataBuilder setLeft(final Control control) {
			return setLeft(new FormAttachment(control));
		}
		
		public FormDataBuilder setLeft(final Control control, final int numerator) {
			return setLeft(new FormAttachment(control, numerator));
		}
		
		public FormDataBuilder setLeft(final Control control, final int numerator, final int alignment) {
			return setLeft(new FormAttachment(control, numerator, alignment));
		}
		
		public FormDataBuilder setRight(final FormAttachment formAttachment) {
			formData.right = formAttachment;
			return this;
		}
		
		public FormDataBuilder setRight(final int numerator) {
			return setRight(new FormAttachment(numerator));
		}
		
		public FormDataBuilder setRight(final int numerator, final int offset) {
			return setRight(new FormAttachment(numerator, offset));
		}
		
		public FormDataBuilder setRight(final int numerator, final int denominator, final int offset) {
			return setRight(new FormAttachment(numerator, denominator, offset));
		}
		
		public FormDataBuilder setRight(final Control control) {
			return setRight(new FormAttachment(control));
		}
		
		public FormDataBuilder setRight(final Control control, final int numerator) {
			return setRight(new FormAttachment(control, numerator));
		}
		
		public FormDataBuilder setRight(final Control control, final int numerator, final int alignment) {
			return setRight(new FormAttachment(control, numerator, alignment));
		}
		
		public FormDataBuilder setBottom(final FormAttachment formAttachment) {
			formData.bottom = formAttachment;
			return this;
		}
		
		public FormDataBuilder setBottom(final int numerator) {
			return setBottom(new FormAttachment(numerator));
		}
		
		public FormDataBuilder setBottom(final int numerator, final int offset) {
			return setBottom(new FormAttachment(numerator, offset));
		}
		
		public FormDataBuilder setBottom(final int numerator, final int denominator, final int offset) {
			return setBottom(new FormAttachment(numerator, denominator, offset));
		}
		
		public FormDataBuilder setBottom(final Control control) {
			return setBottom(new FormAttachment(control));
		}
		
		public FormDataBuilder setBottom(final Control control, final int numerator) {
			return setBottom(new FormAttachment(control, numerator));
		}
		
		public FormDataBuilder setBottom(final Control control, final int numerator, final int alignment) {
			return setBottom(new FormAttachment(control, numerator, alignment));
		}
		
		public FormDataBuilder setHeight(final int height) {
			formData.height = height;
			return this;
		}
		
		public FormDataBuilder setWidth(final int width) {
			formData.width = width;
			return this;
		}
		
		public FormData attach(final Control control) {
			control.setLayoutData(formData);
			return formData;
		}
	}
}
