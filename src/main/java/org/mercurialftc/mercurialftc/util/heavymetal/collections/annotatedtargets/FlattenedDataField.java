package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class FlattenedDataField extends AnnotatedDataField {
	private final String parentGroup;

	public FlattenedDataField(@NotNull Object parentInstance, @NotNull Field field, @NotNull String parentGroup) {
		super(parentInstance, field, parentGroup);
		this.parentGroup = parentGroup;
	}

	@NotNull
	@Override
	public String group() {
		return parentGroup;
	}
}
