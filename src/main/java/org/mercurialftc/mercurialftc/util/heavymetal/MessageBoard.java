package org.mercurialftc.mercurialftc.util.heavymetal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.LimitedQueue;

import java.util.Iterator;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class MessageBoard implements TraceComponent {
	private final LimitedQueue<TraceMessage> messageQueue;
	private String cachedBuild;

	public MessageBoard(LimitedQueue<TraceMessage> messageQueue) {
		this.messageQueue = messageQueue;
		this.cachedBuild = "";
	}

	protected LimitedQueue<TraceMessage> getMessageQueue() {
		return messageQueue;
	}

	@NotNull
	protected String rebuild() {
		StringBuilder builder = new StringBuilder();
		for (TraceMessage line : messageQueue) {
			builder.append(line).append("\n");
		}
		return builder.toString();
	}

	@NotNull
	@Override
	public String toString() {
		return cachedBuild;
	}

	public void add(TraceMessage message) {
		messageQueue.offer(message);
		this.cachedBuild = rebuild();
	}

	public static class Builder implements TraceComponentBuilder {
		int length = 0;

		@Override
		public void add(TraceMessage message) {
			length++;
		}

		@Nullable
		@Override
		public TraceComponent build(Object settings) {
			Settings castSettings = (Settings) settings;
			if (length == 0) return null;
			if (castSettings.len != 0) length = castSettings.len;
			else {
				length += 2;
			}
			if (castSettings.reversed) return new ReversedMessageBoard(new LimitedQueue<>(length));
			return new MessageBoard(new LimitedQueue<>(length));
		}
	}

	public static class Settings {
		private final boolean reversed;
		private final int len;

		public Settings(boolean reversed, int len) {
			this.reversed = reversed;
			this.len = len;
		}

		public Settings(boolean reversed) {
			this(reversed, 0);
		}

		public Settings(int len) {
			this(true, len);
		}

		public Settings() {
			this(true, 0);
		}
	}

	public static class ReversedMessageBoard extends MessageBoard {
		public ReversedMessageBoard(LimitedQueue<TraceMessage> messageQueue) {
			super(messageQueue);
		}

		@Override
		@NotNull
		protected String rebuild() {
			StringBuilder builder = new StringBuilder();
			for (Iterator<TraceMessage> it = getMessageQueue().reverseIterator(); it.hasNext(); ) {
				TraceMessage line = it.next();
				builder.append(line).append("\n");
			}
			return builder.toString();
		}
	}
}
