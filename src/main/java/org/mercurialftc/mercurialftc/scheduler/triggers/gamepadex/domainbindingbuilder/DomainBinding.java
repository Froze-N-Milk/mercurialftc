package org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex.domainbindingbuilder;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.commands.CommandSignature;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;
import org.mercurialftc.mercurialftc.scheduler.triggers.Trigger;
import org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex.BindingInterface;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

@SuppressWarnings("unused")
public class DomainBinding<S extends DoubleSupplier> implements BindingInterface<DomainBinding<S>> {
	private final S s;
	private final BooleanSupplier internalInput;
	private boolean previousState;
	private long leadingEdgeDebounce;
	private long trailingEdgeDebounce;
	private long lastCheck = System.nanoTime() - 100;
	private boolean toggledOn = false;
	private boolean previousToggleState = true;
	private boolean processedInput = false;

	public DomainBinding(S s, BooleanSupplier internalInput) {
		this.internalInput = internalInput;
		this.s = s;
	}

	@Override
	public void endLoopUpdate() {
		previousToggleState = toggledOn;
		previousState = state();
	}

	@Override
	public boolean state() {
		if (processedInput != previousState) {
			lastCheck = System.nanoTime();
		}
		if (internalInput.getAsBoolean() && (System.nanoTime() - lastCheck + 1 >= leadingEdgeDebounce)) {
			toggledOn = !toggledOn;
			processedInput = true;
			lastCheck = System.nanoTime();
		} else if (!internalInput.getAsBoolean() && (System.nanoTime() - lastCheck + 1 >= trailingEdgeDebounce)) {
			processedInput = false;
			lastCheck = System.nanoTime();
		}

		return processedInput;
	}

	@Override
	public DomainBinding<S> whileTrue(@NotNull CommandSignature toRun) {
		new Trigger(() -> (state() && state() != previousState),
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.init(toRun::initialise)
						.execute(toRun::execute)
						.end(toRun::end)
						.finish(() -> !state() || toRun.finished())
						.setInterruptable(toRun.interruptable())
		);
		return this;
	}

	@Override
	public DomainBinding<S> whileFalse(@NotNull CommandSignature toRun) {
		new Trigger(() -> (state() && state() != previousState),
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.setRunStates(toRun.getRunStates())
						.init(toRun::initialise)
						.execute(toRun::execute)
						.end(toRun::end)
						.finish(() -> state() || toRun.finished())
						.setInterruptable(toRun.interruptable())
		);
		return this;
	}

	@Override
	public DomainBinding<S> onTrue(@NotNull CommandSignature toRun) {
		new Trigger(() -> (state() && state() != previousState), toRun);
		return this;
	}

	@Override
	public DomainBinding<S> toggle(@NotNull CommandSignature toRun) {
		new Trigger(() -> {
			state();
			return toggledOn && !previousToggleState;
		},
				new LambdaCommand()
						.setRequirements(toRun.getRequiredSubsystems())
						.setRunStates(toRun.getRunStates())
						.init(toRun::initialise)
						.execute(toRun::execute)
						.end(toRun::end)
						.finish(() -> !toggledOn || toRun.finished())
						.setInterruptable(toRun.interruptable())
		);
		return this;
	}

	@Override
	public DomainBinding<S> onFalse(@NotNull CommandSignature toRun) {
		new Trigger(() -> (!state() && state() != previousState), toRun);
		return this;
	}

	@Override
	public DomainBinding<S> debounce(@NotNull DebouncingType type, double duration) {
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

	public S endBinding() {
		return s;
	}
}
