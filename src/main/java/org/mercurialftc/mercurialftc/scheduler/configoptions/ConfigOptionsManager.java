package org.mercurialftc.mercurialftc.scheduler.configoptions;

import org.jetbrains.annotations.NotNull;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * currently does not support inline arrays or tables, used for the scheduler config internally, can be used for a team's own permanent setting solutions
 * probably does not support tables or arrays at all tbh, but is much closer to regular ones than inline ones
 */
public class ConfigOptionsManager {
	private final File tomlFile;
	private final Map<String, Object> changes;
	private TomlParseResult tomlParseResult;

	/**
	 * @param tomlFile the file to read
	 * @throws RuntimeException will be thrown if tomlFile has not yet been made, use {@link #guaranteeFile(File)} to do so before calling this constructor
	 */
	public ConfigOptionsManager(@NotNull File tomlFile) throws IOException {
		if (!tomlFile.isFile()) {
			throw new RuntimeException(String.format("supplied tomlFile with path %s is not a file, and is probably a directory", tomlFile.getPath()));
		}
		this.tomlFile = tomlFile;
		tomlParseResult = read(tomlFile);
		changes = new HashMap<>();
	}

	/**
	 * much safer than the no default file constructor
	 *
	 * @param tomlFile    the file to read
	 * @param defaultFile a default file to populate from
	 */
	public ConfigOptionsManager(@NotNull File tomlFile, @NotNull File defaultFile) throws IOException {
		this.tomlFile = tomlFile;
		defaultFile(defaultFile);
		if (!tomlFile.isFile()) {
			throw new RuntimeException(String.format("could not create and defaultly format tomlFile at: %s", tomlFile.getPath()));
		}
		tomlParseResult = read(tomlFile);
		changes = new HashMap<>();
	}

	/**
	 * much safer than the no default file constructor
	 *
	 * @param tomlFile    the file to read
	 * @param defaultFile a default file to populate from
	 */
	public ConfigOptionsManager(@NotNull File tomlFile, @NotNull String defaultFile) throws IOException {
		this.tomlFile = tomlFile;
		defaultFile(defaultFile);
		if (!tomlFile.isFile()) {
			throw new RuntimeException(String.format("could not create and defaultly format tomlFile at: %s", tomlFile.getPath()));
		}
		tomlParseResult = read(tomlFile);
		changes = new HashMap<>();
	}

	/**
	 * loads the info from the file
	 *
	 * @return a TomlParseResult representing the data in the file
	 */
	@NotNull
	public static TomlParseResult read(File tomlFile) throws IOException {
		return Toml.parse(new BufferedReader(new FileReader(tomlFile)));
	}

	/**
	 * loads the info from the string
	 *
	 * @return a TomlParseResult representing the data in the file
	 */
	@NotNull
	public static TomlParseResult read(String tomlFile) throws IOException {
		return Toml.parse(tomlFile);
	}

	public static void write(File readTomlFile, @NotNull File writeTomlFile, @NotNull Map<String, Object> changes) throws IOException {
		BufferedReader bReader1 = new BufferedReader(new FileReader(readTomlFile));
		BufferedReader bReader2 = new BufferedReader(new FileReader(readTomlFile));

		writeInternal(writeTomlFile, changes, bReader1, bReader2);
	}

	public static void write(String readTomlString, @NotNull File writeTomlFile, @NotNull Map<String, Object> changes) throws IOException {
		BufferedReader bReader1 = new BufferedReader(new StringReader(readTomlString));
		BufferedReader bReader2 = new BufferedReader(new StringReader(readTomlString));
		writeInternal(writeTomlFile, changes, bReader1, bReader2);
	}

	private static void writeInternal(@NotNull File writeTomlFile, @NotNull Map<String, Object> changes, BufferedReader bReader1, BufferedReader bReader2) throws IOException {
		TomlParseResult tomlParseResult = Toml.parse(bReader1);

		File tempFile = File.createTempFile("tmp" + writeTomlFile.getName().split("\\.")[0], ".toml", writeTomlFile.getParentFile());

		BufferedWriter bWriter = new BufferedWriter(new FileWriter(tempFile));

		Map<Integer, String> changedLines = new HashMap<>();

		for (String dottedKey : changes.keySet()) {
			changedLines.put(Objects.requireNonNull(tomlParseResult.inputPositionOf(dottedKey)).line() - 1, dottedKey);
		}

		for (int i = 0; bReader2.ready(); i++) {
			String line = bReader2.readLine();

			if (changedLines.containsKey(i)) {
				String dottedKey = changedLines.get(i);
				bWriter.write(dottedKey);
				bWriter.write(" = ");

				if (!tomlParseResult.isString(dottedKey)) {
					bWriter.write(Objects.requireNonNull(changes.get(dottedKey)).toString());
				} else {
					bWriter.write("\"");
					String toEscape = Objects.requireNonNull(changes.get(dottedKey)).toString();
					String escaped = toEscape.replace("\\", "\\\\")
							.replace("\t", "\\t")
							.replace("\b", "\\b")
							.replace("\n", "\\n")
							.replace("\r", "\\r")
							.replace("\f", "\\f")
							.replace("\"", "\\\"");
					;
					bWriter.write(escaped);
					bWriter.write("\"");
				}
			} else {
				bWriter.write(line);
			}
			bWriter.newLine();
		}

		changes.clear();

		bReader2.close();
		bWriter.close();

		writeTomlFile.delete();
		tempFile.renameTo(writeTomlFile);
	}

	/**
	 * ensures that the file exists by making its directories, then it and then returning true that it exists and is a file
	 *
	 * @return true if the file exists and is a file
	 * @throws IOException
	 */
	public static boolean guaranteeFile(File tomlFile) throws IOException {
		Objects.requireNonNull(tomlFile.getParentFile()).mkdirs();
		tomlFile.createNewFile();
		return tomlFile.isFile();
	}

	/**
	 * updated when {@link #update()} is called
	 *
	 * @return the TomlParseResult of the file supplied to the constructor
	 */
	public TomlParseResult getTomlParseResult() {
		return tomlParseResult;
	}

	public void update() throws IOException {
		write(tomlFile, tomlFile, changes); //write the current changes
		tomlParseResult = read(tomlFile); //update the parse
	}

	/**
	 * updates are applied when {@link #update()} is run
	 *
	 * @param dottedKey dottedKey of the field to update
	 * @param value     the new value for the field
	 */
	public void updateValue(String dottedKey, Object value) {
		if (!tomlParseResult.contains(dottedKey)) {
			throw new RuntimeException(String.format("invalid dottedKey: %s", dottedKey));
		}

		changes.put(dottedKey, value);
	}

	/**
	 * <p>ensures that tomlFile exists, and has all the fields specified in defaultTomlFile and of the correct type</p>
	 * <p>will use {@link #guaranteeFile(File)} to ensure that tomlFile exists.</p>
	 * <p>will copy defaultTomlFile into the newly made tomlFile if it didn't exist</p>
	 * <p>will reset tomlFile to be defaultTomlFile if it exists but does not have all the fields of the correct type</p>
	 *
	 * @param defaultTomlFile the file that tomlFile should have the features of, this file will not be modified
	 * @return true if the file must be reset
	 */
	public boolean defaultFile(File defaultTomlFile) throws IOException {
		guaranteeFile(tomlFile);
		boolean write = false;

		Set<Map.Entry<String, Object>> defaultMapSet = read(defaultTomlFile).entrySet();

		tomlParseResult = read(tomlFile);

		for (Map.Entry<String, Object> defaultEntry : defaultMapSet) {
			write |= !tomlParseResult.contains(defaultEntry.getKey());
			Object value = tomlParseResult.get(defaultEntry.getKey());
			write |= !(value != null && value.getClass().equals(defaultEntry.getValue().getClass()));
		}

		if (write) {
			write(defaultTomlFile, tomlFile, new HashMap<>());
		}

		return write;
	}

	/**
	 * <p>ensures that tomlFile exists, and has all the fields specified in defaultTomlString and of the correct type</p>
	 * <p>will use {@link #guaranteeFile(File)} to ensure that tomlFile exists.</p>
	 * <p>will copy defaultTomlString into the newly made tomlFile if it didn't exist</p>
	 * <p>will reset tomlFile to be defaultTomlString if it exists but does not have all the fields of the correct type</p>
	 *
	 * @param defaultTomlString the string that tomlFile should have the features of, this string will not be modified
	 * @return true if the file was reset
	 */
	public boolean defaultFile(String defaultTomlString) throws IOException {
		guaranteeFile(tomlFile);
		boolean write = false;

		Set<Map.Entry<String, Object>> defaultMapSet = read(defaultTomlString).entrySet();

		tomlParseResult = read(tomlFile);

		for (Map.Entry<String, Object> defaultEntry : defaultMapSet) {
			write |= !tomlParseResult.contains(defaultEntry.getKey());
			Object value = tomlParseResult.get(defaultEntry.getKey());
			write |= !(value != null && value.getClass().equals(defaultEntry.getValue().getClass()));
		}

		if (write) {
			write(defaultTomlString, tomlFile, new HashMap<>());
		}

		return write;
	}
}
