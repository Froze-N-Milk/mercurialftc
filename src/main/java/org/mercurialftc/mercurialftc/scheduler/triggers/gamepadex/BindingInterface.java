package org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.commands.CommandSignature;

@SuppressWarnings("unused")
public interface BindingInterface<B extends BindingInterface<B>> {
	void endLoopUpdate();

	boolean state();

	B whileTrue(@NotNull CommandSignature toRun);

	B whileFalse(@NotNull CommandSignature toRun);

	B onTrue(@NotNull CommandSignature toRun);

	B toggle(@NotNull CommandSignature toRun);

	B onFalse(@NotNull CommandSignature toRun);

	B debounce(@NotNull DebouncingType type, double duration);

	enum DebouncingType {
		LEADING_EDGE,
		TRAILING_EDGE,
		BOTH
	}
}
