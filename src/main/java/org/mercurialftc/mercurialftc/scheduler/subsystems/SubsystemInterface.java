package org.mercurialftc.mercurialftc.scheduler.subsystems;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;

public interface SubsystemInterface {
	/**
	 * @return the default command of the subsystem, should not create a new instance when called, see {@link #setDefaultCommand(Command)}
	 */
	Command getDefaultCommand();

	/**
	 * sets the default command of the subsystem, overriding the previous contents
	 *
	 * @param defaultCommand the new default command of the subsystem
	 */
	void setDefaultCommand(Command defaultCommand);

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
	 * afterward, the default command will be queued again
	 * <p>you can use {@link #setDefaultCommand(Command)} to gain greater control over the default command of a subsystem, if you need more fine-grain control.</p>
	 */
	void defaultCommandExecute();


	/**
	 * methods to be run when the subsystem is no longer used.
	 */
	void close();
}
