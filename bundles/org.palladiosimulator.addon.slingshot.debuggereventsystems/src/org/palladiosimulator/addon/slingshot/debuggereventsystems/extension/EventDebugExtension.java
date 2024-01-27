package org.palladiosimulator.addon.slingshot.debuggereventsystems.extension;

import org.palladiosimulator.addon.slingshot.debuggereventsystems.model.EventType;

public record EventDebugExtension(String name,
								  String description,
								  ExtensionType extensionType,
								  EventType eventType
) {
	
}
