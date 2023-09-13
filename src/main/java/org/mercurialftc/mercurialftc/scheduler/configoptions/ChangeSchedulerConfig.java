package org.mercurialftc.mercurialftc.scheduler.configoptions;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@TeleOp(name = "Edit Scheduler Config Options", group = "?") // we use '?' to move it to the bottom of the list
public class ChangeSchedulerConfig extends OpModeEX {
	private Telemetry.Item currentSettings;
	private int selection, selectionSize;
	private String selectionString;

	@Override
	public void registerSubsystems() {

	}

	@Override
	public void initEX() {
	}

	@Override
	public void registerTriggers() {
		gamepadEX1().dpad_up().onPress(
				new LambdaCommand().init(() -> {
					selection++;
					if (selection < 0) selection += selectionSize;
					selection %= selectionSize;
				})
		);
		gamepadEX1().dpad_down().onPress(
				new LambdaCommand().init(() -> {
					selection--;
					if (selection < 0) selection += selectionSize;
					selection %= selectionSize;
				})
		);
		gamepadEX1().a().onPress(
				new LambdaCommand().init(() -> {
					Scheduler.getConfigOptionsManager().updateValue(selectionString, Boolean.FALSE.equals(Scheduler.getConfigOptionsManager().getTomlParseResult().getBoolean(selectionString)));
					try {
						Scheduler.getConfigOptionsManager().update();
					} catch (IOException e) {
						throw new RuntimeException("failed to update settings: \n" + e);
					}
				})
		);
	}

	@Override
	public void init_loopEX() {

	}

	@Override
	public void startEX() {
		Telemetry.Line instructions = telemetry.addLine();
		currentSettings = telemetry.addData("", "");

		instructions.addData("use up and down on gamepad1's dpad to select the setting you want to change, then press a to change it", null);
	}

	@Override
	public void loopEX() {
		Set<Map.Entry<String, Object>> config = Scheduler.getConfigOptionsManager().getTomlParseResult().dottedEntrySet();
		selectionSize = config.size();

		StringBuilder configBuilder = new StringBuilder();
		Iterator<Map.Entry<String, Object>> configIterator = config.iterator();
		for (int i = 0; configIterator.hasNext(); i++) {
			Map.Entry<String, Object> configEntry = configIterator.next();
			configBuilder.append(configEntry.getKey()).append(": ").append(configEntry.getValue());
			if (selection == i) {
				configBuilder.append(" <--");
				selectionString = configEntry.getKey();
			}
			configBuilder.append("\n");
		}

		currentSettings.setCaption(configBuilder.toString());
	}

	@Override
	public void stopEX() {

	}
}
