package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.Objects;

@SuppressWarnings("unused")
public abstract class AnnotatedTarget<O extends AccessibleObject, A extends Annotation> {
	// holds the field
	@NotNull
	private final Object parentInstance;

	// exists on the object
	@NotNull
	private final AccessibleObject accessibleObject;

	// exists on the field
	@NotNull
	private final Annotation annotation;

	public AnnotatedTarget(@NotNull Object parentInstance, @NotNull AccessibleObject accessibleObject, Class<? extends A> annotation) {
		this.parentInstance = parentInstance;
		this.accessibleObject = accessibleObject;
		this.accessibleObject.setAccessible(true);
		this.annotation = Objects.requireNonNull(accessibleObject.getAnnotation(annotation));
	}

	@NotNull
	public Object getParentInstance() {
		return parentInstance;
	}


	@NotNull
	public AccessibleObject getAccessibleObject() {
		return accessibleObject;
	}

	@NotNull
	public Annotation getAnnotation() {
		return annotation;
	}
}
