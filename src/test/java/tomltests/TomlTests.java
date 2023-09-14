package tomltests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mercurialftc.mercurialftc.scheduler.configoptions.ConfigOptionsManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TomlTests {
	private ConfigOptionsManager configOptionsManager;
	private File tomlFile;

	@BeforeEach
	public void setUp() throws IOException {
		tomlFile = new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/config.toml");
		Assertions.assertFalse(tomlFile.exists());
		configOptionsManager = new ConfigOptionsManager(tomlFile, new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/defaultConfig.toml"));
	}

	@AfterEach
	public void cleanUp() {
		tomlFile.delete();
	}

	@Test
	public void guaranteeFiles() throws IOException {
		Assertions.assertTrue(ConfigOptionsManager.guaranteeFile(tomlFile));
		Assertions.assertTrue(ConfigOptionsManager.guaranteeFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/defaultConfig.toml")));
		Assertions.assertTrue(ConfigOptionsManager.guaranteeFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/blankDefaultConfig.toml")));
		Assertions.assertTrue(ConfigOptionsManager.guaranteeFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/wrongTypesDefaultConfig.toml")));
		Assertions.assertTrue(ConfigOptionsManager.guaranteeFile(tomlFile));
	}

	@Test
	public void ensureDefaultsAreMet() throws IOException {
		ConfigOptionsManager.write(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/blankDefaultConfig.toml"), tomlFile, new HashMap<>());

		Assertions.assertTrue(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/defaultConfig.toml")), "tomlFile was not updated to match new defaults");
		Assertions.assertFalse(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/defaultConfig.toml")), "tomlFile was updated despite defaults being set");

		Assertions.assertTrue(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/wrongTypesDefaultConfig.toml")), "tomlFile was not updated to match new defaults");
		Assertions.assertFalse(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/wrongTypesDefaultConfig.toml")), "tomlFile was updated despite defaults being set");

		Assertions.assertFalse(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/blankDefaultConfig.toml")), "tomlFile was updated despite minimum defaults being met");
		Assertions.assertFalse(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/blankDefaultConfig.toml")), "tomlFile was updated despite minimum defaults being met");

		Assertions.assertTrue(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/defaultConfig.toml")), "tomlFile was not updated to match new defaults");
		Assertions.assertFalse(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/defaultConfig.toml")), "tomlFile was updated despite defaults being set");

		Assertions.assertTrue(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/wrongTypesDefaultConfig.toml")), "tomlFile was not updated to match new defaults");
		Assertions.assertFalse(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/wrongTypesDefaultConfig.toml")), "tomlFile was updated despite defaults being set");

		Assertions.assertTrue(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/defaultConfig.toml")), "tomlFile was not updated to match new defaults");
		Assertions.assertFalse(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/defaultConfig.toml")), "tomlFile was updated despite defaults being set");
	}

	@Test
	public void modifyValues() throws IOException {
		boolean result;

		configOptionsManager.updateValue("schedulerRefreshEnabled", false);
		configOptionsManager.update();
		result = Boolean.TRUE.equals(configOptionsManager.getTomlParseResult().getBoolean("schedulerRefreshEnabled"));
		Assertions.assertFalse(result);

		configOptionsManager.updateValue("schedulerRefreshEnabled", true);
		configOptionsManager.update();
		result = Boolean.TRUE.equals(configOptionsManager.getTomlParseResult().getBoolean("schedulerRefreshEnabled"));
		Assertions.assertTrue(result);

		configOptionsManager.updateValue("loggingEnabled", false);
		configOptionsManager.update();
		result = Boolean.TRUE.equals(configOptionsManager.getTomlParseResult().getBoolean("loggingEnabled"));
		Assertions.assertFalse(result);

		configOptionsManager.updateValue("loggingEnabled", true);
		configOptionsManager.update();
		result = Boolean.TRUE.equals(configOptionsManager.getTomlParseResult().getBoolean("loggingEnabled"));
		Assertions.assertTrue(result);

	}

	@Test
	public void ensureDefaultsAreMetBReader() throws IOException {
		String defaultToml = "# this file is automatically generated and edited by mercurialftc's scheduler\n" +
				"# you may add more settings here and they will show up in the 'Edit Scheduler Config Options' OpMode that appears under the teleop list\n" +
				"# removing either of these two properties will cause the scheduler to remake this file with the default settings\n" +
				"\n" +
				"schedulerRefreshEnabled = true\n" +
				"loggingEnabled = false\n";

		BufferedReader breader = new BufferedReader(new StringReader(defaultToml));

		String line;
		
		while (breader.ready() && ((line = breader.readLine()) != null)) {
			System.out.println(line);
		}

//		configOptionsManager = new ConfigOptionsManager(tomlFile, defaultToml);
//
//
//		for (Map.Entry<String, Object> entry : configOptionsManager.getTomlParseResult().dottedEntrySet()) {
//			System.out.println(entry.getKey() + ": " + entry.getValue());
//		}
//
//		Assertions.assertFalse(configOptionsManager.defaultFile(new File("/Users/oscarchevalier/IdeaProjects/mercurialftc/src/test/java/tomltests/defaultConfig.toml")), "tomlFile was updated despite defaults being set");
	}
}
