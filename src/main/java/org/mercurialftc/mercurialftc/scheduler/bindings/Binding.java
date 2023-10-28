package org.mercurialftc.mercurialftc.scheduler.bindings;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;

import java.util.function.BooleanSupplier;

/**
 * allows for the powerful binding of commands to boolean conditions, especially those supplied by gamepad inputs and sensors
 */
@SuppressWarnings("unused")
public class Binding<B extends Binding<B>> implements BooleanSupplier {
	@SuppressWarnings("unchecked")
	private final B thisAsB = (B) this;
	private final BooleanSupplier internalInput;
	private boolean previousState;
	private long leadingEdgeDebounce;
	private long trailingEdgeDebounce;
	private long lastCheck = System.nanoTime() - 100;
	private boolean toggledOn = false;
	private boolean previousToggleState = false;
	private boolean processedInput = false;

	public Binding(BooleanSupplier internalInput) {
		Scheduler.getSchedulerInstance().registerBinding(this);
		this.internalInput = internalInput;
	}

	public void preLoopUpdate() {
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
	}

	public void postLoopUpdate() {
		previousToggleState = toggledOn;
		previousState = getAsBoolean();
	}

	@Override
	public boolean getAsBoolean() {
		return processedInput;
	}

	public B whileTrue(@NotNull Command toRun) {
		return onTrue(
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.setInit(toRun::initialise)
						.setExecute(toRun::execute)
						.setEnd(toRun::end)
						.addFinish(() -> !getAsBoolean())
						.setInterruptible(toRun.interruptable())
		);
	}

	public B whileFalse(@NotNull Command toRun) {
		return onFalse(
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.setRunStates(toRun.getRunStates())
						.setInit(toRun::initialise)
						.setExecute(toRun::execute)
						.setEnd(toRun::end)
						.addFinish(this)
						.setInterruptible(toRun.interruptable())
		);
	}

	public B onTrue(@NotNull Command toRun) {
		new Trigger(() -> (getAsBoolean() && !previousState), toRun);
		return thisAsB;
	}

	public B toggle(@NotNull Command toRun) {
		new Trigger(() -> toggledOn && !previousToggleState,
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.setRunStates(toRun.getRunStates())
						.setInit(toRun::initialise)
						.setExecute(toRun::execute)
						.setEnd(toRun::end)
						.addFinish(() -> !toggledOn)
						.setInterruptible(toRun.interruptable())
		);
		return thisAsB;
	}

	public B onFalse(@NotNull Command toRun) {
		new Trigger(() -> (!getAsBoolean() && previousState), toRun);
		return thisAsB;
	}

	/**
	 * @param type     the target of this debouncing operation
	 * @param duration number of seconds
	 * @return mutated self
	 */
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
