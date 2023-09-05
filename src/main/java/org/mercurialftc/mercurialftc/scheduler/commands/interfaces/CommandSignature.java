package org.mercurialftc.mercurialftc.scheduler.commands.interfaces;

public interface CommandSignature extends CommandInit, CommandMethod, CommandEnd, CommandFinish {
	void initialise();
	void execute();
	void end();
	boolean isFinished();
}
