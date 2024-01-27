package org.palladiosimulator.addon.slingshot.debuggereventsystems.model;

import java.io.Serializable;

public sealed interface HandlerStatus extends Serializable {
	record Success() implements HandlerStatus {
	}

	record Started() implements HandlerStatus {
	}

	public record ExceptionThrown(Throwable throwable) implements HandlerStatus {
	}

	public record Error(String message) implements HandlerStatus {
	}

	public static HandlerStatus SUCCESS = new Success();
	public static HandlerStatus STARTED = new Started();
}