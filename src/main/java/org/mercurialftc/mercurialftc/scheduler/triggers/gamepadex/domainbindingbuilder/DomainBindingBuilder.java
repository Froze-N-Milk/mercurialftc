package org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex.domainbindingbuilder;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

@SuppressWarnings("unused")
public class DomainBindingBuilder<S extends DoubleSupplier> {
	private final S s;
	private final BooleanSupplier upperSupplier, lowerSupplier;
	private final double upper, lower;

	public DomainBindingBuilder(S s) {
		this(s, () -> s.getAsDouble() <= 1.0, () -> s.getAsDouble() >= -1.0, 1.0, -1.0);
	}

	private DomainBindingBuilder(S s, BooleanSupplier upperSupplier, BooleanSupplier lowerSupplier, double upper, double lower) {
		this.s = s;
		this.upperSupplier = upperSupplier;
		this.lowerSupplier = lowerSupplier;
		this.upper = upper;
		this.lower = lower;
	}

	public DomainBindingBuilder<S> lessThan(double value) {
		return new DomainBindingBuilder<>(s, () -> s.getAsDouble() < value, lowerSupplier, value, lower);
	}

	public DomainBindingBuilder<S> lessThanEqualTo(double value) {
		return new DomainBindingBuilder<>(s, () -> s.getAsDouble() <= value, lowerSupplier, value, lower);
	}

	public DomainBindingBuilder<S> greaterThan(double value) {
		return new DomainBindingBuilder<>(s, upperSupplier, () -> s.getAsDouble() > value, upper, value);
	}

	public DomainBindingBuilder<S> greaterThanEqualTo(double value) {
		return new DomainBindingBuilder<>(s, upperSupplier, () -> s.getAsDouble() >= value, upper, value);
	}

	public DomainBinding<S> bind() {
		if (upper >= lower) {
			return new DomainBinding<>(s, () -> upperSupplier.getAsBoolean() && lowerSupplier.getAsBoolean());
		}
		return new DomainBinding<>(s, () -> upperSupplier.getAsBoolean() || lowerSupplier.getAsBoolean());
	}
}
