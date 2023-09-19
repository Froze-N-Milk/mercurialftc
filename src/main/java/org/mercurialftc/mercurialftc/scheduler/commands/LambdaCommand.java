package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class LambdaCommand extends Command {
	private final Runnable commandInit;
	private final Runnable commandMethod;
	private final BooleanSupplier commandFinish;
	private final Consumer<Boolean> commandEnd;
	private final boolean interruptable;
	private final Set<OpModeEX.OpModeEXRunStates> runStates;

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
				(interrupted) -> {
				},
				true,
				new HashSet<>()
		);
	}

	private LambdaCommand(
			Set<SubsystemInterface> requiredSubsystems,
			Runnable commandInit,
			Runnable commandMethod,
			BooleanSupplier commandFinish,
			Consumer<Boolean> commandEnd,
			boolean interruptable,
			Set<OpModeEX.OpModeEXRunStates> runStates
	) {
		super(requiredSubsystems);
		this.commandInit = commandInit;
		this.commandMethod = commandMethod;
		this.commandFinish = commandFinish;
		this.commandEnd = commandEnd;
		this.interruptable = interruptable;
		this.runStates = runStates;
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
				this.interruptable,
				this.runStates
		);
	}

	public LambdaCommand addRequirements(Set<SubsystemInterface> requiredSubsystems) {
		return new LambdaCommand(
				requiredSubsystems,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptable,
				this.runStates
		);
	}

	public LambdaCommand init(Runnable initialise) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				initialise,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptable,
				this.runStates
		);
	}

	public LambdaCommand execute(Runnable execute) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				execute,
				this.commandFinish,
				this.commandEnd,
				this.interruptable,
				this.runStates
		);
	}

	public LambdaCommand finish(BooleanSupplier finish) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				finish,
				this.commandEnd,
				this.interruptable,
				this.runStates
		);
	}

	public LambdaCommand end(Consumer<Boolean> end) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				end,
				this.interruptable,
				this.runStates
		);
	}

	public LambdaCommand setInterruptable(boolean interruptable) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				interruptable,
				this.runStates
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
	public void end(boolean interrupted) {
		commandEnd.accept(interrupted);
	}

	@Override
	public final boolean interruptable() {
		return interruptable;
	}

	@Override
	public Set<OpModeEX.OpModeEXRunStates> getRunStates() {
		return runStates;
	}

	public LambdaCommand setRunStates(OpModeEX.OpModeEXRunStates... runStates) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptable,
				new HashSet<>(Arrays.asList(runStates))
		);
	}

	/**
	 * ensures that Loop in included at queue time
	 */
	@Override
	public void queue() {
		if (runStates.isEmpty()) runStates.add(OpModeEX.OpModeEXRunStates.LOOP);
		super.queue();
	}
}
