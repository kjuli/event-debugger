package org.palladiosimulator.addon.slingshot.debuggereventsystem.eclipse.model;

import org.eclipse.jdt.core.IType;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.EventBreakpoint;

public class EclipseEventBreakpoint implements EventBreakpoint {
	
	private final IType type;

	public EclipseEventBreakpoint(final IType type) {
		this.type = type;
	}

	@Override
	public String eventName() {
		return type.getFullyQualifiedName();
	}
	
	public IType getType() {
		return type;
	}
	
}
