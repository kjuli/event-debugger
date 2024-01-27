package org.palladiosimulator.addon.slingshot.debuggereventsystem.eclipse.model;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.core.IType;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.EventBreakpoint;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.EventType;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.IDebugEvent;

public class EclipseEventBreakpoint implements EventBreakpoint {
	
	private final IType type;

	public EclipseEventBreakpoint(final IType type) {
		this.type = type;
	}

	@Override
	public String eventName() {
		return type.getFullyQualifiedName();
	}
	
	@Override
	public boolean isEvent(final IDebugEvent target) {
		if (EventType.CLASS_BASED.equals(target.getMetaInformation().get(IDebugEvent.TYPE_KEY))) {
			try {
				final Class<?> clazzType = Class.forName(type.getFullyQualifiedName());
				return target.getEvent().getClass().equals(clazzType);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Class couldn't be found, and thus the event could not be debugged", e);
			}
		}
		
		return false;
	}
	
	public IType getType() {
		return this.type;
	}
	
}
