package org.mercurialftc.mercurialftc.scheduler.commands;

public interface CommandSignature {
	void initialise();

	void execute();

	void end();

	boolean finished();
}
