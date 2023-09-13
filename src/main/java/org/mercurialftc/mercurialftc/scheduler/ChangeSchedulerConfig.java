package org.mercurialftc.mercurialftc.scheduler;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Autonomous(name = "Edit Scheduler Config Options")
public class ChangeSchedulerConfig extends OpModeEX {
	private Telemetry.Line instructions;
	private Telemetry.Item currentSettings;
	private int selection, selectionSize;
	private String selectionString;
	private TomlTable configTable;

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
					Scheduler.setBooleanConfigOption(selectionString, Boolean.FALSE.equals(configTable.getBoolean(selectionString)));
				})
		);
	}

	@Override
	public void init_loopEX() {

	}

	@Override
	public void startEX() {
		instructions = telemetry.addLine();
		currentSettings = telemetry.addData("", "");
		instructions.addData("use up and down on gamepad1's dpad to select the setting you want to change, then press a to change it", null);
	}

	@Override
	public void loopEX() {
		StringBuilder builder = new StringBuilder();
		TomlParseResult config = Scheduler.getConfig();
		configTable = config.getTableOrEmpty("configOptions");
		Set<Map.Entry<String, Object>> configSettings = configTable.dottedEntrySet(true);
		Iterator<Map.Entry<String, Object>> settingsIterator = configSettings.iterator();
		selectionSize = configSettings.size() - 1;
		for (int i = 0; i < configSettings.size(); i++) {
			Map.Entry<String, Object> entry = settingsIterator.next();
			builder.append(entry.getKey()).append(": ").append(entry.getValue().toString());
			if (selection == i) {
				selectionString = entry.getKey();
				builder.append(" <--");
			}
			builder.append("\n");
		}
		currentSettings.setCaption(builder.toString());
	}

	@Override
	public void stopEX() {

	}
}
