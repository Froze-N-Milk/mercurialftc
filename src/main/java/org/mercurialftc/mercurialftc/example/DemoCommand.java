package org.mercurialftc.mercurialftc.example;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import com.qualcomm.robotcore.util.ElapsedTime;

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
	public boolean getOverrideAllowed() {
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
	public void end() {
	}

	@Override
	public boolean finishCondition() {
		return elapsedTime.seconds() - startTime >= 5;
	}
}
