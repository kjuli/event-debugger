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

/**
 * A listener for Java line breakpoints that identifies and marks event
 * breakpoints.
 * <p>
 * This class implements the {@link IBreakpointListener} interface to listen for
 * breakpoint additions in Java projects. When a new breakpoint is added, this
 * class checks if the breakpoint is within an event-handler method. If so, the
 * breakpoint is marked as a special breakpoint, known as an "event breakpoint".
 * This distinction is important for debugging event-driven applications, as it
 * allows developers to quickly identify breakpoints that are specifically set
 * on event-handler methods.
 * </p>
 * 
 * @author Julijan Katic
 */
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
	
	/**
	 * Finds a {@link IType} by its name within a specific {@link IJavaProject}.
	 * <p>
	 * This method searches for a type by its fully qualified name within the
	 * context of a given Java project. It returns the type if found; otherwise, it
	 * returns null. This method is crucial for resolving the type in which a new
	 * breakpoint has been set, facilitating the check whether the breakpoint is
	 * within an event-handler method.
	 * </p>
	 * 
	 * @param typeName The fully qualified name of the type to find.
	 * @param project  The Java project within which to search for the type.
	 * @return The {@link IType} found, or null if the type does not exist within
	 *         the given project.
	 */
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

	/**
	 * Finds a {@link IType} by its name across all Java projects in the workspace.
	 * <p>
	 * This method iterates over all Java projects in the workspace, attempting to
	 * find a type by its fully qualified name. This is useful when the project
	 * context of a breakpoint is not known or when the type could reside in any
	 * project. It leverages {@link #findType(String, IJavaProject)} for each
	 * project and returns the first match found.
	 * </p>
	 * 
	 * @param typeName The fully qualified name of the type to find.
	 * @return The {@link IType} found, or null if the type does not exist in any
	 *         project.
	 * 
	 * @see #findType(String, IJavaProject)
	 */
	private IType findType(final String typeName) {
		return JDTHelper.getAllJavaProjectsAsStream()
				.map(javaProject -> findType(typeName, javaProject))
				.filter(type -> type != null)
				.findAny()
				.orElse(null);
	}

	/**
	 * Retrieves the {@link IJavaProject} associated with a breakpoint.
	 * <p>
	 * This method extracts the Java project from a breakpoint's marker, which is
	 * necessary for identifying the project context of the breakpoint. This context
	 * is used to search for types within the correct scope and to decrease the time
	 * needed to find the correct type of the breakpoint.
	 * </p>
	 * 
	 * @param breakpoint The breakpoint for which to retrieve the associated Java
	 *                   project.
	 * @return The {@link IJavaProject} associated with the breakpoint, or null if
	 *         it cannot be determined.
	 */
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

	/**
	 * An AST visitor that checks if a given line number falls within a method
	 * declaration, used to identify event handlers.
	 * <p>
	 * This static inner class extends {@link JDTHelper.ASTVisitorWithCU} to visit
	 * method declarations in a Java AST. It checks if the specified line number
	 * falls within any method's line range. If so, it marks the visitation state as
	 * having encountered an event handler. This is part of the mechanism to detect
	 * and mark event breakpoints.
	 * </p>
	 */
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
