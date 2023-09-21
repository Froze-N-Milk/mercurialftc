package org.mercurialftc.mercurialftc.scheduler;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;
import org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex.GamepadEX;

import java.util.List;

public abstract class OpModeEX extends OpMode {
	private GamepadEX
			gamepadEX1,
			gamepadEX2;

	private Scheduler scheduler;

	private List<LynxModule> allHubs;

	private ElapsedTime elapsedTime;

	public OpModeEX() {
		scheduler = Scheduler.freshInstance();
	}

	public final GamepadEX gamepadEX1() {
		return gamepadEX1;
	}

	public final GamepadEX gamepadEX2() {
		return gamepadEX2;
	}

	public final Scheduler getScheduler() {
		return scheduler;
	}

	public final List<LynxModule> getAllHubs() {
		return allHubs;
	}

	public final ElapsedTime getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * called before {@link #initEX()}, solely for initialising all subsystems, ensures that they are registered with the correct {@link Scheduler}, and that their init methods will be run
	 */
	public abstract void registerSubsystems();

	/**
	 * should contain your regular init code
	 */
	public abstract void initEX();

	/**
	 * registers triggers after the subsystem and regular init code,
	 * useful for organisation of your OpModeEX, but functionally no different to initialising them at the end of {@link #initEX()}
	 */
	public abstract void registerTriggers();

	/**
	 * should not be called, for internal use only, ensures that the current version of the scheduler is correct
	 */
	@Override
	public final void init() {
		if (Scheduler.isSchedulerRefreshEnabled()) {
			scheduler = Scheduler.freshInstance();
		} else {
			scheduler = Scheduler.getSchedulerInstance();
		}

		Telemetry.Item initialising = telemetry.addData("", "");
		initialising.setCaption("Initialising");
		initialising.setValue("Robot");
		telemetry.update();

		gamepadEX1 = new GamepadEX(gamepad1);
		gamepadEX2 = new GamepadEX(gamepad2);
		elapsedTime = new ElapsedTime();

		allHubs = hardwareMap.getAll(LynxModule.class);
		for (LynxModule module : allHubs) {
			module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
		}
		elapsedTime.reset();

		registerSubsystems();

		Telemetry.Item initialisedSubsystems = telemetry.addData("", "");
		initialisedSubsystems.setCaption("Initialised");
		StringBuilder initialisationSequencer = new StringBuilder();

		for (SubsystemInterface subsystem : scheduler.getSubsystems()) {
			String subsystemString = subsystem.getClass().getSimpleName();
			initialising.setValue(subsystemString);
			telemetry.update();
			subsystem.init();
			initialisationSequencer.append("\n");
			initialisationSequencer.append(subsystemString);
			initialisedSubsystems.setValue(initialisationSequencer);
			telemetry.update();
		}

		initEX();
		registerTriggers();

		initialisationSequencer.append("\nRobot");
		initialisedSubsystems.setValue(initialisationSequencer);
		telemetry.update();

		initialising.setValue("");

	}

	public abstract void init_loopEX();

	/**
	 * DO NOT CALL, for internal use only
	 */
	@Override
	public final void init_loop() {
		for (LynxModule module : allHubs) {
			module.clearBulkCache();
		}
		scheduler.pollSubsystemsPeriodic();
		scheduler.pollTriggers();
		init_loopEX();
		scheduler.pollCommands(OpModeEXRunStates.INIT_LOOP);
		gamepadEX1.endLoopUpdate();
		gamepadEX2.endLoopUpdate();
		telemetry.update();
	}

	public abstract void startEX();

	/**
	 * DO NOT CALL, for internal use only
	 */
	@Override
	public final void start() {
		telemetry.clear();
		startEX();
		elapsedTime.reset();
	}

	public abstract void loopEX();

	/**
	 * DO NOT CALL, for internal use only
	 */
	@Override
	public final void loop() {
		for (LynxModule module : allHubs) {
			module.clearBulkCache();
		}
		scheduler.pollSubsystemsPeriodic();
		scheduler.pollTriggers();
		loopEX();
		scheduler.pollCommands(OpModeEXRunStates.LOOP);
		gamepadEX1.endLoopUpdate();
		gamepadEX2.endLoopUpdate();
		telemetry.update();
	}

	public abstract void stopEX();

	/**
	 * DO NOT CALL, for internal use only
	 */
	@Override
	public final void stop() {
		stopEX();
		for (SubsystemInterface subsystem : scheduler.getSubsystems()) {
			subsystem.close();
		}
	}

	public enum OpModeEXRunStates {
		INIT_LOOP,
		LOOP;
	}
}
