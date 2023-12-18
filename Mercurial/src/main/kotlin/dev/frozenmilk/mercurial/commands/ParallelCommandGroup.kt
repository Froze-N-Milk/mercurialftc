package dev.frozenmilk.mercurial.commands

import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.RunStates
import dev.frozenmilk.mercurial.subsystems.Subsystem
import java.util.Arrays
import java.util.Collections

@Suppress("unused")
class ParallelCommandGroup : CommandGroup {
	private val commands: MutableMap<Command, Boolean>
	override val requiredSubsystems: Set<Subsystem>
	override val runStates: Set<RunStates>
	private var _interruptible: Boolean

	/**
	 * a new empty ParallelCommandGroup, which will run all its commands at the same time
	 */
	constructor() {
		requiredSubsystems = HashSet()
		runStates = HashSet(2)
		_interruptible = true
		commands = HashMap()
	}

	private constructor(commands: HashMap<Command, Boolean>, requirements: Set<Subsystem>, runStates: Set<RunStates>, interruptable: Boolean) {
		requiredSubsystems = requirements
		this.runStates = runStates
		this._interruptible = interruptable
		this.commands = commands
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new ParallelCommandGroup, with the added commands
	 */
	override fun addCommands(vararg commands: Command): ParallelCommandGroup {
		return addCommands(Arrays.asList(*commands))
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new ParallelCommandGroup, with the added commands
	 */
	override fun addCommands(commands: Collection<Command>): ParallelCommandGroup {
		check(!this.commands.containsValue(true)) { "Commands cannot be added to a composition while it's running" }
		val newCommandMap = HashMap(this.commands)
		val newRequirementSet = HashSet(requiredSubsystems)
		var newInterruptable = interruptible
		val newRunStates = HashSet<RunStates>(2)
		for (command in commands) {
			require(Collections.disjoint(command.requiredSubsystems, requiredSubsystems)) { "Multiple commands in a parallel composition cannot require the same subsystems" }
			newCommandMap[command] = false
			newRequirementSet.addAll(command.requiredSubsystems)
			newInterruptable = newInterruptable and command.interruptible
			newRunStates.addAll(command.runStates)
		}
		Mercurial.registerComposedCommands(commands)
		return ParallelCommandGroup(
				newCommandMap,
				newRequirementSet,
				newRunStates,
				newInterruptable
		)
	}

	override val interruptible: Boolean
		get() {
			return _interruptible
		}

	override fun initialise() {
		if (commands.isEmpty()) {
			throw RuntimeException("Attempted to run empty ParallelCommandGroup, ParallelCommandGroup requires a minimum of 1 Command to be run")
		}
		_interruptible = true
		for (commandRunning in commands.entries) {
			if (commandRunning.key.runStates.contains(Mercurial.runState)) {
				commandRunning.key.initialise()
				commandRunning.setValue(true)
				_interruptible = _interruptible and commandRunning.key.interruptible
			} else {
				commandRunning.setValue(false)
			}
		}
	}

	override fun execute() {
		_interruptible = true
		for (commandRunning in commands.entries) {
			if (!(commandRunning.value && commandRunning.key.runStates.contains(Mercurial.runState))) {
				continue
			}
			val command = commandRunning.key
			if (command.finished()) {
				command.end(false)
				commands[command] = false
			} else {
				_interruptible = _interruptible and command.interruptible
				command.execute()
			}
		}
	}

	override fun end(interrupted: Boolean) {
		for ((command, value) in commands) {
			if (value) {
				if (command.runStates.contains(Mercurial.runState)) {
					command.end(interrupted)
				}
			}
		}
	}

	override fun finished(): Boolean {
		return !commands.containsValue(true)
	}
}
