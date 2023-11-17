package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.DataField;

import java.lang.reflect.Field;
import java.util.Objects;

public class AnnotatedDataField extends AnnotatedField<DataField> implements GroupedData, LabeledData {
	private final String group, label;

	public AnnotatedDataField(@NotNull Object parentInstance, @NotNull Field field, @NotNull String parentGroup) {
		super(parentInstance, field, DataField.class);

		String group = getAnnotation().group();
		if (Objects.equals(group, "")) group = parentGroup;
		if (Objects.equals(group, "")) group = parentInstance.getClass().getSimpleName();
		this.group = group;

		String label = getAnnotation().label();
		if (Objects.equals(label, "")) label = getAccessibleObject().getName();
		this.label = label;
	}

	@NotNull
	@Override
	public DataField getAnnotation() {
		return (DataField) super.getAnnotation();
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
}
