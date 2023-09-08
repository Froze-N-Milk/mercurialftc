package org.mercurialftc.mercurialftc.example;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;

/**
 * A demonstration of a command. Sets the demoSubsystem to run for 5 seconds or until this command is interrupted
 */
public class DemoCommand extends Command {
	private final DemoSubsystem demoSubsystem;
	private ElapsedTime elapsedTime;
	private double startTime;

	public DemoCommand(DemoSubsystem demoSubsystem) {
		super(demoSubsystem);
		this.demoSubsystem = demoSubsystem;
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public void initialise() {
		elapsedTime = demoSubsystem.opModeEX.getElapsedTime();
		startTime = elapsedTime.seconds();
	}

	@Override
	public void execute() {

	}

	@Override
	public void end(boolean interrupted) {
	}

	@Override
	public boolean finished() {
		return elapsedTime.seconds() - startTime >= 5;
	}
}
