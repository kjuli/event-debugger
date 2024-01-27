package org.palladiosimulator.addon.slingshot.debuggereventsystems.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectStateCacheManager {

	private final Map<String, Object> objectStateCache = new HashMap<>();
	private final Map<String, Map<String, Object>> globalStateCache = new HashMap<>();
	private final Map<String, Object> restoredObjects = new HashMap<>();

	public void cacheObjectState(final Object obj) {
		final String objectHash = getObjectIdentifier(obj);
		if (globalStateCache.containsKey(objectHash)) {
			return;
		}
		
		final Map<String, Object> stateMap = new HashMap<>();
		for (final Field field : obj.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				final Object value = field.get(obj);

				if (isPrimitiveOrString(value)) {
					stateMap.put(field.getName(), value);
				} else if (value instanceof final Collection<?> col) {
					final List<Object> serializedCol = new ArrayList<>(col.size());
					col.stream().map(this::serializeObject).forEach(serializedCol::add);
					stateMap.put(field.getName(), serializedCol);
				} else if (value instanceof final Map<?, ?> map) {
					final Map<Object, Object> serializedMap = new HashMap<>();
					for (final Map.Entry<?, ?> entry : map.entrySet()) {
						serializedMap.put(serializeObject(entry.getKey()), serializeObject(entry.getValue()));
					}
					stateMap.put(field.getName(), serializedMap);
				} else {
					stateMap.put(field.getName(), serializeObject(value));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		globalStateCache.put(objectHash, stateMap);
		objectStateCache.put(objectHash, obj);
	}

	public void restoreObjectState(final Object object) {
		final String objectHash = getObjectIdentifier(object);
		final Map<String, Object> stateMap = globalStateCache.get(objectHash);
		if (stateMap != null) {
			for (final Map.Entry<String, Object> entry : stateMap.entrySet()) {
				try {
					final Field field = object.getClass().getDeclaredField(entry.getKey());
					field.setAccessible(true);
					final Object value = entry.getValue();

					if (value instanceof final String strVal && strVal.startsWith("Object:")) {
						final String objref = strVal.substring(7);
//						final String[] parts = objref.split("@");
//						final int refHash = Integer.parseInt(parts[1]);
						final Object refObject = getObjectFromGlobalCache(objref);
						field.set(object, refObject);
					} else if (value instanceof final String strVal && strVal.startsWith("String:")) {
						field.set(object, strVal.substring(7));
					} else {
						field.set(object, value);
					}
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private Object getObjectFromGlobalCache(final String ref) {
		if (!restoredObjects.containsKey(ref)) {
			final Object val = objectStateCache.get(ref);
			restoreObjectState(val);
		}

		return restoredObjects.get(ref);
	}

	private boolean isPrimitiveOrString(final Object val) {
		return val instanceof String || val.getClass().isPrimitive();
	}

	private Object serializeObject(final Object obj) {
		if (!isPrimitiveOrString(obj)) {
			cacheObjectState(obj);
		}

		return getObjectIdentifier(obj);
	}

	private String getObjectIdentifier(final Object obj) {
		if (obj instanceof final String str) {
			return "String:" + obj;
		} else if (obj.getClass().isPrimitive()) {
			return obj.toString();
		} else {
			return "Object:" + obj.getClass().getName() + "@" + System.identityHashCode(obj);
		}
	}
}
