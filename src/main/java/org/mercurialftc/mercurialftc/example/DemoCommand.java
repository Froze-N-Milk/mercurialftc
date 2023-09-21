package org.mercurialftc.mercurialftc.example;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.HashSet;
import java.util.Set;

/**
 * A demonstration of a command. Sets the demoSubsystem to run for 5 seconds or until this command is interrupted
 */
public class DemoCommand extends Command {
	private final DemoSubsystem demoSubsystem;
	private ElapsedTime elapsedTime;
	private double startTime;

	@SuppressWarnings("unused")
	public DemoCommand(DemoSubsystem demoSubsystem) {
		this.demoSubsystem = demoSubsystem;
	}

	// this is functionally equivalent to not having this block here
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

	@Override
	public Set<SubsystemInterface> getRequiredSubsystems() {
		HashSet<SubsystemInterface> requirements = new HashSet<>();
		requirements.add(demoSubsystem);
		return requirements;
	}


	// this is functionally equivalent to not having this block here
	@Override
	public Set<OpModeEX.OpModeEXRunStates> getRunStates() {
		HashSet<OpModeEX.OpModeEXRunStates> runStates = new HashSet<>(1);
		runStates.add(OpModeEX.OpModeEXRunStates.LOOP);
		return runStates;
	}
}
