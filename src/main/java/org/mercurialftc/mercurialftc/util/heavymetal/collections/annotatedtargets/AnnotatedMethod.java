package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AnnotatedMethod<A extends Annotation> extends AnnotatedTarget<Method, A> {
	public AnnotatedMethod(@NotNull Object parentInstance, @NotNull Method method, Class<? extends A> annotation) {
		super(parentInstance, method, annotation);
	}

	@NotNull
	@Override
	public Method getAccessibleObject() {
		return (Method) super.getAccessibleObject();
	}
}
