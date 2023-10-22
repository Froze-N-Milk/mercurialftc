package org.mercurialftc.mercurialftc.scheduler.commands;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class LambdaCommand implements Command {
	private final Runnable commandInit;
	private final Runnable commandMethod;
	private final BooleanSupplier commandFinish;
	private final Consumer<Boolean> commandEnd;
	private final boolean interruptable;
	private final Set<OpModeEX.OpModeEXRunStates> runStates;
	private final Set<SubsystemInterface> requiredSubsystems;

	/**
	 * constructs a default lambda command with the following default behaviours:
	 * <p>no requirements</p>
	 * <p>an empty init method</p>
	 * <p>an empty execute method</p>
	 * <p>instantly finishes</p>
	 * <p>an empty end method</p>
	 * <p>is interruptable</p>
	 * <p>allowed to run in LOOP only</p>
	 * <p>these are sensible defaults for a command that is meant to run in LOOP</p>
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

	/**
	 * non-mutating, sets the requirements, overriding the previous contents
	 *
	 * @param requiredSubsystems subsystem requirements of this command
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setRequirements(@NotNull SubsystemInterface... requiredSubsystems) {
		Set<SubsystemInterface> requirements = new HashSet<>(requiredSubsystems.length);
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

	/**
	 * non-mutating, sets the requirements, overriding the previous contents
	 *
	 * @param requiredSubsystems subsystem requirements of this command
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setRequirements(@NotNull Set<SubsystemInterface> requiredSubsystems) {
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

	/**
	 * non-mutating, sets the init method, overriding the previous contents
	 *
	 * @param initialise the new initialise method of the command
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setInit(Runnable initialise) {
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

	/**
	 * non-mutating, sets the execute method, overriding the previous contents
	 *
	 * @param execute the new execute method of the command
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setExecute(Runnable execute) {
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

	/**
	 * non-mutating, sets the finish method, overriding the previous contents
	 *
	 * @param finish the new finish method of the command
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setFinish(BooleanSupplier finish) {
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

	/**
	 * non-mutating, sets the end method, overriding the previous contents
	 *
	 * @param end the new end method of the command
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setEnd(Consumer<Boolean> end) {
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

	/**
	 * non-mutating, sets if interruption is allowed
	 *
	 * @param interruptable if interruption is allowed
	 * @return a new LambdaCommand
	 */
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

	/**
	 * non-mutating, adds to the current requirements
	 *
	 * @param requiredSubsystems the additional required subsystems
	 * @return a new LambdaCommand
	 */
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

	/**
	 * non-mutating, adds to the current requirements
	 *
	 * @param requiredSubsystems the additional required subsystems
	 * @return a new LambdaCommand
	 */
	public LambdaCommand addRequirements(@NotNull Set<SubsystemInterface> requiredSubsystems) {
		requiredSubsystems.addAll(this.requiredSubsystems);
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

	/**
	 * non-mutating, adds to the current init method
	 *
	 * @param initialise the additional method to run after the preexisting init
	 * @return a new LambdaCommand
	 */
	public LambdaCommand addInit(Runnable initialise) {
		return new LambdaCommand(
				this.requiredSubsystems,
				() -> {
					this.commandInit.run();
					initialise.run();
				},
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptable,
				this.runStates
		);
	}

	/**
	 * non-mutating, adds to the current execute method
	 *
	 * @param execute the additional method to run after the preexisting execute
	 * @return a new LambdaCommand
	 */
	public LambdaCommand addExecute(Runnable execute) {
		return new LambdaCommand(
				this.requiredSubsystems,
				this.commandInit,
				() -> {
					this.commandMethod.run();
					execute.run();
				},
				this.commandFinish,
				this.commandEnd,
				this.interruptable,
				this.runStates
		);
	}

	/**
	 * non-mutating, adds to the current finish method, either the preexisting method OR the new one will end the command
	 *
	 * @param finish the additional condition to consider after the preexisting finish
	 * @return a new LambdaCommand
	 */
	public LambdaCommand addFinish(BooleanSupplier finish) {
		return new LambdaCommand(
				this.requiredSubsystems,
				this.commandInit,
				this.commandMethod,
				() -> this.commandFinish.getAsBoolean() || finish.getAsBoolean(),
				this.commandEnd,
				this.interruptable,
				this.runStates
		);
	}

	/**
	 * non-mutating, adds to the current end method
	 *
	 * @param end the additional method to run after the preexisting end
	 * @return a new LambdaCommand
	 */
	public LambdaCommand addEnd(Consumer<Boolean> end) {
		return new LambdaCommand(
				this.requiredSubsystems,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				(interrupted) -> {
					this.commandEnd.accept(interrupted);
					end.accept(interrupted);
				},
				this.interruptable,
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

	/**
	 * non-mutating, sets the RunStates, overriding the previous contents
	 *
	 * @param runStates allowed RunStates of the command
	 * @return a new LambdaCommand
	 */
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

	/**
	 * non-mutating, sets the RunStates, overriding the previous contents
	 *
	 * @param runStates allowed RunStates of the command
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setRunStates(Set<OpModeEX.OpModeEXRunStates> runStates) {
		return new LambdaCommand(
				this.requiredSubsystems,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptable,
				runStates
		);
	}
}
