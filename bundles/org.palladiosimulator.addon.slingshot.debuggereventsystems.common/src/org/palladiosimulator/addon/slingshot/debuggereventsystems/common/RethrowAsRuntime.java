package org.palladiosimulator.addon.slingshot.debuggereventsystems.common;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RethrowAsRuntime<T extends Throwable> implements Consumer<T> {

	private final Supplier<String> getMessage;

	public RethrowAsRuntime(final Supplier<String> getMessage) {
		this.getMessage = getMessage;
	}

	@Override
	public void accept(final T t) {
		throw new RuntimeException(getMessage.get(), t);
	}

	public static <T extends Throwable> RethrowAsRuntime<T> from(final Supplier<String> getMessage) {
		return new RethrowAsRuntime<>(getMessage);
	}
}
