package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class LambdaCommand extends Command {
	private final Runnable commandInit;
	private final Runnable commandMethod;
	private final BooleanSupplier commandFinish;
	private final Runnable commandEnd;
	private final boolean overrideAllowed;

	/**
	 * constructs a default lambda command
	 */
	public LambdaCommand() {
		this(
				new HashSet<>(),
				() -> {
				},
				() -> {
				},
				() -> true,
				() -> {
				},
				true
		);
	}

	private LambdaCommand(
			Set<SubsystemInterface> requiredSubsystems,
			Runnable commandInit,
			Runnable commandMethod,
			BooleanSupplier commandFinish,
			Runnable commandEnd,
			boolean overrideAllowed
	) {
		super(requiredSubsystems);
		this.commandInit = commandInit;
		this.commandMethod = commandMethod;
		this.commandFinish = commandFinish;
		this.commandEnd = commandEnd;
		this.overrideAllowed = overrideAllowed;
	}

	public LambdaCommand addRequirements(SubsystemInterface... requiredSubsystems) {
		Set<SubsystemInterface> requirements = this.getRequiredSubsystems();
		Collections.addAll(requirements, requiredSubsystems);

		return new LambdaCommand(
				requirements,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.overrideAllowed
		);
	}

	public LambdaCommand init(Runnable initialise) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				initialise,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.overrideAllowed
		);
	}

	public LambdaCommand execute(Runnable execute) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				execute,
				this.commandFinish,
				this.commandEnd,
				this.overrideAllowed
		);
	}

	public LambdaCommand finish(BooleanSupplier finish) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				finish,
				this.commandEnd,
				this.overrideAllowed
		);
	}

	public LambdaCommand end(Runnable end) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				end,
				this.overrideAllowed
		);
	}

	// Wrapper methods:
	@Override
	public final void initialise() {
		commandInit.run();
	}

	@Override
	public final void execute() {
		commandMethod.run();
	}

	@Override
	public final boolean finished() {
		return commandFinish.getAsBoolean();
	}

	@Override
	public void end() {
		commandEnd.run();
	}

	@Override
	public final boolean getOverrideAllowed() {
		return overrideAllowed;
	}

	public LambdaCommand setOverrideAllowed(boolean overrideAllowed) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				overrideAllowed
		);
	}
}
