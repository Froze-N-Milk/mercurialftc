package dev.frozenmilk.mercurial.commands

import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.RunStates
import dev.frozenmilk.mercurial.subsystems.Subsystem
import java.util.Arrays

class SequentialCommandGroup : CommandGroup {
	private val commands: ArrayList<Command>
	override val requiredSubsystems: Set<Subsystem>
	override val runStates: Set<RunStates>
	private var _interruptible: Boolean
	private var commandIndex: Int
	private var currentCommand: Command? = null

	constructor() {
		requiredSubsystems = HashSet()
		runStates = HashSet(2)
		commands = ArrayList()
		commandIndex = -1
		_interruptible = true
	}

	private constructor(commands: ArrayList<Command>, requirements: Set<Subsystem>, runStates: Set<RunStates>, interruptible: Boolean) {
		requiredSubsystems = requirements
		this.runStates = runStates
		this.commands = commands
		commandIndex = -1
		this._interruptible = interruptible
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new SequentialCommandGroup, with the added commands
	 */
	override fun addCommands(vararg commands: Command): SequentialCommandGroup {
		return addCommands(Arrays.asList(*commands))
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new SequentialCommandGroup, with the added commands
	 */
	override fun addCommands(commands: Collection<Command>): SequentialCommandGroup {
		check(commandIndex == -1) { "Commands cannot be added to a composition while it is running" }
		val newCommandList = ArrayList(this.commands)
		newCommandList.addAll(commands)
		val newRequirementSet = HashSet(requiredSubsystems)
		var newInterruptible = interruptible
		val newRunStates = HashSet<RunStates>(2)
		for (command in commands) {
			newRequirementSet.addAll(command.requiredSubsystems)
			newInterruptible = newInterruptible and command.interruptible
			newRunStates.addAll(command.runStates)
		}
		Mercurial.registerComposedCommands(commands)
		return SequentialCommandGroup(
				newCommandList,
				newRequirementSet,
				newRunStates,
				newInterruptible
		)
	}

	override val interruptible: Boolean
		get() {
			return _interruptible
		}

	override fun initialise() {
		if (commands.isEmpty()) {
			throw RuntimeException("Attempted to run empty SequentialCommandGroup, SequentialCommandGroupRequires requires a minimum of 1 Command to be run")
		}
		commandIndex = 0
		currentCommand = commands[commandIndex]
		if (currentCommand!!.runStates.contains(Mercurial.runState)) {
			currentCommand!!.initialise()
		}
	}

	override fun execute() {
		if (currentCommand == null) return
		if (currentCommand!!.finished() || !currentCommand!!.runStates.contains(Mercurial.runState)) {
			if (currentCommand!!.runStates.contains(Mercurial.runState)) {
				currentCommand!!.end(false)
			}
			commandIndex++
			if (commandIndex < commands.size) {
				currentCommand = commands[commandIndex]
				if (currentCommand!!.runStates.contains(Mercurial.runState)) {
					currentCommand!!.initialise()
				}
			} else {
				currentCommand = null
				return
			}
		}
		if (currentCommand!!.runStates.contains(Mercurial.runState)) {
			currentCommand!!.execute()
		}
	}

	override fun end(interrupted: Boolean) {
		commandIndex = -1
		if (currentCommand == null || !currentCommand!!.runStates.contains(Mercurial.runState)) return
		currentCommand!!.end(interrupted)
	}

	override fun finished(): Boolean {
		return commandIndex >= commands.size || currentCommand == null
	}
}
