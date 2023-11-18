package org.mercurialftc.mercurialftc.util.heavymetal;

import org.jetbrains.annotations.Nullable;

public interface TraceComponent {
	void add(TraceMessage message);

	interface TraceComponentBuilder {
		/**
		 * add for types that don't care about the message on initialisation
		 */
		default void add() {
			add(null);
		}

		void add(TraceMessage message);

		/**
		 * @return null if nothing was added
		 */
		@Nullable
		TraceComponent build(Object settings);
	}
}
