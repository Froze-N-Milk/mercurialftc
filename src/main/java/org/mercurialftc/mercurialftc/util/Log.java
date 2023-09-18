package org.mercurialftc.mercurialftc.util;

import android.os.Environment;
import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

public class Log {
	private final long startTime;
	private final String now;

	private final String[] dataLine;

	private final LinkedHashMap<String, Integer> dataHeadings;
	private FileWriter fileWriter;

	public Log(String system, int storedLogs, @NotNull String... dataHeadings) {
		this.startTime = System.nanoTime();
		this.now = String.valueOf(System.nanoTime() / 1E9);
		this.dataHeadings = new LinkedHashMap<>();
		this.dataHeadings.put("ElapsedTime", 0);
		int i = 1;
		for (String heading :
				dataHeadings) {
			this.dataHeadings.put(heading, i);
			i++;
		}
		this.dataLine = new String[dataHeadings.length + 1];

		if (!Scheduler.isLoggingEnabled()) {
			return;
		}

		String directoryPath = clearToDirectoryPath(system, storedLogs);
		try {
			fileWriter = new FileWriter(directoryPath + "/" + system + "0.csv", true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		createHeadings();
	}

	public Log(String subsystem, String... dataHeadings) {
		this(subsystem, 6, dataHeadings);
	}

	/**
	 * removes oldest log and shifts all the others down one
	 *
	 * @param system     name of the logFIle
	 * @param storedLogs number of logs to be stored
	 * @return the path to the log folder
	 */
	@NotNull
	private static String clearToDirectoryPath(String system, int storedLogs) {
		String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/FIRST/mercurialftc/logs/" + system;
		File directory = new File(directoryPath);
		directory.mkdirs();

		File outdatedLog = new File(directoryPath + "/" + system + (storedLogs - 1) + ".csv");
		outdatedLog.delete();
		for (int i = storedLogs; i > 0; i--) {
			File oldLog = new File(directoryPath + "/" + system + (i - 1) + ".csv");
			File oldLogDestination = new File(directoryPath + "/" + system + (i) + ".csv");
			oldLog.renameTo(oldLogDestination);
		}
		return directoryPath;
	}

	public void updateLoop(boolean storeTime) {
		if (!Scheduler.isLoggingEnabled()) {
			return;
		}
		double elapsedTime = ((System.nanoTime() - startTime) / 1E9);
		StringBuilder dataWrite = new StringBuilder();
		if (storeTime) {
			dataLine[0] = String.valueOf(elapsedTime);
		}
		for (int i = 0; i < dataLine.length; i++) {
			dataWrite.append(dataLine[i]);
			if (i != dataLine.length - 1) {
				dataWrite.append(",");
			}
		}

		try {
			fileWriter.write(dataWrite + "\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param dataHeading a previously set data heading
	 * @param data        the data object to store, Object.toString is automatically called on it
	 */
	public void logData(String dataHeading, Object data) {
		if (!Scheduler.isLoggingEnabled()) {
			return;
		}
		Integer dataHeadingArrayPosition = dataHeadings.get(dataHeading);
		if (dataHeadingArrayPosition == null) return;
		dataLine[dataHeadingArrayPosition] = data.toString();
	}

	public void close() {
		if (!Scheduler.isLoggingEnabled()) {
			return;
		}
		try {
			fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void createHeadings() {
		if (!Scheduler.isLoggingEnabled()) {
			return;
		}
		try {
			fileWriter.write(now + "\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int i = 0;
		for (String heading : dataHeadings.keySet()) {
			dataLine[i] = heading;
			i++;
		}

		updateLoop(false);
	}
}
