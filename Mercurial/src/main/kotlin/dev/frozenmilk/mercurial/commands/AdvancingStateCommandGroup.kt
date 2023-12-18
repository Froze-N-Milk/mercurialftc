package dev.frozenmilk.mercurial.commands

import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.RunStates
import dev.frozenmilk.mercurial.subsystems.Subsystem

class AdvancingStateCommandGroup private constructor(private val wraps: Boolean,
													 private val commands: ArrayList<Command>,
													 override val requiredSubsystems: Set<Subsystem>,
													 override val runStates: Set<RunStates>) : CommandGroup {
	private val advanceCommand: Command
	private val reverseCommand: Command
	private var commandIndex: Int
	private var currentCommand: Command? = null
	private var advanceState: Boolean

	/**
	 * a new empty AdvancingStateCommandGroup, which will interrupt its current command (if running) and move on to the next/previous one when [.advanceCommand] or [.reverseCommand] are called
	 */
	constructor(wraps: Boolean) : this(wraps, ArrayList<Command>(), HashSet<Subsystem>(), HashSet<RunStates>(2))

	init {
		commandIndex = -1
		advanceState = false
		advanceCommand = LambdaCommand().addInit {
			schedule()
			if (commandIndex != commands.size - 1 || wraps) {
				commandIndex++
				commandIndex %= commands.size
				advanceState = true
			}
		}
		reverseCommand = LambdaCommand().addInit {
			schedule()
			if (commandIndex != 0 || wraps) {
				commandIndex--
				commandIndex %= commands.size
				advanceState = true
			}
		}
	}

	/**
	 * moves the index back one, if the index was 0 and wrapping is true, moves to the last index
	 */
	fun reverse() {
		reverseCommand.initialise()
	}

	/**
	 * moves the index forward one, if the index was the last one and wrapping is true, moves to the index 0
	 */
	fun advance() {
		advanceCommand.initialise()
	}

	/**
	 * moves the index forward one, if the index was the last one and wrapping is true, moves to the index 0
	 */
	fun advanceCommand(): Command {
		return advanceCommand
	}

	/**
	 * moves the index back one, if the index was 0 and wrapping is true, moves to the last index
	 */
	fun reverseCommand(): Command {
		return reverseCommand
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new AdvancingStateCommandGroup, with the added commands
	 */
	override fun addCommands(vararg commands: Command): AdvancingStateCommandGroup {
		return addCommands(listOf(*commands))
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new AdvancingStateCommandGroup, with the added commands
	 */
	override fun addCommands(commands: Collection<Command>): AdvancingStateCommandGroup {
		check(commandIndex == -1) { "Commands cannot be added to a composition while it is running" }
		val newCommandList = ArrayList(this.commands)
		newCommandList.addAll(commands)
		val newRequirementSet = HashSet(requiredSubsystems)
		val newRunStates = HashSet<RunStates>(2)
		for (command in commands) {
			newRequirementSet.addAll(command.requiredSubsystems)
			newRunStates.addAll(command.runStates)
		}
		Mercurial.registerComposedCommands(commands)
		return AdvancingStateCommandGroup(
				wraps,
				newCommandList,
				newRequirementSet,
				newRunStates
		)
	}

	override val interruptible: Boolean
		get() {
			return currentCommand == null || currentCommand!!.interruptible
		}

	override fun initialise() {
		if (commands.isEmpty()) {
			throw RuntimeException("Attempted to run empty AdvancingStateCommandGroup, AdvancingStateCommandGroup requires a minimum of 1 Command to be run")
		}
		commandIndex = 0
		currentCommand = commands[commandIndex]
	}

	override fun execute() {
		if (advanceState) {
			if (currentCommand != null) currentCommand!!.end(true)
			currentCommand = commands[commandIndex]
			if (currentCommand != null) currentCommand!!.initialise()
			advanceState = false
		}
		if (currentCommand == null) return
		if (currentCommand!!.finished()) {
			currentCommand!!.end(false)
			currentCommand = null
		} else currentCommand!!.execute()
	}

	override fun end(interrupted: Boolean) {
		if (interrupted && currentCommand != null) currentCommand!!.end(true) else if (!interrupted && currentCommand != null) currentCommand!!.end(false)
		commandIndex = -1
	}

	override fun finished(): Boolean {
		return false
	}
}
