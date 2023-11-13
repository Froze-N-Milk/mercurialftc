package org.mercurialftc.mercurialftc.util.heavymetal.collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class MessageBoardQueue<T> extends AbstractQueue<T> {
	private final T[] array;
	private int currentLen, startIndex;

	@SuppressWarnings("unchecked")
	public MessageBoardQueue(int capacity) {
		this.currentLen = startIndex = 0;
		this.array = (T[]) new Object[capacity];
	}

	@NonNull
	@NotNull
	@Override
	public Iterator<T> iterator() {
		return new TraceMessageBoardIterator<>(this);
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
		currentLen++;
		if (currentLen >= this.array.length) {
			currentLen = this.array.length;
			startIndex++;
			startIndex %= this.array.length;
		}
		this.array[getEndIndex()] = t;
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

	public static class TraceMessageBoardIterator<T> implements Iterator<T> {
		private final MessageBoardQueue<T> queue;
		private final int startIndex;
		private int index;

		public TraceMessageBoardIterator(@NotNull MessageBoardQueue<T> queue) {
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
}