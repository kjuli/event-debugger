package org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.palladiosimulator.addon.slingshot.debuggereventsystem.ui.preferences.ActivateByPreference;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.StartEventFromHereListener;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.listener.events.StartSystemFromHereEvent;

/**
 * A listener for Eclipse debug events that integrates with a custom
 * event-debugger.
 * <p>
 * This class listens for debug events within the Eclipse IDE and interacts with
 * a custom event-debugger to facilitate advanced debugging scenarios. It
 * manages the lifecycle of the event-debugger alongside the standard Eclipse
 * debugger, ensuring that both are started and stopped synchronously.
 * Additionally, it implements logic to handle specific debugging actions such
 * as step-over or step-into, ensuring that execution resumes past middleware or
 * boilerplate code when an event-handler method is exited.
 * </p>
 * 
 * <p>
 * Key features include listening for the start and termination of the debugger,
 * managing breakpoints related to event handlers, and supporting a
 * {@link StartSystemFromHereEvent} which allows users to specify that execution
 * should continue from a particular event. This is needed here in order to
 * resume the paused thread (otherwise, the replay would not work).
 * </p>
 * 
 * 
 * @author Julijan Katic
 */
public class EclipseDebugEventListener implements IDebugEventSetListener, StartEventFromHereListener {

	private final Map<IJavaThread, Integer> lastSizes = new HashMap<>();
	private final ActivateByPreference abp;

	public EclipseDebugEventListener(final ActivateByPreference abp) {
		this.abp = abp;
	}

	@Override
	public void onEvent(final StartSystemFromHereEvent listenerEvent) {
		if (!lastSizes.isEmpty()) {
			final IJavaThread thread = lastSizes.entrySet().stream().findAny().get().getKey();
			try {
				thread.resume();
				lastSizes.remove(thread);
			} catch (final DebugException e) {
				throw new RuntimeException("Could not resume the suspention for restarting the system from event "
						+ listenerEvent.getEventId(), e);
			}
		}
	}

	@Override
	public void handleDebugEvents(final DebugEvent[] events) {
		for (final DebugEvent event : events) {
			if (event.getKind() == DebugEvent.CREATE && event.getSource() instanceof final JDIDebugTarget target) {
				abp.setActive(true);

				EventDebugSystem.clear();
				EventDebugSystem.listenToDebugEvents();
			} else if (event.getKind() == DebugEvent.SUSPEND) {
				handleBreakpointEvent(event);
			} else if (event.getKind() == DebugEvent.TERMINATE
					&& event.getSource() instanceof JDIDebugTarget) {
				abp.setActive(false);
				EventDebugSystem.close();
			}
		}
	}


	private void handleBreakpointEvent(final DebugEvent event) {
		if (event.getSource() instanceof final IJavaThread javaThread
				&& javaThread.isSuspended()) {
			try {
				if (event.getDetail() == DebugEvent.BREAKPOINT && hasEventBreakpoint(javaThread)) {
					lastSizes.put(javaThread, javaThread.getStackFrames().length);
				} else if (event.getDetail() == DebugEvent.STEP_END) {
					final int lastSize = lastSizes.getOrDefault(javaThread, -1);

					if (lastSize < 0) {
						return;
					}

					final int stackSize = javaThread.getStackFrames().length;

					if (stackSize < lastSize) {
						javaThread.resume();
						lastSizes.remove(javaThread);
					}

					lastSizes.put(javaThread, stackSize);
				}
			} catch (final DebugException e) {
				throw new RuntimeException("Could not stop the suspention.", e);
			}
		}

	}
	
	private boolean hasEventBreakpoint(final IJavaThread javaThread) {
		for (final IBreakpoint breakpoint : javaThread.getBreakpoints()) {
			try {
				final Map<String, Object> attributes = breakpoint.getMarker().getAttributes();
				for (final Map.Entry<String, Object> attribute : attributes.entrySet()) {
					System.out.println("\t" + attribute.getKey() + ": " + attribute.getValue().toString());
				}
			} catch (final CoreException e) {
				e.printStackTrace();
			}

			if (breakpoint.getMarker().getAttribute("isEventBp", false)) {
				return true;
			}
		}

		return false;
	}

}
