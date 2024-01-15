package dev.frozenmilk.mercurial.commands

import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.RunStates
import dev.frozenmilk.mercurial.subsystems.Subsystem

class ProxyCommandGroup : CommandGroup {
    private val commands: MutableMap<Command, Boolean>
    override val requiredSubsystems: Set<Subsystem>
    override val runStates: Set<RunStates>
    private var _interruptible: Boolean

    /**
     * a new empty ProxyCommandGroup, which will schedule all of its commands at the same time and wait for them to become unscheduled
     */
    constructor() {
        requiredSubsystems = HashSet()
        runStates = HashSet(2)
        _interruptible = true
        commands = HashMap()
    }

    private constructor(commands: HashMap<Command, Boolean>, runStates: Set<RunStates>, interruptable: Boolean) {
        requiredSubsystems = HashSet()
        this.runStates = runStates
        this._interruptible = interruptable
        this.commands = commands
    }

    /**
     * non-mutating
     *
     * @param commands new commands to add
     * @return a new ProxyCommandGroup, with the added commands
     */
    override fun addCommands(vararg commands: Command): ProxyCommandGroup {
        return addCommands(listOf(*commands))
    }

    /**
     * non-mutating
     *
     * @param commands new commands to add
     * @return a new ProxyCommandGroup, with the added commands
     */
    override fun addCommands(commands: Collection<Command>): ProxyCommandGroup {
        check(!this.commands.containsValue(true)) { "Commands cannot be added to a composition while it's running" }
        val newCommandMap = HashMap(this.commands)
        var newInterruptable = interruptible
        val newRunStates = HashSet<RunStates>(this.runStates)
        for (command in commands) {
            newCommandMap[command] = false
            newInterruptable = newInterruptable and command.interruptible
            newRunStates.addAll(command.runStates)
        }
        return ProxyCommandGroup(
            newCommandMap,
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
            throw RuntimeException("Attempted to run empty ProxyCommandGroup, ProxyCommandGroup requires a minimum of 1 Command to be run")
        }
        _interruptible = true
        for (commandRunning in commands.entries) {
            if (commandRunning.key.runStates.contains(Mercurial.runState)) {
                commandRunning.key.schedule()
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
            if (!commandRunning.value) {
                continue
            }
            val command = commandRunning.key
            if (!Mercurial.isScheduled(command)) {
                commands[command] = false
            } else if (!command.runStates.contains(Mercurial.runState)) {
                Mercurial.cancelCommand(command)
                commands[command] = false
            } else {
                _interruptible = _interruptible and command.interruptible
            }
        }
    }

    override fun end(interrupted: Boolean) {
        for (commandRunning in commands.entries) {
            if (commandRunning.value) {
                Mercurial.cancelCommand(commandRunning.key)
                commandRunning.setValue(false)
            }
        }
    }

    override fun finished(): Boolean {
        return !commands.containsValue(true)
    }
}

fun Command.asProxy(): ProxyCommandGroup {
    return ProxyCommandGroup().addCommands(this)
}
