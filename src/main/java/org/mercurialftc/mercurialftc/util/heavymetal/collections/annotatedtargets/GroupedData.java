package org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets;

import org.jetbrains.annotations.NotNull;

public interface GroupedData {
	@NotNull
	default String group() {
		return this.getClass().getSimpleName();
	}
}
