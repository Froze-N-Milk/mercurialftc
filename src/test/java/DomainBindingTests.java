import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mercurialftc.mercurialftc.scheduler.bindings.gamepadex.DomainSupplier;
import org.mercurialftc.mercurialftc.scheduler.bindings.gamepadex.domainbindingbuilder.DomainBinding;

import java.util.HashMap;
import java.util.Map;

public class DomainBindingTests {
	double internalValue;
	DomainSupplier domainSupplier = new DomainSupplier(() -> internalValue);

	HashMap<DomainBinding<DomainSupplier>, HashMap<Double, Boolean>> testMappings = new HashMap<>();

	@Test
	void simpleDomains() {
		testMappings.put(
				domainSupplier.buildBinding()
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, false);
					put(Double.POSITIVE_INFINITY, false);
					put(0.0, false);
				}}
		);

		testMappings.put(
				domainSupplier.buildBinding()
						.lessThan(0.0)
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, true);
					put(Double.POSITIVE_INFINITY, false);
					put(-10.0, true);
					put(-1.0, true);
					put(-0.1, true);
					put(-0.0001, true);
					put(0.0, false);
					put(1.0, false);
					put(10.0, false);
				}}
		);

		testMappings.put(
				domainSupplier.buildBinding()
						.lessThan(0.0)
						.greaterThanEqualTo(-10.0)
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, false);
					put(Double.POSITIVE_INFINITY, false);
					put(-10.0, true);
					put(-1.0, true);
					put(-0.1, true);
					put(-0.0001, true);
					put(0.0, false);
					put(1.0, false);
					put(10.0, false);
				}}
		);

		testMappings.put(
				domainSupplier.buildBinding()
						.greaterThanEqualTo(-10.0)
						.greaterThan(-10.01)
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, false);
					put(Double.POSITIVE_INFINITY, true);
					put(-10.01, false);
					put(-10.0, true);
					put(-1.0, true);
					put(-0.1, true);
					put(-0.0001, true);
					put(0.0, true);
					put(1.0, true);
					put(10.0, true);
				}}
		);

		testMappings.put(
				domainSupplier.buildBinding()
						.greaterThanEqualTo(-10.0)
						.lessThanEqualTo(10.0)
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, false);
					put(Double.POSITIVE_INFINITY, false);
					put(-10.01, false);
					put(-10.0, true);
					put(-1.0, true);
					put(-0.1, true);
					put(-0.0001, true);
					put(0.0, true);
					put(1.0, true);
					put(10.0, true);
					put(10.01, false);
				}}
		);

		testMappings.put(
				domainSupplier.buildBinding()
						.lessThanEqualTo(10.0)
						.greaterThanEqualTo(-10.0)
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, false);
					put(Double.POSITIVE_INFINITY, false);
					put(-10.01, false);
					put(-10.0, true);
					put(-1.0, true);
					put(-0.1, true);
					put(-0.0001, true);
					put(0.0, true);
					put(1.0, true);
					put(10.0, true);
					put(10.01, false);
				}}
		);

		testMappings.put(
				domainSupplier.buildBinding()
						.greaterThanEqualTo(10.0)
						.lessThanEqualTo(-10.0)
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, true);
					put(Double.POSITIVE_INFINITY, true);
					put(-10.01, true);
					put(-10.0, true);
					put(-1.0, false);
					put(-0.1, false);
					put(-0.0001, false);
					put(0.0, false);
					put(1.0, false);
					put(10.0, true);
					put(10.01, true);
				}}
		);

		testMappings.put(
				domainSupplier.buildBinding()
						.lessThanEqualTo(-10.0)
						.greaterThanEqualTo(10.0)
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, true);
					put(Double.POSITIVE_INFINITY, true);
					put(-10.01, true);
					put(-10.0, true);
					put(-1.0, false);
					put(-0.1, false);
					put(-0.0001, false);
					put(0.0, false);
					put(1.0, false);
					put(10.0, true);
					put(10.01, true);
				}}
		);

		testMappings.put(
				domainSupplier.buildBinding()
						.lessThan(0.0)
						.greaterThan(0.0)
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, true);
					put(Double.POSITIVE_INFINITY, true);
					put(0.0, false);
				}}
		);

	}

	@Test
		// no user should ever need to use this to this depth, but they might
	void abusingTheSystem() {
		testMappings.put(
				domainSupplier.buildBinding()
						.lessThanEqualTo(-100.0)

						.greaterThan(-90.0)
						.lessThan(-80.0)

						.greaterThanEqualTo(-70.0)
						.lessThanEqualTo(80.0)

						.greaterThanEqualTo(100.0)
						.bind(),
				new HashMap<Double, Boolean>() {{
					put(Double.NEGATIVE_INFINITY, true);
					put(Double.POSITIVE_INFINITY, true);

					put(-100.0, true);
					put(-90.0, false);
					put(-85.0, true);
					put(-80.0, false);
					put(-70.0, true);
					put(0.0, true);
					put(80.0, true);
					put(90.0, false);
					put(100.0, true);

				}}
		);
	}

	@AfterEach
	void testBindings() {
		for (Map.Entry<DomainBinding<DomainSupplier>, HashMap<Double, Boolean>> testMap : testMappings.entrySet()) {
			for (Map.Entry<Double, Boolean> test : testMap.getValue().entrySet()) {
				testMap.getKey().postLoopUpdate();
				internalValue = test.getKey();
				testMap.getKey().preLoopUpdate();
				Assertions.assertEquals(test.getValue(), testMap.getKey().state(), String.format("expected %b, but got %b for a value of %f", test.getValue(), testMap.getKey().state(), internalValue));
			}
		}

		testMappings.clear();
	}
}
