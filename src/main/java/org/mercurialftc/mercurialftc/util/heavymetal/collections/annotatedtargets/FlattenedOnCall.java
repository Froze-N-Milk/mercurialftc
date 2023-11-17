package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class FlattenedOnCall extends AnnotatedOnCall {
	private final String parentGroup;

	public FlattenedOnCall(@NotNull Object parentInstance, @NotNull Method method, @NotNull String parentGroup) {
		super(parentInstance, method, parentGroup);
		this.parentGroup = parentGroup;
	}

	@NotNull
	@Override
	public String group() {
		return parentGroup;
	}
}