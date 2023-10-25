package org.mercurialftc.mercurialftc.scheduler.bindings.gamepadex.domainbindingbuilder;

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
		handleBuildState(OperationType.LESS, Inclusivity.NOT_INCLUSIVE, value);
		domainClosureBuilder = domainClosureBuilder.lessThan(value);
		return this;
	}

	/**
	 * @return self, for chaining
	 */
	public DomainBindingBuilder<S> lessThanEqualTo(double value) {
		handleBuildState(OperationType.LESS, Inclusivity.INCLUSIVE, value);
		domainClosureBuilder = domainClosureBuilder.lessThanEqualTo(value);
		return this;
	}

	/**
	 * @return self, for chaining
	 */
	public DomainBindingBuilder<S> greaterThan(double value) {
		handleBuildState(OperationType.GREATER, Inclusivity.NOT_INCLUSIVE, value);
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
		handleBuildState(OperationType.GREATER, Inclusivity.INCLUSIVE, value);
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
	private void handleBuildState(OperationType operationType, Inclusivity inclusivity, double newValue) {
		if (previousOperationType == operationType || (operationType == OperationType.LESS && (newValue < domainClosureBuilder.lower && inclusivity.isInclusive() || newValue <= domainClosureBuilder.lower && !inclusivity.isInclusive())) || (operationType == OperationType.GREATER && (domainClosureBuilder.upper < newValue && inclusivity.isInclusive() || domainClosureBuilder.upper <= newValue && !inclusivity.isInclusive()))) {
			domainCheckers.add(this.domainClosureBuilder.build());
			domainClosureBuilder = new DomainClosureBuilder();
		}
		previousOperationType = operationType;
	}

	private enum OperationType {
		LESS,
		GREATER,
	}

	private enum Inclusivity {
		INCLUSIVE(true),
		NOT_INCLUSIVE(false);

		private final boolean inclusive;

		Inclusivity(boolean inclusive) {
			this.inclusive = inclusive;
		}

		public boolean isInclusive() {
			return inclusive;
		}
	}

	private interface DomainChecker {
		boolean getResult(double value);
	}

	private static class DomainClosureBuilder {
		private final double lower;
		private final double upper;
		private final Inclusivity lowerInclusive, upperInclusive;

		private DomainClosureBuilder() {
			this(Double.NEGATIVE_INFINITY, Inclusivity.INCLUSIVE, Double.POSITIVE_INFINITY, Inclusivity.INCLUSIVE);
		}

		private DomainClosureBuilder(double lower, Inclusivity lowerInclusive, double upper, Inclusivity upperInclusive) {
			this.lower = lower;
			this.lowerInclusive = lowerInclusive;
			this.upper = upper;
			this.upperInclusive = upperInclusive;
		}

		@NotNull
		@Contract("_ -> new")
		DomainClosureBuilder lessThan(double value) {
			return new DomainClosureBuilder(this.lower, this.lowerInclusive, value, Inclusivity.NOT_INCLUSIVE);
		}

		@NotNull
		@Contract("_ -> new")
		DomainClosureBuilder lessThanEqualTo(double value) {
			return new DomainClosureBuilder(this.lower, this.lowerInclusive, value, Inclusivity.INCLUSIVE);
		}

		@NotNull
		@Contract("_ -> new")
		DomainClosureBuilder greaterThan(double value) {
			return new DomainClosureBuilder(value, Inclusivity.NOT_INCLUSIVE, this.upper, this.upperInclusive);
		}

		@NotNull
		@Contract("_ -> new")
		DomainClosureBuilder greaterThanEqualTo(double value) {
			return new DomainClosureBuilder(value, Inclusivity.INCLUSIVE, this.upper, this.upperInclusive);
		}

		@NotNull
		@Contract(pure = true)
		DomainChecker build() {
			return (value -> {
				boolean result = value > lower && value < upper;
				result |= lowerInclusive.isInclusive() && value == lower;
				result |= upperInclusive.isInclusive() && value == upper;
				return result;
			});
		}
	}
}
