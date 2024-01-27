package org.palladiosimulator.addon.slingshot.debuggereventsystems.common;

import java.util.LinkedHashMap;
import java.util.Map;

public class FixedSizeMapCache<K, V> extends LinkedHashMap<K, V> {

	private final int maxEntries;

	public FixedSizeMapCache(final int maxEntries) {
		super(maxEntries);
		this.maxEntries = maxEntries;
	}

	@Override
	public boolean removeEldestEntry(final Map.Entry<K, V> eldes) {
		return size() > maxEntries;
	}

}
