package org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex.domainbindingbuilder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.DoubleSupplier;

@SuppressWarnings("unused")
public class DomainBindingBuilder<S extends DoubleSupplier> {
	private final S s;
	private final ArrayList<DomainChecker> domainCheckers;
	private DomainClosureBuilder domainClosureBuilder;
	private OperationType previousOperationType;

	public DomainBindingBuilder(S s) {
		this.s = s;
		this.domainCheckers = new ArrayList<>(1);
		this.domainClosureBuilder = new DomainClosureBuilder();
	}

	/**
	 * @return self, for chaining
	 */
	public DomainBindingBuilder<S> lessThan(double value) {
		handleBuildState(OperationType.LESS, value);
		domainClosureBuilder = domainClosureBuilder.lessThan(value);
		return this;
	}

	/**
	 * @return self, for chaining
	 */
	public DomainBindingBuilder<S> lessThanEqualTo(double value) {
		handleBuildState(OperationType.LESS, value);
		domainClosureBuilder = domainClosureBuilder.lessThanEqualTo(value);
		return this;
	}

	/**
	 * @return self, for chaining
	 */
	public DomainBindingBuilder<S> greaterThan(double value) {
		handleBuildState(OperationType.GREATER, value);
		domainClosureBuilder = domainClosureBuilder.greaterThan(value);
		return this;
	}

	// when we do a new operation, check to see if it can form a valid closure with the previous operation, if so, perform the closure union, else, close the previous closure and add this one in
	// closes if upper > lower
	// only need to check if we currently already have one domain set

	/**
	 * @return self, for chaining
	 */
	public DomainBindingBuilder<S> greaterThanEqualTo(double value) {
		handleBuildState(OperationType.GREATER, value);
		domainClosureBuilder = domainClosureBuilder.greaterThanEqualTo(value);
		return this;
	}

	public DomainBinding<S> bind() {
		if (domainClosureBuilder.lower != Double.NEGATIVE_INFINITY || domainClosureBuilder.upper != Double.POSITIVE_INFINITY) {
			domainCheckers.add(this.domainClosureBuilder.build());
		}

		// todo simplify domain checkers by checking their extremes and seeing if one entirely contains another or if two could be merged?
		// doesn't matter for the moment, but very plausible for later

		return new DomainBinding<>(s, () -> {
			boolean result = false;
			for (DomainChecker domainChecker : domainCheckers) {
				result |= domainChecker.getResult(s.getAsDouble());
			}
			return result;
		});
	}

	// we should perform a build if:
	// * we already performed an operation of this sign (less / greater)
	// * we already have one value loaded in there AND:
	// * the new value doesn't close, so we actually want inverse values, which we achieve by building the previous value and letting the user continue to cook
	// * OTHERWISE: if the new value DOES close, we add it and then run a build
	private void handleBuildState(OperationType operationType, double newValue) {
		if (previousOperationType == operationType || (operationType == OperationType.LESS && newValue < domainClosureBuilder.lower) || (operationType == OperationType.GREATER && domainClosureBuilder.upper < newValue)) {
			domainCheckers.add(this.domainClosureBuilder.build());
			domainClosureBuilder = new DomainClosureBuilder();
		}
		previousOperationType = operationType;
	}

	private enum OperationType {
		LESS,
		GREATER,
	}

	private interface DomainChecker {
		boolean getResult(double value);
	}

	private static class DomainClosureBuilder {
		private final double lower;
		private final double upper;
		private final boolean lowerInclusive, upperInclusive;

		private DomainClosureBuilder() {
			this(Double.NEGATIVE_INFINITY, true, Double.POSITIVE_INFINITY, true);
		}

		private DomainClosureBuilder(double lower, boolean lowerInclusive, double upper, boolean upperInclusive) {
			this.lower = lower;
			this.lowerInclusive = lowerInclusive;
			this.upper = upper;
			this.upperInclusive = upperInclusive;
		}

		@NotNull
		@Contract("_ -> new")
		DomainClosureBuilder lessThan(double value) {
			return new DomainClosureBuilder(this.lower, this.lowerInclusive, value, false);
		}

		@NotNull
		@Contract("_ -> new")
		DomainClosureBuilder lessThanEqualTo(double value) {
			return new DomainClosureBuilder(this.lower, this.lowerInclusive, value, true);
		}

		@NotNull
		@Contract("_ -> new")
		DomainClosureBuilder greaterThan(double value) {
			return new DomainClosureBuilder(value, false, this.upper, this.upperInclusive);
		}

		@NotNull
		@Contract("_ -> new")
		DomainClosureBuilder greaterThanEqualTo(double value) {
			return new DomainClosureBuilder(value, true, this.upper, this.upperInclusive);
		}

		@NotNull
		@Contract(pure = true)
		DomainChecker build() {
			return (value -> {
				boolean result = value > lower && value < upper;
				result |= lowerInclusive && value == lower;
				result |= upperInclusive && value == upper;
				return result;
			});
		}
	}
}
