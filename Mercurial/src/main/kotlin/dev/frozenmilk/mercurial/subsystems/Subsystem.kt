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
	 * gets called when this subsystem is registered for the very first time
	 *
	 * should contain one-off initialisation code
	 */
	fun init()

	/**
	 * is called to refresh the subsystem at the start of an auto or at the start of a teleop, following another teleop
	 *
	 * is used to facilitate 
	 *
	 * @see dev.frozenmilk.mercurial.Mercurify.crossPollinate
	 * @see Mercurial.crossPollinate
	 */
	fun refresh()

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