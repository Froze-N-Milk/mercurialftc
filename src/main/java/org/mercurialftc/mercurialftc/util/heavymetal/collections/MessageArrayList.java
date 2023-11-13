package org.mercurialftc.mercurialftc.util.heavymetal.collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.TraceMessage;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("unused")
public class MessageArrayList<E extends TraceMessage> extends ArrayList<E> {
	private int labelWidth;

	public MessageArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	public MessageArrayList() {
		super();
	}

	public MessageArrayList(@NonNull @NotNull Collection<? extends E> c) {
		super(c);
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	private void findLabelWidth() {
		for (E item : this) {
			labelWidth = Math.max(labelWidth, item.getLabelWidth());
		}
	}

	private void safeAdd(@NotNull E incoming) {
		labelWidth = Math.max(incoming.getLabelWidth(), labelWidth);
	}

	private void safeRemove(@NotNull E outgoing) {
		if (outgoing.getLabelWidth() == labelWidth) {
			findLabelWidth();
		}
	}

	@Override
	public E set(int index, E element) {
		E outgoing = this.get(index);
		E operation = super.set(index, element);
		safeRemove(outgoing);
		safeAdd(element);
		return operation;
	}

	@Override
	public boolean add(E e) {
		safeAdd(e);
		return super.add(e);
	}

	@Override
	public void add(int index, E element) {
		safeAdd(element);
		super.add(index, element);
	}

	@Override
	public E remove(int index) {
		E operation = super.remove(index);
		safeRemove(operation);
		return operation;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(@Nullable @org.jetbrains.annotations.Nullable Object o) {
		boolean operation = super.remove(o);
		if (operation && o != null) {
			safeRemove((E) o);
		}
		return operation;
	}

	@Override
	public boolean addAll(@NonNull @NotNull Collection<? extends E> c) {
		boolean operation = super.addAll(c);
		findLabelWidth();
		return operation;
	}

	@Override
	public boolean addAll(int index, @NonNull @NotNull Collection<? extends E> c) {
		boolean operation = super.addAll(index, c);
		findLabelWidth();
		return operation;
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		super.removeRange(fromIndex, toIndex);
		findLabelWidth();
	}

	@Override
	public boolean removeAll(@NonNull @NotNull Collection<?> c) {
		boolean operation = super.removeAll(c);
		findLabelWidth();
		return operation;
	}
}
