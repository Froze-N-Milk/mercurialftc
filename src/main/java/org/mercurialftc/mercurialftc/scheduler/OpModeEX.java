package org.mercurialftc.mercurialftc.scheduler;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;
import org.mercurialftc.mercurialftc.scheduler.bindings.gamepadex.GamepadEX;
import org.mercurialftc.mercurialftc.util.heavymetal.HeavyMetal;
import org.mercurialftc.mercurialftc.util.heavymetal.TraceComponentRenderer;

import java.util.List;

@SuppressWarnings("unused")
public abstract class OpModeEX extends OpMode {
	private GamepadEX
			gamepadEX1,
			gamepadEX2;

	private Scheduler scheduler;

	private List<LynxModule> allHubs;

	private ElapsedTime elapsedTime;

	private HeavyMetal heavyMetal;

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

	public HeavyMetal getHeavyMetal() {
		return heavyMetal;
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
	 * registers bindings after the subsystem and regular init code,
	 * useful for organisation of your OpModeEX, but functionally no different to initialising them at the end of {@link #initEX()}
	 */
	public abstract void registerBindings();

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
		initialising.setValue("Preregistration");
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
		initialising.setValue("Robot");
		telemetry.update();

		initEX();
		registerBindings();

		initialisationSequencer.append("\nRobot");
		initialisedSubsystems.setValue(initialisationSequencer);
		telemetry.update();

		initialising.setValue("");
		scheduler.setRunState(OpModeEXRunStates.INIT_LOOP);

		HeavyMetal heavyMetal = new HeavyMetal(telemetry, TraceComponentRenderer.RenderOrder.getDefaultMapping());
		heavyMetal.findTraces(this, this.getClass());
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
		scheduler.preLoopUpdateBindings();
		scheduler.pollSubsystemsPeriodic();
		scheduler.pollTriggers();
		init_loopEX();
		scheduler.pollCommands();
		scheduler.postLoopUpdateBindings();
		heavyMetal.update();
		telemetry.update();
	}

	public abstract void startEX();

	/**
	 * DO NOT CALL, for internal use only
	 */
	@Override
	public final void start() {
		telemetry.clear();
		elapsedTime.reset();
		scheduler.setRunState(OpModeEXRunStates.LOOP);
		startEX();
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
		scheduler.preLoopUpdateBindings();
		scheduler.pollSubsystemsPeriodic();
		scheduler.pollTriggers();
		loopEX();
		scheduler.pollCommands();
		scheduler.postLoopUpdateBindings();
		heavyMetal.update();
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
