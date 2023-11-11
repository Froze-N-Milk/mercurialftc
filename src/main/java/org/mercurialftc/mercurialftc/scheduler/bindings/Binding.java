package org.mercurialftc.mercurialftc.scheduler.bindings;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;

import java.util.function.BooleanSupplier;

/**
 * allows for the powerful binding of commands to boolean conditions, especially those supplied by gamepad inputs and sensors
 */
@SuppressWarnings("unused, unchecked")
public class Binding<B extends Binding<B>> implements BooleanSupplier {
	private final B thisAsB = (B) this;
	private final BooleanSupplier internalInput;
	private boolean previousState;
	private long leadingEdgeDebounce;
	private long trailingEdgeDebounce;
	private long lastCheck = System.nanoTime() - 100;
	private boolean toggledOn = false;
	private boolean previousToggleState = false;
	private boolean processedInput = false;
	private boolean registered;

	public Binding(BooleanSupplier internalInput) {
		registered = false;
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
		registrationCheck();
		return processedInput;
	}

	public B whileTrue(@NotNull Command toRun) {
		return onTrue(LambdaCommand.from(toRun).addFinish(() -> !getAsBoolean()));
	}

	public B whileFalse(@NotNull Command toRun) {
		return onFalse(LambdaCommand.from(toRun).addFinish(this));
	}

	public B onTrue(@NotNull Command toRun) {
		registrationCheck();
		new Trigger(() -> (getAsBoolean() && !previousState), toRun);
		return thisAsB;
	}

	public B toggleTrue(@NotNull Command toRun) {
		registrationCheck();
		new Trigger(() -> toggledOn && !previousToggleState, LambdaCommand.from(toRun).addFinish(() -> !toggledOn));
		return thisAsB;
	}

	public B toggleFalse(@NotNull Command toRun) {
		registrationCheck();
		new Trigger(() -> !toggledOn && !previousToggleState, LambdaCommand.from(toRun).addFinish(() -> toggledOn));
		return thisAsB;
	}

	public B onFalse(@NotNull Command toRun) {
		registrationCheck();
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

	/**
	 * non-mutating
	 *
	 * @param andSupplier an and condition added to the binding
	 * @return a new binding that will have internal state of true if its previous internal BooleanSupplier AND its new additional BooleanSupplier return true
	 */
	public B and(BooleanSupplier andSupplier) {
		return (B) new Binding<B>(() -> this.getAsBoolean() && andSupplier.getAsBoolean());
	}

	/**
	 * non-mutating
	 *
	 * @param orSupplier an or condition added to the binding
	 * @return a new binding that will have internal state of true if its previous internal BooleanSupplier OR its new additional BooleanSupplier return true
	 */
	public B or(BooleanSupplier orSupplier) {
		return (B) new Binding<>(() -> this.getAsBoolean() || orSupplier.getAsBoolean());
	}

	/**
	 * ensures that this will be registered if it is used, otherwise, doesn't end up registered
	 */
	protected void registrationCheck() {
		if (!registered) {
			Scheduler.getSchedulerInstance().registerBinding(this);
			registered = true;
		}
	}

	public enum DebouncingType {
		LEADING_EDGE,
		TRAILING_EDGE,
		BOTH
	}
}
