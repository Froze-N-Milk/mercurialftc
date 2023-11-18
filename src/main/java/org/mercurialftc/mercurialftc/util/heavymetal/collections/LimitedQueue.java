package org.mercurialftc.mercurialftc.util.heavymetal.collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class LimitedQueue<T> extends AbstractQueue<T> {
	private final T[] array;
	private int currentLen, startIndex;

	@SuppressWarnings("unchecked")
	public LimitedQueue(int capacity) {
		this.currentLen = startIndex = 0;
		this.array = (T[]) new Object[capacity];
	}

	@NonNull
	@NotNull
	@Override
	public Iterator<T> iterator() {
		return new LimitedQueueIterator<>(this);
	}

	@NonNull
	@NotNull
	public Iterator<T> reverseIterator() {
		return new ReverseLimitedQueueIterator<>(this);
	}

	private int getEndIndex() {
		return (startIndex + currentLen) % this.array.length;
	}

	@Override
	public int size() {
		return currentLen;
	}

	@Override
	public boolean offer(T t) {
		if (t == null) return false;
		this.array[getEndIndex()] = t;
		currentLen++;
		if (currentLen > this.array.length) {
			currentLen = this.array.length;
			startIndex++;
			startIndex %= this.array.length;
		}
		return true;
	}

	@Nullable
	@org.jetbrains.annotations.Nullable
	@Override
	public T poll() {
		T result = this.array[startIndex];
		this.array[startIndex] = null;
		currentLen--;
		currentLen = Math.max(0, currentLen);
		startIndex++;
		startIndex %= this.array.length;
		return result;
	}

	@Nullable
	@org.jetbrains.annotations.Nullable
	@Override
	public T peek() {
		return this.array[startIndex];
	}

	public static class LimitedQueueIterator<T> implements Iterator<T> {
		private final LimitedQueue<T> queue;
		private final int startIndex;
		private int index;

		public LimitedQueueIterator(@NotNull LimitedQueue<T> queue) {
			this.queue = queue;
			this.startIndex = queue.startIndex;
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			return index < queue.size();
		}

		@Override
		public T next() {
			return queue.array[(startIndex + index++) % queue.size()];
		}
	}

	public static class ReverseLimitedQueueIterator<T> implements Iterator<T> {
		private final LimitedQueue<T> queue;
		private final int startIndex;
		private int index;

		public ReverseLimitedQueueIterator(@NotNull LimitedQueue<T> queue) {
			this.queue = queue;
			int startIndex = queue.getEndIndex() - 1;
			if (startIndex < 0) startIndex += queue.size();
			this.startIndex = startIndex;
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			return Math.abs(index) < queue.size();
		}

		@Override
		public T next() {
			int i = (startIndex + index--) % queue.size();
			if (i < 0) i += queue.size();
			return queue.array[i];
		}
	}
}