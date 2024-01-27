package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.core.dom.Message;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaBreakpointListener;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;

public class EclipseJDTBreakpointListener implements IJavaBreakpointListener {

	@Override
	public void addingBreakpoint(final IJavaDebugTarget target, final IJavaBreakpoint breakpoint) {

	}

	@Override
	public int installingBreakpoint(final IJavaDebugTarget target, final IJavaBreakpoint breakpoint, final IJavaType type) {

		return DONT_CARE;
	}

	@Override
	public void breakpointInstalled(final IJavaDebugTarget target, final IJavaBreakpoint breakpoint) {

	}

	@Override
	public int breakpointHit(final IJavaThread thread, final IJavaBreakpoint breakpoint) {
		System.out.println("A breakpoint is hit: " + getTypeName(breakpoint));
		return DONT_CARE;
	}

	@Override
	public void breakpointRemoved(final IJavaDebugTarget target, final IJavaBreakpoint breakpoint) {

	}

	@Override
	public void breakpointHasRuntimeException(final IJavaLineBreakpoint breakpoint, final DebugException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void breakpointHasCompilationErrors(final IJavaLineBreakpoint breakpoint, final Message[] errors) {
		// TODO Auto-generated method stub

	}

	private String getTypeName(final IJavaBreakpoint breakpoint) {
		try {
			return breakpoint.getTypeName() + " with identifier " + breakpoint.getModelIdentifier();
		} catch (final CoreException e) {
			throw new RuntimeException("Identifier could not be read.", e);
		}
	}
}
