package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.Flatpack;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.Flatten;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class FlatpackedField extends TracedField {
	private final Flatpack flatpack;
	private final String parentGroup;

	public FlatpackedField(@NotNull Object parentInstance, @NotNull Field field, String parentGroup) {
		super(parentInstance, field);
		this.flatpack = Objects.requireNonNull(getAccessibleObject().getAnnotation(Flatpack.class));
		this.parentGroup = parentGroup;
	}

	@NotNull
	@Override
	public Stream<AnnotatedDataField> getDataFields() {
		return super.getDataFields()
				.filter(dataField -> (dataField.getAccessibleObject().isAnnotationPresent(Flatten.class) && flatpack.includeDefaults()) || Arrays.asList(flatpack.targets()).contains(dataField.getAccessibleObject().getName()))
				.map(dataField -> new FlattenedDataField(dataField.getParentInstance(), dataField.getAccessibleObject(), parentGroup));
	}

	@NotNull
	@Override
	public Stream<AnnotatedOnCall> getOnCalls() {
		return super.getOnCalls()
				.filter(onCall -> (onCall.getAccessibleObject().isAnnotationPresent(Flatten.class) && flatpack.includeDefaults()) || Arrays.asList(flatpack.targets()).contains(onCall.getAccessibleObject().getName()))
				.map(onCall -> new FlattenedOnCall(onCall.getParentInstance(), onCall.getAccessibleObject(), parentGroup));
	}
}
