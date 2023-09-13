package org.mercurialftc.mercurialftc.scheduler;

import android.os.Environment;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Autonomous(name = "Edit Scheduler Config")
public class ChangeSchedulerConfig extends OpModeEX {
	private Telemetry.Line instructions, currentSettings;
	private int selection, selectionSize;

	@Override
	public void registerSubsystems() {

	}

	@Override
	public void initEX() {
		instructions = telemetry.addLine();
	}

	@Override
	public void registerTriggers() {
		gamepadEX1().dpad_up().onPress(
				new LambdaCommand().init(() -> {
					selection++;
					selection %= selectionSize;
				})
		);
	}

	@Override
	public void init_loopEX() {

	}

	@Override
	public void startEX() {
		instructions.addData("use up and down on gamepad1's dpad to select the setting you want to change, then press a to change it", null);
	}

	@Override
	public void loopEX() {
		StringBuilder builder = new StringBuilder();
		TomlParseResult config = getConfig();
		TomlTable configTable = config.getTableOrEmpty("");
		Set<Map.Entry<String, Object>> configSettings = configTable.dottedEntrySet(true);
		Iterator<Map.Entry<String, Object>> settingsIterator = configSettings.iterator();
		selectionSize = configSettings.size() - 1;
		for (int i = 0; i < configSettings.size(); i++) {
			Map.Entry<String, Object> entry = settingsIterator.next();
			builder.append(entry.getKey()).append(": ").append(entry.getValue().toString());
			if (selection == i) {
				builder.append(" <--");
			}
			builder.append("\n");
		}
		currentSettings.addData(builder.toString(), null);
	}

	@Override
	public void stopEX() {

	}

	private TomlParseResult getConfig() {
		// all the required checks to ensure this exists have already been done by the scheduler
		String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/FIRST/mercurialftc/";
		File configFile = new File(directoryPath, "config.toml");
		try {
			return Toml.parse(new FileReader(configFile));
		} catch (IOException e) {
			throw new RuntimeException("Error reading scheduler config");
		}
	}

	/**
	 * inverts the boolean found at selection
	 */
	private void changeValueAt(Set<Map.Entry<String, Object>> configSettings) {
		// all the required checks to ensure this exists have already been done by the scheduler
		String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/FIRST/mercurialftc/";
		File configFile = new File(directoryPath, "config.toml");
		try {
			FileWriter writer = new FileWriter(configFile);
			StringBuilder builder = new StringBuilder();
			Iterator<Map.Entry<String, Object>> settingsIterator = configSettings.iterator();
			for (int i = 0; i < configSettings.size(); i++) {
				Map.Entry<String, Object> entry = settingsIterator.next();
				builder.append(entry.getKey()).append(" = ");
				if (selection == i) {
					builder.append(!((boolean) entry.getValue()));
				} else {
					builder.append("\"").append(entry.getValue()).append("\"");
				}
				builder.append("\n");
			}
			writer.write(builder.toString());
			writer.close();
			Scheduler.configFiles();
		} catch (IOException e) {
			throw new RuntimeException("Error writing to the scheduler config");
		}
	}
}
