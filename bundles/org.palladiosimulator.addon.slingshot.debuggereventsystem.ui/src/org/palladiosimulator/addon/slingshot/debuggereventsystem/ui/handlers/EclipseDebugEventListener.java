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

public class EclipseDebugEventListener implements IDebugEventSetListener, StartEventFromHereListener {

	private final Map<IJavaThread, Integer> lastSizes = new HashMap<>();
	private final ActivateByPreference abp;

	public EclipseDebugEventListener(final ActivateByPreference abp) {
		this.abp = abp;
	}

	@Override
	public void onEvent(final StartSystemFromHereEvent listenerEvent) {
		// TODO: What if there are more than one threads?
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
				//System.out.println("The event object type after CREATE is: " + event.getSource().getClass().getName());
				System.out.println("Debug launch started: " + target.getLaunch().getLaunchConfiguration().getName());
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
		System.out.println("Step event about to be made for " + event.getSource().getClass().getName() + " with "
				+ event.getDetail());
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

					System.out.println(
							"Stack size was " + stackSize + " and now is " + javaThread.getStackFrames().length);

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
//		return Arrays.stream(javaThread.getBreakpoints())
//				.peek(breakpoint -> {
//					try {
//						System.out.println("Breakpoint " + breakpoint.getModelIdentifier() + " has attribute "
//								+ breakpoint.getMarker().getAttributes());
//					} catch (final CoreException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				})
//					 .anyMatch(breakpoint -> breakpoint.getMarker().getAttribute("isEventBp", false));
		for (final IBreakpoint breakpoint : javaThread.getBreakpoints()) {
			try {
				final Map<String, Object> attributes = breakpoint.getMarker().getAttributes();
				System.out.println("Breakpoint has following attributes: ");
				for (final Map.Entry<String, Object> attribute : attributes.entrySet()) {
					System.out.println("\t" + attribute.getKey() + ": " + attribute.getValue().toString());
				}
			} catch (final CoreException e) {
				e.printStackTrace();
			}

			if (breakpoint.getMarker().getAttribute("isEventBp", false)) {
				System.out.println("Found it!");
				return true;
			}
		}

		return false;
	}

}
