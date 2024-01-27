package org.palladiosimulator.addon.slingshot.debuggereventsystems.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectSerializer {

	private static final Set<Class<?>> STRING_PRIMITIVE_CLASSES = Set.of(String.class, Integer.class, Float.class,
			Double.class, Long.class, Short.class, Byte.class, Boolean.class, Character.class, int.class, float.class,
			double.class, long.class, short.class, byte.class, boolean.class, char.class);

	public static Map<String, Object> serializeObject(final Object obj, final int depth) {
		if (depth < 0) {
			return Map.of("String-representation: ", obj.toString());
		}

		final Map<String, Object> result = new LinkedHashMap<>();
		final Map<String, Object> stateMap = new HashMap<>();
		for (final Field field : obj.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				final Object value = field.get(obj);

				if (isPrimitiveOrString(value)) {
					stateMap.put(field.getName(), value);
				} else if (value instanceof final Collection<?> col) {
					final List<Object> serializedCol = new ArrayList<>(col.size());
					col.stream().map(ob -> serializeObject(ob, depth - 1)).forEach(serializedCol::add);
					stateMap.put(field.getName(), serializedCol);
				} else if (value instanceof final Map<?, ?> map) {
					final Map<Object, Object> serializedMap = new HashMap<>();
					for (final Map.Entry<?, ?> entry : map.entrySet()) {
						serializedMap.put(getObjectName(entry.getKey()), getObjectName(entry.getValue()));
						result.put(getObjectName(entry.getKey()), serializeObject(entry.getKey(), depth - 1));
						result.put(getObjectName(entry.getValue()), serializeObject(entry.getValue(), depth - 1));
					}
					stateMap.put(field.getName(), serializedMap);
				} else {
					stateMap.put(field.getName(), getObjectName(value));
					result.put(getObjectName(value), serializeObject(obj, depth - 1));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		result.put(getObjectName(obj), stateMap);
		return result;
	}

	public static String getObjectName(final Object obj) {
		return obj.getClass().getName() + "@" + obj.hashCode();
	}

	public static boolean isPrimitiveOrString(final Object value) {
		return STRING_PRIMITIVE_CLASSES.contains(value.getClass());
	}
}
