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
public class ButtonEX {
	private final BooleanSupplier internalInput;
	private boolean previousState;
	private long leadingEdgeDebounce;
	private long trailingEdgeDebounce;
	private long lastCheck = System.nanoTime() - 100;

	private boolean processedInput = false;

	public ButtonEX(BooleanSupplier internalInput) {
		this.internalInput = internalInput;
	}

	/**
	 * must be called at the end of every teleop loop, or other environment in which the {@link GamepadEX} inputs are used, which should be handled by {@link GamepadEX} update method, so no need for a user to call this
	 */
	public void endLoopUpdate() {
		previousState = buttonState();
	}

	/**
	 * includes debouncing
	 *
	 * @return returns the boolean value of the button with debouncing used
	 */
	public boolean buttonState() {
		if (processedInput != previousState) {
			lastCheck = System.nanoTime();
		}
		if (internalInput.getAsBoolean() && (System.nanoTime() - lastCheck + 1 >= leadingEdgeDebounce)) {
			processedInput = true;
			lastCheck = System.nanoTime();
		} else if (!internalInput.getAsBoolean() && (System.nanoTime() - lastCheck + 1 >= trailingEdgeDebounce)) {
			processedInput = false;
			lastCheck = System.nanoTime();
		}

		return processedInput;
	}

	/**
	 * same as {@link #onPress(Command)} but will enhance the finish condition so the command ends when the button is released, or it comes to its own finish
	 * <p>any number of commands can be bound here, binding a new one will not override the previous one</p>
	 *
	 * @param toRun the command to run
	 * @return self for chaining
	 */
	public ButtonEX whilePressed(@NotNull Command toRun) {
		new Trigger(() -> (buttonState() && buttonState() != previousState),
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.init(toRun::initialise)
						.execute(toRun::execute)
						.end(toRun::end)
						.finish(() -> !buttonState() || toRun.finished())
						.setInterruptable(toRun.interruptable())
		);
		return this;
	}

	/**
	 * same as {@link #onRelease(Command)} but will enhance the finish condition so the command ends when the button is pressed, or it comes to its own finish
	 * <p>any number of commands can be bound here, binding a new one will not override the previous one</p>
	 *
	 * @param toRun the command to run when the button
	 * @return self for chaining
	 */
	public ButtonEX whileReleased(@NotNull Command toRun) {
		new Trigger(() -> (buttonState() && buttonState() != previousState),
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.init(toRun::initialise)
						.execute(toRun::execute)
						.end(toRun::end)
						.finish(() -> buttonState() || toRun.finished())
						.setInterruptable(toRun.interruptable())
		);
		return this;
	}

	/**
	 * queues the command once when pressed
	 * <p>any number of commands can be bound here, binding a new one will not override the previous one</p>
	 *
	 * @param toRun the command to queue
	 * @return self for chaining
	 */
	public ButtonEX onPress(@NotNull Command toRun) {
		new Trigger(() -> (buttonState() && buttonState() != previousState), toRun);
		return this;
	}

	/**
	 * queues the command once when released
	 * <p>any number of commands can be bound here, binding a new one will not override the previous one</p>
	 *
	 * @param toRun the command to queue
	 * @return self for chaining
	 */
	public ButtonEX onRelease(@NotNull Command toRun) {
		new Trigger(() -> (!buttonState() && buttonState() != previousState), toRun);
		return this;
	}

	/**
	 * applies debouncing to the button, mutating
	 *
	 * @param type     type of debouncing to be updated, leading edge affects the change from true to false, trailing edge affects the change from false to true
	 * @param duration the duration of the debouncing to be applied, in seconds
	 * @return returns the updated button object
	 */
	public ButtonEX debounce(@NotNull DebouncingType type, double duration) {
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
		return this;
	}

	public enum DebouncingType {
		LEADING_EDGE,
		TRAILING_EDGE,
		BOTH
	}
}
