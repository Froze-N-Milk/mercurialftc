package org.mercurialftc.mercurialftc.util.heavymetal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.MessageArrayList;

@SuppressWarnings("unused")
public class DataBlock implements TraceComponent {
	private final MessageArrayList<TraceMessage> traceMessages;

	public DataBlock(MessageArrayList<TraceMessage> traceMessages) {
		this.traceMessages = traceMessages;
	}

	public MessageArrayList<TraceMessage> getTraceMessages() {
		return traceMessages;
	}

	@NotNull
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (TraceMessage line : traceMessages) {
			builder.append(line.groupToOutput(traceMessages.getLabelWidth())).append("\n");
		}
		return builder.toString();
	}

	@Override
	public void add(TraceMessage message) {
		traceMessages.add(message);
	}

	public static class Builder implements TraceComponentBuilder {
		private final MessageArrayList<TraceMessage> traceMessages = new MessageArrayList<>();

		@Override
		public void add(TraceMessage message) {
			traceMessages.add(message);
		}

		@Nullable
		@Override
		public TraceComponent build(Object settings) {
			if (traceMessages.isEmpty()) return null;
			return new DataBlock(traceMessages);
		}
	}

	public static class Settings {

	}
}
