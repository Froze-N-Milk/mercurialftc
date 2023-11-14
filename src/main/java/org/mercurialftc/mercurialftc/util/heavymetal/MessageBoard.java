package org.mercurialftc.mercurialftc.util.heavymetal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.MessageBoardQueue;

import java.util.ArrayList;
import java.util.Queue;

@SuppressWarnings("unused")
public class MessageBoard implements TraceComponent {
	private final MessageBoardQueue<TraceMessage> messageQueue;
	private String cachedBuild;

	public MessageBoard(MessageBoardQueue<TraceMessage> messageQueue) {
		this.messageQueue = messageQueue;
		this.cachedBuild = "";
	}

	@NotNull
	private String rebuild() {
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
		public TraceComponent build() {
			if (length == 0) return null;
			length += 2;
			return new MessageBoard(new MessageBoardQueue<>(length));
		}
	}
}
