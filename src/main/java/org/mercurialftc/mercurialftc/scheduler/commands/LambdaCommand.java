package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class LambdaCommand extends Command {
	private final Runnable commandInit;
	private final Runnable commandMethod;
	private final BooleanSupplier commandFinish;
	private final Consumer<Boolean> commandEnd;
	private final boolean interruptable;
	private final Set<OpModeEX.OpModeEXRunStates> runStates;
	private final Set<SubsystemInterface> requiredSubsystems;

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
				new HashSet<>(Collections.singletonList(OpModeEX.OpModeEXRunStates.LOOP))
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
		this.requiredSubsystems = requiredSubsystems;
		this.commandInit = commandInit;
		this.commandMethod = commandMethod;
		this.commandFinish = commandFinish;
		this.commandEnd = commandEnd;
		this.interruptable = interruptable;
		this.runStates = runStates;
	}

	public LambdaCommand setRequirements(SubsystemInterface... requiredSubsystems) {
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

	public LambdaCommand setRequirements(Set<SubsystemInterface> requiredSubsystems) {
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
				this.requiredSubsystems,
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
				this.requiredSubsystems,
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
				this.requiredSubsystems,
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
				this.requiredSubsystems,
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
				this.requiredSubsystems,
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
	public Set<SubsystemInterface> getRequiredSubsystems() {
		return requiredSubsystems;
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
		if (runStates.isEmpty()) runStates.add(OpModeEX.OpModeEXRunStates.LOOP);
		return runStates;
	}

	public LambdaCommand setRunStates(OpModeEX.OpModeEXRunStates... runStates) {
		return new LambdaCommand(
				this.requiredSubsystems,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptable,
				new HashSet<>(Arrays.asList(runStates))
		);
	}

}
