package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.common.JDTHelper;

public class JavaLineBreakpointAdder implements IBreakpointListener {

	@Override
	public void breakpointAdded(final IBreakpoint breakpoint) {
		if (breakpoint instanceof final IJavaLineBreakpoint javaLineBp) {
			try {
				if (!javaLineBp.getMarker().getAttribute("isEventBp", false)) {
					final String typeName = javaLineBp.getMarker().getAttribute("org.eclipse.jdt.debug.core.typeName",
							null);
					final int lineNumber = javaLineBp.getMarker().getAttribute(IMarker.LINE_NUMBER, -1);


					if (typeName != null && lineNumber != -1) {
						final IJavaProject project = retrieveJavaProject(javaLineBp);
						final IType type = (project != null) ? findType(typeName, project) : findType(typeName);

						if (type != null) {
							final ICompilationUnit unit = type.getCompilationUnit();
							final CompilationUnit astRoot = JDTHelper.parseCompilationUnit(unit);

							final MethodHandlerCheckerVisitor visitor = new MethodHandlerCheckerVisitor(lineNumber,
									astRoot);
							astRoot.accept(visitor);

							if (visitor.isHandler) {
								javaLineBp.getMarker().setAttribute("isEventBp", true);

								// Optionally, refresh the breakpoint view to reflect the changes
								DebugPlugin.getDefault().getBreakpointManager().fireBreakpointChanged(javaLineBp);
							}
						}
					}
				}
			} catch (final CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void breakpointRemoved(final IBreakpoint breakpoint, final IMarkerDelta delta) {
		// Nothing needed
	}

	@Override
	public void breakpointChanged(final IBreakpoint breakpoint, final IMarkerDelta delta) {
		// Nothing needed
	}
	
	private IType findType(final String typeName, final IJavaProject project) {
		try {
			final IType result = project.findType(typeName);
			if (result != null && result.exists()) {
				return result;
			}
		} catch (final JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	private IType findType(final String typeName) {
		return JDTHelper.getAllJavaProjectsAsStream()
				.map(javaProject -> findType(typeName, javaProject))
				.filter(type -> type != null)
				.findAny()
				.orElse(null);
	}

	private IJavaProject retrieveJavaProject(final IBreakpoint breakpoint) {
		try {
			final IMarker marker = breakpoint.getMarker();
			if (marker == null) {
				return null;
			}

			final IResource resource = marker.getResource();
			if (resource == null) {
				return null;
			}

			final IJavaElement javaElement = JavaCore.create(resource);
			if (javaElement == null) {
				return null;
			}

			return javaElement.getJavaProject();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final class MethodHandlerCheckerVisitor extends JDTHelper.ASTVisitorWithCU {
		private final int lineNumber;
		private boolean isHandler = false;

		public MethodHandlerCheckerVisitor(final int lineNumber, final CompilationUnit astRoot) {
			this.lineNumber = lineNumber;
			cu = astRoot;
		}

		@Override
		public boolean visit(final MethodDeclaration node) {
			final int methodLineNumberStart = cu.getLineNumber(node.getStartPosition());
			final int methodLineNumberEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());

			if (methodLineNumberStart <= lineNumber && lineNumber <= methodLineNumberEnd) {
				isHandler = true;
			}

			return false;
		}
	}
}
