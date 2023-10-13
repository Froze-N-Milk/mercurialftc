package org.mercurialftc.mercurialftc.scheduler.subsystems;

import org.mercurialftc.mercurialftc.scheduler.commands.CommandSignature;

public interface SubsystemInterface {
	CommandSignature getDefaultCommand();

	/**
	 * The code to be run when the OpMode is initialised.
	 * should include hardware initialisation
	 */
	void init();

	/**
	 * The method that is run at the start of every loop to facilitate encoder reads
	 * and any other calculations that need to be run every loop regardless of the command.
	 * Telemetry operations may also be run here.
	 * Or logging operations.
	 */
	void periodic();

	/**
	 * The execute method of the default command run by a subsystem, will run every loop until a different command is scheduled over it,
	 * afterward, the default command will be queued up over the top
	 * <p>you can override {@link #getDefaultCommand()} to gain greater control over the default command of a subsystem</p>
	 */
	void defaultCommandExecute();


	/**
	 * methods to be run when the subsystem is no longer used.
	 */
	void close();
}
