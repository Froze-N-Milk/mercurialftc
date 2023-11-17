package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.OnCall;

import java.lang.reflect.Method;
import java.util.Objects;

public class AnnotatedOnCall extends AnnotatedMethod<OnCall> implements GroupedData, LabeledData {
	private final String group, label, contents;

	public AnnotatedOnCall(@NotNull Object parentInstance, @NotNull Method method, @NotNull String parentGroup) {
		super(parentInstance, method, OnCall.class);

		String group = getAnnotation().group();
		if (Objects.equals(group, "")) group = parentGroup;
		if (Objects.equals(group, "")) group = parentInstance.getClass().getSimpleName();
		this.group = group;

		String label = getAnnotation().label();
		if (Objects.equals(label, "")) label = getAccessibleObject().getName();
		this.label = label;

		String contents = getAnnotation().contents();
		if (Objects.equals(contents, "")) contents = "was invoked";
		this.contents = contents;
	}

	@NotNull
	@Override
	public OnCall getAnnotation() {
		return (OnCall) super.getAnnotation();
	}

	@NotNull
	@Override
	public String group() {
		return group;
	}

	@NotNull
	@Override
	public String label() {
		return label;
	}

	public String contents() {
		return contents;
	}
}
