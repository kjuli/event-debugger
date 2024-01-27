package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.utils;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

public class UiUtils {
	
	public static void setFontSize(final Control control, final int fontSize) {
		final FontData[] fontData = control.getFont().getFontData();
		for (final FontData fd : fontData) {
			fd.setHeight(fontSize);
		}
		
		final Font newFont = new Font(control.getDisplay(), fontData);
		control.setFont(newFont);
		
		control.addDisposeListener(e -> newFont.dispose());
	}
	
	public static void runProgressMonitor(final boolean separateThread, final boolean closable, final IRunnableWithProgress runnable) {
		final IRunnableContext context = PlatformUI.getWorkbench().getProgressService();
		try {
			context.run(separateThread, closable, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			throw new RuntimeException("The operation could not finish.", e);
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
