package org.mercurialftc.mercurialftc.scheduler.subsystems;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;

public interface SubsystemInterface {
	Command getDefaultCommand();
	
	/**
	 * The code to be run when the OpMode is initialised.
	 */
	void init();
	
	/**
	 * The method that is ran at the start of every loop to facilitate encoder reads
	 * and any other calculations that need to be ran every loop regardless of the command
	 */
	void periodic();
	
	/**
	 * The default command run by a subsystem, will run every loop until a different command is scheduled over it
	 */
	void defaultCommandExecute();
	
	
	/**
	 * methods to be run when the subsystem is no longer used,
	 * for instance when the option to close the subsystem is implemented at the end of an OpMode,
	 * or when a new scheduler instance is forced.
	 */
	void close();
}
