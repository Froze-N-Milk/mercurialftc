package org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;
import org.mercurialftc.mercurialftc.scheduler.triggers.Trigger;

import java.util.function.BooleanSupplier;

/**
 * allows for the powerful binding of commands to boolean conditions, especially those supplied by gamepad inputs and sensors
 */
@SuppressWarnings("unused")
public class Binding<B extends Binding<B>> {
	@SuppressWarnings("unchecked")
	private final B thisAsB = (B) this;
	private final BooleanSupplier internalInput;
	private boolean previousState;
	private long leadingEdgeDebounce;
	private long trailingEdgeDebounce;
	private long lastCheck = System.nanoTime() - 100;
	private boolean toggledOn = false;
	private boolean previousToggleState = true;
	private boolean processedInput = false;

	public Binding(BooleanSupplier internalInput) {
		this.internalInput = internalInput;
	}

	public void endLoopUpdate() {
		previousToggleState = toggledOn;
		previousState = state();
	}

	public boolean state() {
		if (processedInput != previousState) {
			lastCheck = System.nanoTime();
		}
		if (internalInput.getAsBoolean() && (System.nanoTime() - lastCheck >= leadingEdgeDebounce)) {
			toggledOn = !toggledOn;
			processedInput = true;
			lastCheck = System.nanoTime();
		} else if (!internalInput.getAsBoolean() && (System.nanoTime() - lastCheck >= trailingEdgeDebounce)) {
			processedInput = false;
			lastCheck = System.nanoTime();
		}

		return processedInput;
	}

	public B whileTrue(@NotNull Command toRun) {
		new Trigger(() -> (state() && state() != previousState),
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.setInit(toRun::initialise)
						.setExecute(toRun::execute)
						.setEnd(toRun::end)
						.setFinish(() -> !state() || toRun.finished())
						.setInterruptable(toRun.interruptable())
		);
		return thisAsB;
	}

	public B whileFalse(@NotNull Command toRun) {
		new Trigger(() -> (state() && state() != previousState),
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.setRunStates(toRun.getRunStates())
						.setInit(toRun::initialise)
						.setExecute(toRun::execute)
						.setEnd(toRun::end)
						.setFinish(() -> state() || toRun.finished())
						.setInterruptable(toRun.interruptable())
		);
		return thisAsB;
	}

	public B onTrue(@NotNull Command toRun) {
		new Trigger(() -> (state() && state() != previousState), toRun);
		return thisAsB;
	}

	public B toggle(@NotNull Command toRun) {
		new Trigger(() -> {
			state();
			return toggledOn && !previousToggleState;
		},
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.setRunStates(toRun.getRunStates())
						.setInit(toRun::initialise)
						.setExecute(toRun::execute)
						.setEnd(toRun::end)
						.setFinish(() -> !toggledOn || toRun.finished())
						.setInterruptable(toRun.interruptable())
		);
		return thisAsB;
	}

	public B onFalse(@NotNull Command toRun) {
		new Trigger(() -> (!state() && state() != previousState), toRun);
		return thisAsB;
	}

	public B debounce(@NotNull DebouncingType type, double duration) {
		switch (type) {
			case LEADING_EDGE:
				leadingEdgeDebounce = (long) (duration * 1e9);
				break;
			case TRAILING_EDGE:
				trailingEdgeDebounce = (long) (duration * 1e9);
				break;
			case BOTH:
				leadingEdgeDebounce = (long) (duration * 1e9);
				trailingEdgeDebounce = (long) (duration * 1e9);
				break;
		}
		return thisAsB;
	}

	public enum DebouncingType {
		LEADING_EDGE,
		TRAILING_EDGE,
		BOTH
	}
}
