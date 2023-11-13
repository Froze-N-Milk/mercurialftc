import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.DataField;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.Traced;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.ArrayMap;

import java.lang.reflect.Field;
import java.util.*;

public class ReflectionTests {
	@Traced
	Child testChild;

	@Test
	void traceTest() throws IllegalAccessException {
		testChild = new Child();

		ArrayMap<Object, Field> results = tracedFields(this, this.getClass());

		for (ArrayMap.Entry<Object, Field> entry : results) {
			System.out.println(entry.getKey().toString() + ": " + entry.getValue().toString());
			entry.getValue().setAccessible(true);
			if (entry.getValue().isAnnotationPresent(DataField.class)) {
				System.out.println(entry.getValue().get(entry.getKey()));
			}
		}
	}

	@Test
	void reflectionTest() throws NoSuchFieldException, IllegalAccessException {
		Child child1 = new Child();

		Child child2 = new Child();

		Field one = child1.getClass().getDeclaredField("flag");
		Field two = child2.getClass().getDeclaredField("flag");

		one.setAccessible(true);
		two.setAccessible(true);

		one.setBoolean(child1, true);

		System.out.println(child1.flag);
		System.out.println(child2.flag);
	}

	/**
	 * recursively finds the fields which contain traces in the blood stream
	 *
	 * @param bloodstream
	 * @return
	 */
	@NotNull
	private ArrayMap<Object, Field> tracedFields(@NotNull Object bloodstream, @NotNull Class<?> target) throws IllegalAccessException {
		ArrayMap<Object, Field> traceMap = new ArrayMap<>();
		for (Field field : target.getDeclaredFields()) {
			if (field.isAnnotationPresent(Traced.class)) {
				field.setAccessible(true);
				traceMap.add(bloodstream, field);
			}
		}

		ArrayMap<Object, Field> rTraceMap = new ArrayMap<>();

		Class<?> parent = target.getSuperclass();
		if (parent != null && parent != Object.class) {
			rTraceMap.addAll(tracedFields(bloodstream, parent));
		}

		for (ArrayMap.Entry<Object, Field> entry : traceMap) {
			Object downstream = entry.getValue().get(entry.getKey());
			if (downstream == null) continue;
			rTraceMap.addAll(tracedFields(downstream, downstream.getClass()));
		}

		traceMap.addAll(rTraceMap);

		return traceMap;
	}

	static class Parent {
		@Traced
		@DataField
		private boolean flag2 = true;
	}

	static class Child extends Parent {
		@Traced
		@DataField
		private boolean flag = false;
	}
}
