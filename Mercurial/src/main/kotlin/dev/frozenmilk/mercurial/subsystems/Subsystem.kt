package dev.frozenmilk.mercurial.subsystems

import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.commands.Command

interface Subsystem {
	/**
	 * the default command of the subsystem, null if none
	 */
	var defaultCommand: Command?
		get() {
			return Mercurial.getDefaultCommand(this)
		}
		set(value) {
			Mercurial.setDefaultCommand(this, value)
		}

	/**
	 * The code to be run when the OpMode is initialised.
	 * should include hardware initialisation
	 */
	fun init()

	/**
	 * gets called to reset hardware devices if its the appropriate time to do so
	 *
	 * gets called at the start of an auto, and at the end of a teleop, allowing sensors to not be reset from an auto to a teleop
	 */
	fun reset()

	/**
	 * The method that is run at the start of every loop to facilitate encoder reads
	 * and any other calculations that need to be run every loop regardless of the command.
	 * Telemetry operations may also be run here.
	 * Or logging operations.
	 */
	fun periodic()

	/**
	 * methods to be run when the subsystem is no longer used.
	 */
	fun close()
}