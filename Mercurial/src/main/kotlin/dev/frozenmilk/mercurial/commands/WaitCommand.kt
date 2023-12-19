package dev.frozenmilk.mercurial.commands

import dev.frozenmilk.mercurial.RunStates
import dev.frozenmilk.mercurial.subsystems.Subsystem

/**
 * a command that waits for the specified duration, in seconds
 */
class WaitCommand(val duration: Double) : Command {
	var startTime = 0L
		private set
	override fun initialise() {
		startTime = System.nanoTime()
	}

	override fun execute() {
	}

	override fun end(interrupted: Boolean) {
	}

	override fun finished(): Boolean {
		return (System.nanoTime() - startTime) / 1E9 >= duration
	}

	override val requiredSubsystems: Set<Subsystem> = emptySet()
	override val runStates: Set<RunStates> = setOf(RunStates.INIT_LOOP, RunStates.LOOP)
}