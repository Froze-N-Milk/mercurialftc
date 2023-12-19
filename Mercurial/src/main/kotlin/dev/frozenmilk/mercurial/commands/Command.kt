package dev.frozenmilk.mercurial.commands

import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.RunStates
import dev.frozenmilk.mercurial.subsystems.Subsystem

interface Command {
	/**
	 * Gets run once when a command is first scheduled
	 */
	fun initialise()

	/**
	 * Gets run once per loop until [.finished]
	 */
	fun execute()

	/**
	 * Gets run once when [.finished] or when interrupted
	 */
	fun end(interrupted: Boolean)

	/**
	 * the supplier for the natural end condition of the command
	 *
	 * @return true when the command should finish
	 */
	fun finished(): Boolean

	/**
	 * @return the set of subsystems required by this command
	 */
	val requiredSubsystems: Set<Subsystem>

	/**
	 * @return the set of OpMode run states during which this command is allowed to run
	 */
	val runStates: Set<RunStates>

	/**
	 * @return if this command is allowed to be interrupted by others
	 */
	val interruptible: Boolean
		get() = true

	/**
	 * schedule the command with the scheduler
	 */
	fun schedule() {
		if (!Mercurial.isScheduled(this)) Mercurial.scheduleCommand(this)
	}

	fun toLambda(): LambdaCommand = LambdaCommand.from(this)
}
