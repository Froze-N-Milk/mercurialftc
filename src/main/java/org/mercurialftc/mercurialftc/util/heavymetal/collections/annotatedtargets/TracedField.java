package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.DataField;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.OnCall;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.Traced;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class TracedField extends AnnotatedField<Traced> implements GroupedData {
	private final String group;

	public TracedField(@NotNull Object parentInstance, @NotNull Field field) {
		super(parentInstance, field, Traced.class);

		String group = getAnnotation().group();
		if (Objects.equals(group, "")) {
			group = getAccessibleObject().getName();
		}
		this.group = group;
	}

	@NotNull
	@Override
	public Traced getAnnotation() {
		return (Traced) super.getAnnotation();
	}

	@NotNull
	public Stream<AnnotatedDataField> getDataFields() {
		ArrayList<Field> fields = new ArrayList<>();
		Class<?> searchTargetClass = getChildInstance().getClass();
		while (searchTargetClass != null && searchTargetClass != Object.class) {
			fields.addAll(Arrays.asList(searchTargetClass.getDeclaredFields()));
			searchTargetClass = searchTargetClass.getSuperclass();
		}
		return fields.stream()
				.filter(f -> f.isAnnotationPresent(DataField.class))
				.peek(f -> f.setAccessible(true))
				.map(f -> new AnnotatedDataField(getChildInstance(), f, group()));
	}

	@NotNull
	public Stream<AnnotatedOnCall> getOnCalls() {
		ArrayList<Method> methods = new ArrayList<>();
		Class<?> searchTargetClass = getChildInstance().getClass();
		while (searchTargetClass != null && searchTargetClass != Object.class) {
			methods.addAll(Arrays.asList(searchTargetClass.getDeclaredMethods()));
			searchTargetClass = searchTargetClass.getSuperclass();
		}
		return methods.stream()
				.filter(m -> m.isAnnotationPresent(OnCall.class))
				.peek(m -> m.setAccessible(true))
				.map(m -> new AnnotatedOnCall(getChildInstance(), m, group()));
	}

	@NotNull
	public String group() {
		return group;
	}
}
