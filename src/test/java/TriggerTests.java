import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX.OpModeEXRunStates;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.bindings.Binding;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;

public class TriggerTests {
	private Scheduler scheduler;
	private Binding testBinding;
	private boolean inputState, output;
	private LambdaCommand testCommand;

	@BeforeEach
	void init() {
		this.scheduler = Scheduler.freshInstance();
		scheduler.setRunState(OpModeEXRunStates.LOOP);
		this.inputState = false;
		this.output = false;
		this.testBinding = new Binding(() -> inputState);

		testCommand = new LambdaCommand()
				.setInit(() -> {
					output = true;
					System.out.println("init: " + true);
				})
				.setExecute(() -> System.out.println("executing: " + output))
				.setEnd((interrupted) -> {
					output = false;
					System.out.println("end: " + false);
				});
	}


	@Test
	void onTrue() {
		testBinding.onTrue(testCommand);

		test(true, true);

		test(true, false);

		test(true, false);

		test(true, false);

		test(true, false);

		test(true, false);

		test(false, false);

		test(true, true);

		test(false, false);
	}

	@Test
	void onFalse() {
		testBinding.onFalse(testCommand);

		test(true, false);

		test(false, true);

		test(false, false);

		test(false, false);

		test(false, false);

		test(false, false);

		test(false, false);

		test(true, false);

		test(false, true);

		test(true, false);
	}

	@Test
	void whileFalse() {
		testCommand = testCommand.setFinish(() -> false);
		testBinding.whileFalse(testCommand);

		test(true, false);

		test(false, true);

		test(false, true);

		test(false, true);

		test(false, true);

		test(false, true);

		test(false, true);

		test(true, false);

		test(true, false);
	}

	@Test
	void whileTrue() {
		testCommand = testCommand.setFinish(() -> false);
		testBinding.whileTrue(testCommand);

		test(true, true);

		test(true, true);

		test(true, true);

		test(true, true);

		test(true, true);

		test(true, true);

		test(false, false);

		test(false, false);
	}

	void test(boolean in, boolean expectedOut) {
		inputState = in;
		scheduler.preLoopUpdateBindings();
		scheduler.pollTriggers();
		scheduler.pollCommands();
		Assertions.assertEquals(expectedOut, output);
		scheduler.postLoopUpdateBindings();
	}
}
