package org.mercurialftc.mercurialftc.util.heavymetal;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TimedTraceMessage extends TraceMessage {
	TimedTraceMessage(long startTime, String label, Supplier<String> contents) {
		super("[" + Math.round(((System.nanoTime() - startTime) * 100.0) / 1E9) / 100.0 + "] " + label, contents);
	}

	public TimedTraceMessage(long startTime, String label, @NotNull String contents) {
		this(startTime, label, () -> contents);
	}
}
