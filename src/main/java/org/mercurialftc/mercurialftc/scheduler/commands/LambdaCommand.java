package org.mercurialftc.mercurialftc.scheduler.commands;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class LambdaCommand implements Command {
	private final static HashSet<SubsystemInterface> DEFAULT_REQUIREMENTS = new HashSet<>();
	private final static HashSet<OpModeEX.OpModeEXRunStates> DEFAULT_RUN_STATES = new HashSet<>(Collections.singletonList(OpModeEX.OpModeEXRunStates.LOOP));
	private final Runnable commandInit;
	private final Runnable commandMethod;
	private final BooleanSupplier commandFinish;
	private final Consumer<Boolean> commandEnd;
	private final BooleanSupplier interruptibleSupplier;
	private final Supplier<Set<OpModeEX.OpModeEXRunStates>> runStatesSupplier;
	private final Supplier<Set<SubsystemInterface>> requiredSubsystemsSupplier;

	/**
	 * constructs a default lambda command with the following default behaviours:
	 * <p>no requirements</p>
	 * <p>an empty init method</p>
	 * <p>an empty execute method</p>
	 * <p>instantly finishes</p>
	 * <p>an empty end method</p>
	 * <p>is interruptible</p>
	 * <p>allowed to run in LOOP only</p>
	 * <p>these are sensible defaults for a command that is meant to run in LOOP</p>
	 */
	public LambdaCommand() {
		this(
				() -> DEFAULT_REQUIREMENTS,
				() -> {
				},
				() -> {
				},
				() -> true,
				(interrupted) -> {
				},
				() -> true,
				() -> DEFAULT_RUN_STATES
		);
	}

	private LambdaCommand(
			Supplier<Set<SubsystemInterface>> requiredSubsystemsSupplier,
			Runnable commandInit,
			Runnable commandMethod,
			BooleanSupplier commandFinish,
			Consumer<Boolean> commandEnd,
			BooleanSupplier interruptibleSupplier,
			Supplier<Set<OpModeEX.OpModeEXRunStates>> runStatesSupplier
	) {
		this.requiredSubsystemsSupplier = requiredSubsystemsSupplier;
		this.commandInit = commandInit;
		this.commandMethod = commandMethod;
		this.commandFinish = commandFinish;
		this.commandEnd = commandEnd;
		this.interruptibleSupplier = interruptibleSupplier;
		this.runStatesSupplier = runStatesSupplier;
	}

	/**
	 * Composes a Command into a LambdaCommand
	 *
	 * @param command the command to convert
	 * @return a new LambdaCommand with the features of the argument
	 */
	@NotNull
	public static LambdaCommand from(@NotNull Command command) {
		if (command instanceof LambdaCommand) return (LambdaCommand) command;
		return new LambdaCommand(command::getRequiredSubsystems, command::initialise, command::execute, command::finished, command::end, command::interruptible, command::getRunStates);
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
				() -> requirements,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
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
				() -> requiredSubsystems,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
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
				this.requiredSubsystemsSupplier,
				initialise,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
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
				this.requiredSubsystemsSupplier,
				this.commandInit,
				execute,
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
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
				this.requiredSubsystemsSupplier,
				this.commandInit,
				this.commandMethod,
				finish,
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
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
				this.requiredSubsystemsSupplier,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				end,
				this.interruptibleSupplier,
				this.runStatesSupplier
		);
	}

	/**
	 * non-mutating, sets if interruption is allowed
	 *
	 * @param interruptible if interruption is allowed
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setInterruptible(boolean interruptible) {
		return new LambdaCommand(
				this.requiredSubsystemsSupplier,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				() -> interruptible,
				this.runStatesSupplier
		);
	}

	/**
	 * non-mutating, sets if interruption is allowed
	 *
	 * @param interruptibleSupplier if interruption is allowed
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setInterruptible(BooleanSupplier interruptibleSupplier) {
		return new LambdaCommand(
				this.requiredSubsystemsSupplier,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				interruptibleSupplier,
				this.runStatesSupplier
		);
	}


	/**
	 * non-mutating, adds additional if interruption is allowed conditions, either the preexisting method OR the new one returning true will allow interruption
	 *
	 * @param interruptibleSupplier if interruption is allowed
	 * @return a new LambdaCommand
	 */
	public LambdaCommand addInterruptible(BooleanSupplier interruptibleSupplier) {
		return new LambdaCommand(
				this.requiredSubsystemsSupplier,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				() -> this.interruptibleSupplier.getAsBoolean() || interruptibleSupplier.getAsBoolean(),
				this.runStatesSupplier
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
				() -> requirements,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
		);
	}

	/**
	 * non-mutating, adds to the current requirements
	 *
	 * @param requiredSubsystems the additional required subsystems
	 * @return a new LambdaCommand
	 */
	public LambdaCommand addRequirements(@NotNull Set<SubsystemInterface> requiredSubsystems) {
		requiredSubsystems.addAll(this.getRequiredSubsystems());
		return new LambdaCommand(
				() -> requiredSubsystems,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
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
				this.requiredSubsystemsSupplier,
				() -> {
					this.commandInit.run();
					initialise.run();
				},
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
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
				this.requiredSubsystemsSupplier,
				this.commandInit,
				() -> {
					this.commandMethod.run();
					execute.run();
				},
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
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
				this.requiredSubsystemsSupplier,
				this.commandInit,
				this.commandMethod,
				() -> this.commandFinish.getAsBoolean() || finish.getAsBoolean(),
				this.commandEnd,
				this.interruptibleSupplier,
				this.runStatesSupplier
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
				this.requiredSubsystemsSupplier,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				(interrupted) -> {
					this.commandEnd.accept(interrupted);
					end.accept(interrupted);
				},
				this.interruptibleSupplier,
				this.runStatesSupplier
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
		return requiredSubsystemsSupplier.get();
	}

	@Override
	public void end(boolean interrupted) {
		commandEnd.accept(interrupted);
	}

	@Override
	public final boolean interruptible() {
		return interruptibleSupplier.getAsBoolean();
	}

	@Override
	public Set<OpModeEX.OpModeEXRunStates> getRunStates() {
		return runStatesSupplier.get();
	}

	/**
	 * non-mutating, sets the RunStates, overriding the previous contents
	 *
	 * @param runStates allowed RunStates of the command
	 * @return a new LambdaCommand
	 */
	public LambdaCommand setRunStates(@NotNull OpModeEX.OpModeEXRunStates... runStates) {
		Set<OpModeEX.OpModeEXRunStates> runstatesSet = new HashSet<>(runStates.length);
		Collections.addAll(runstatesSet, runStates);
		return new LambdaCommand(
				this.requiredSubsystemsSupplier,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				() -> runstatesSet
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
				this.requiredSubsystemsSupplier,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.interruptibleSupplier,
				() -> runStates
		);
	}
}
