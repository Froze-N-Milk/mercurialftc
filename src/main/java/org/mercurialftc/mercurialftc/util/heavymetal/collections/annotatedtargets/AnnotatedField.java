package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.Traced;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;

public abstract class AnnotatedField<A extends Annotation> extends AnnotatedTarget<Field, A> {
	// the child
	@NotNull
	private final Object childInstance;

	public AnnotatedField(@NotNull Object parentInstance, @NotNull Field field, Class<? extends A> annotation) {
		super(parentInstance, field, annotation);
		try {
			this.childInstance = Objects.requireNonNull(getAccessibleObject().get(getParentInstance()));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	@Override
	public Field getAccessibleObject() {
		return (Field) super.getAccessibleObject();
	}

	@NotNull
	public Object getChildInstance() {
		return childInstance;
	}
}
