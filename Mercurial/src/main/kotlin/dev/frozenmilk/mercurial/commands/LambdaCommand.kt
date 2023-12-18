package dev.frozenmilk.mercurial.commands

import dev.frozenmilk.mercurial.RunStates
import dev.frozenmilk.mercurial.subsystems.Subsystem
import java.util.Collections
import java.util.function.Consumer
import java.util.function.Supplier

class LambdaCommand private constructor(
		private val requiredSubsystemsSupplier: Supplier<Set<Subsystem>>,
		private val commandInit: Runnable,
		private val commandMethod: Runnable,
		private val commandFinish: Supplier<Boolean>,
		private val commandEnd: Consumer<Boolean>,
		private val interruptibleSupplier: Supplier<Boolean>,
		private val runStatesSupplier: Supplier<Set<RunStates>>
) : Command {
	/**
	 * constructs a default lambda command with the following default behaviours:
	 *
	 * no requirements
	 *
	 * an empty init method
	 *
	 * an empty execute method
	 *
	 * instantly finishes
	 *
	 * an empty end method
	 *
	 * is interruptible
	 *
	 * allowed to run in LOOP only
	 *
	 * these are sensible defaults for a command that is meant to run in LOOP
	 */
	constructor() : this(
			Supplier<Set<Subsystem>> { DEFAULT_REQUIREMENTS },
			Runnable {},
			Runnable {},
			Supplier { true },
			Consumer<Boolean> { },
			Supplier { true },
			Supplier<Set<RunStates>> { DEFAULT_RUN_STATES }
	)

	/**
	 * non-mutating, sets the requirements, overriding the previous contents
	 *
	 * @param requiredSubsystems subsystem requirements of this command
	 * @return a new LambdaCommand
	 */
	fun setRequirements(vararg requiredSubsystems: Subsystem): LambdaCommand {
		val requirements = HashSet<Subsystem>(requiredSubsystems.size)
		Collections.addAll(requirements, *requiredSubsystems)
		return LambdaCommand(
				{ requirements },
				commandInit,
				commandMethod,
				commandFinish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, sets the requirements, overriding the previous contents
	 *
	 * @param requiredSubsystems subsystem requirements of this command
	 * @return a new LambdaCommand
	 */
	fun setRequirements(requiredSubsystems: Set<Subsystem>): LambdaCommand {
		return LambdaCommand(
				{ requiredSubsystems },
				commandInit,
				commandMethod,
				commandFinish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, sets the init method, overriding the previous contents
	 *
	 * @param initialise the new initialise method of the command
	 * @return a new LambdaCommand
	 */
	fun setInit(initialise: Runnable): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				initialise,
				commandMethod,
				commandFinish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, sets the execute method, overriding the previous contents
	 *
	 * @param execute the new execute method of the command
	 * @return a new LambdaCommand
	 */
	fun setExecute(execute: Runnable): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				execute,
				commandFinish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, sets the finish method, overriding the previous contents
	 *
	 * @param finish the new finish method of the command
	 * @return a new LambdaCommand
	 */
	fun setFinish(finish: Supplier<Boolean>): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				commandMethod,
				finish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, sets the end method, overriding the previous contents
	 *
	 * @param end the new end method of the command
	 * @return a new LambdaCommand
	 */
	fun setEnd(end: Consumer<Boolean>): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				commandMethod,
				commandFinish,
				end,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, sets if interruption is allowed
	 *
	 * @param interruptible if interruption is allowed
	 * @return a new LambdaCommand
	 */
	fun setInterruptible(interruptible: Boolean): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				commandMethod,
				commandFinish,
				commandEnd,
				{ interruptible },
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, sets if interruption is allowed
	 *
	 * @param interruptibleSupplier if interruption is allowed
	 * @return a new LambdaCommand
	 */
	fun setInterruptible(interruptibleSupplier: Supplier<Boolean>): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				commandMethod,
				commandFinish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, adds additional if interruption is allowed conditions, either the preexisting method OR the new one returning true will allow interruption
	 *
	 * @param interruptibleSupplier if interruption is allowed
	 * @return a new LambdaCommand
	 */
	fun addInterruptible(interruptibleSupplier: Supplier<Boolean>): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				commandMethod,
				commandFinish,
				commandEnd,
				{ this.interruptibleSupplier.get() || interruptibleSupplier.get() },
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, adds to the current requirements
	 *
	 * @param requiredSubsystems the additional required subsystems
	 * @return a new LambdaCommand
	 */
	fun addRequirements(vararg requiredSubsystems: Subsystem): LambdaCommand {
		val requirements: MutableSet<Subsystem> = this.requiredSubsystems.toMutableSet()
		Collections.addAll(requirements, *requiredSubsystems)
		return LambdaCommand(
				{ requirements },
				commandInit,
				commandMethod,
				commandFinish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, adds to the current requirements
	 *
	 * @param requiredSubsystems the additional required subsystems
	 * @return a new LambdaCommand
	 */
	fun addRequirements(requiredSubsystems: MutableSet<Subsystem>): LambdaCommand {
		requiredSubsystems.addAll(this.requiredSubsystems)
		return LambdaCommand(
				{ requiredSubsystems },
				commandInit,
				commandMethod,
				commandFinish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, adds to the current init method
	 *
	 * @param initialise the additional method to run after the preexisting init
	 * @return a new LambdaCommand
	 */
	fun addInit(initialise: Runnable): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				{
					commandInit.run()
					initialise.run()
				},
				commandMethod,
				commandFinish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, adds to the current execute method
	 *
	 * @param execute the additional method to run after the preexisting execute
	 * @return a new LambdaCommand
	 */
	fun addExecute(execute: Runnable): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				{
					commandMethod.run()
					execute.run()
				},
				commandFinish,
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, adds to the current finish method, either the preexisting method OR the new one will end the command
	 *
	 * @param finish the additional condition to consider after the preexisting finish
	 * @return a new LambdaCommand
	 */
	fun addFinish(finish: Supplier<Boolean>): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				commandMethod,
				{ commandFinish.get() || finish.get() },
				commandEnd,
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	/**
	 * non-mutating, adds to the current end method
	 *
	 * @param end the additional method to run after the preexisting end
	 * @return a new LambdaCommand
	 */
	fun addEnd(end: Consumer<Boolean?>): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				commandMethod,
				commandFinish,
				{ interrupted: Boolean ->
					commandEnd.accept(interrupted)
					end.accept(interrupted)
				},
				interruptibleSupplier,
				runStatesSupplier
		)
	}

	// Wrapper methods:
	override fun initialise() {
		commandInit.run()
	}

	override fun execute() {
		commandMethod.run()
	}

	override fun finished(): Boolean {
		return commandFinish.get()
	}

	override val requiredSubsystems: Set<Subsystem>
		get() = requiredSubsystemsSupplier.get()

	override fun end(interrupted: Boolean) {
		commandEnd.accept(interrupted)
	}

	override val interruptible: Boolean
		get() {
			return interruptibleSupplier.get()
		}

	override val runStates: Set<RunStates>
		get() = runStatesSupplier.get()

	/**
	 * non-mutating, sets the RunStates, overriding the previous contents
	 *
	 * @param runStates allowed RunStates of the command
	 * @return a new LambdaCommand
	 */
	fun setRunStates(vararg runStates: RunStates): LambdaCommand {
		val runstatesSet: MutableSet<RunStates> = HashSet(runStates.size)
		Collections.addAll(runstatesSet, *runStates)
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				commandMethod,
				commandFinish,
				commandEnd,
				interruptibleSupplier
		) { runstatesSet }
	}

	/**
	 * non-mutating, sets the RunStates, overriding the previous contents
	 *
	 * @param runStates allowed RunStates of the command
	 * @return a new LambdaCommand
	 */
	fun setRunStates(runStates: Set<RunStates>): LambdaCommand {
		return LambdaCommand(
				requiredSubsystemsSupplier,
				commandInit,
				commandMethod,
				commandFinish,
				commandEnd,
				interruptibleSupplier
		) { runStates }
	}

	companion object {
		private val DEFAULT_REQUIREMENTS = HashSet<Subsystem>()
		private val DEFAULT_RUN_STATES = hashSetOf(RunStates.LOOP)

		/**
		 * Composes a Command into a LambdaCommand
		 *
		 * @param command the command to convert
		 * @return a new LambdaCommand with the features of the argument
		 */
		fun from(command: Command): LambdaCommand {
			return if (command is LambdaCommand) command else LambdaCommand(command::requiredSubsystems, { command.initialise() }, { command.execute() }, { command.finished() }, { interrupted: Boolean? -> command.end(interrupted!!) }, { command.interruptible }, command::runStates)
		}
	}
}

fun Command.toLambda(): LambdaCommand = LambdaCommand.from(this)