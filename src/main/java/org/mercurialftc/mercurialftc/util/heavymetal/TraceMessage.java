package org.mercurialftc.mercurialftc.util.heavymetal;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class TraceMessage {
	protected final String label; // origin of the message
	protected final Supplier<String> contents; // contents of the message

	TraceMessage(String label, Supplier<String> contents) {
		this.label = label;
		this.contents = contents;
	}

	public TraceMessage(String label, @NotNull String contents) {
		this(label, () -> contents);
	}

	public int getLabelWidth() {
		return label.length();
	}

	public String groupToOutput(int labelWidth) {
		StringBuilder builder = new StringBuilder(label);
		for (int len = label.length(); len < labelWidth; len++) {
			builder.append(" ");
		}
		builder.append(" | ");
		builder.append(contents.get());
		return builder.toString();
	}

	@NotNull
	@Override
	public String toString() {
		if (!Objects.equals(label, "")) {
			return label + ": " + contents.get();
		}
		return contents.get();
	}
}
