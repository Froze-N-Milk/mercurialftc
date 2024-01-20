package org.mercurialftc.mercurialftc.scheduler.bindings.gamepadex.domainbindingbuilder;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.bindings.Binding;
import org.mercurialftc.mercurialftc.scheduler.bindings.gamepadex.DomainSupplier;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;

import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public class DomainBinding extends Binding {
	private final DomainSupplier domainSupplier;

	public DomainBinding(DomainSupplier domainSupplier, BooleanSupplier internalInput) {
		super(internalInput);
		this.domainSupplier = domainSupplier;
	}

	public DomainSupplier endBinding() {
		return domainSupplier;
	}

	@Override
	public DomainBinding whileTrue(@NotNull Command toRun) {
		super.whileTrue(toRun);
		return this;
	}

	@Override
	public DomainBinding whileFalse(@NotNull Command toRun) {
		super.whileFalse(toRun);
		return this;
	}

	@Override
	public DomainBinding onTrue(@NotNull Command toRun) {
		super.onTrue(toRun);
		return this;
	}

	@Override
	public DomainBinding toggleTrue(@NotNull Command toRun) {
		super.toggleTrue(toRun);
		return this;
	}

	@Override
	public DomainBinding toggleFalse(@NotNull Command toRun) {
		super.toggleFalse(toRun);
		return this;
	}

	@Override
	public DomainBinding onFalse(@NotNull Command toRun) {
		super.onFalse(toRun);
		return this;
	}

	@Override
	public DomainBinding debounce(@NotNull Binding.DebouncingType type, double duration) {
		super.debounce(type, duration);
		return this;
	}

	@Override
	public DomainBinding and(BooleanSupplier andSupplier) {
		super.and(andSupplier);
		return this;
	}

	@Override
	public DomainBinding or(BooleanSupplier orSupplier) {
		super.or(orSupplier);
		return this;
	}
}
