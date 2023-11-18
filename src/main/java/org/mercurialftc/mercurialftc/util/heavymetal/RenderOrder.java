package org.mercurialftc.mercurialftc.util.heavymetal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.ArrayMap;

import java.util.LinkedHashMap;

@SuppressWarnings("unused")
public class RenderOrder {
	public final static Render<org.mercurialftc.mercurialftc.util.heavymetal.DataBlock.Settings> DATA_BLOCK = new Render<>(org.mercurialftc.mercurialftc.util.heavymetal.DataBlock.class, org.mercurialftc.mercurialftc.util.heavymetal.DataBlock.Builder.class, new DataBlock.Settings());
	public final static Render<org.mercurialftc.mercurialftc.util.heavymetal.MessageBoard.Settings> REVERSE_MESSAGE_BOARD = new Render<>(org.mercurialftc.mercurialftc.util.heavymetal.MessageBoard.class, org.mercurialftc.mercurialftc.util.heavymetal.MessageBoard.Builder.class, new MessageBoard.Settings());
	public final static Render<org.mercurialftc.mercurialftc.util.heavymetal.MessageBoard.Settings> MESSAGE_BOARD = new Render<>(org.mercurialftc.mercurialftc.util.heavymetal.MessageBoard.class, org.mercurialftc.mercurialftc.util.heavymetal.MessageBoard.Builder.class, new MessageBoard.Settings(false));
	public final static RenderOrder DEFAULT_MAPPING = new RenderOrder(DATA_BLOCK, REVERSE_MESSAGE_BOARD);
	public final static RenderOrder BLOCK_FIRST = DEFAULT_MAPPING;
	public final static RenderOrder BLOCK_FIRST_MESSAGE_FORWARD = new RenderOrder(DATA_BLOCK, MESSAGE_BOARD);
	public final static RenderOrder MESSAGE_FIRST_MESSAGE_FORWARD = new RenderOrder(MESSAGE_BOARD, DATA_BLOCK);
	public final static RenderOrder MESSAGE_FIRST = new RenderOrder(REVERSE_MESSAGE_BOARD, DATA_BLOCK);
	private final LinkedHashMap<Class<? extends TraceComponent>, ArrayMap.Entry<Integer, Render<?>>> orderMapping;

	public RenderOrder(@NotNull Render<?>... renders) {
		LinkedHashMap<Class<? extends TraceComponent>, ArrayMap.Entry<Integer, Render<?>>> map = new LinkedHashMap<>();
		for (int i = 0; i < renders.length; i++) {
			map.put(renders[i].component, new ArrayMap.Entry<>(i, renders[i]));
		}
		this.orderMapping = map;
	}

	public LinkedHashMap<Class<? extends TraceComponent>, ArrayMap.Entry<Integer, Render<?>>> getOrderMapping() {
		return orderMapping;
	}

	@Nullable
	public ArrayMap.Entry<Integer, Render<?>> getEntry(Class<? extends TraceComponent> traceComponent) {
		return orderMapping.get(traceComponent);
	}

	public static class Render<S> {
		private final Class<? extends TraceComponent> component;
		private final Class<? extends TraceComponent.TraceComponentBuilder> componentBuilder;
		private final S settings;

		public Render(Class<? extends TraceComponent> component, Class<? extends TraceComponent.TraceComponentBuilder> componentBuilder, S settings) {
			this.component = component;
			this.componentBuilder = componentBuilder;
			this.settings = settings;
		}

		public Class<? extends TraceComponent> getComponent() {
			return component;
		}

		public Class<? extends TraceComponent.TraceComponentBuilder> getComponentBuilder() {
			return componentBuilder;
		}

		public S getSettings() {
			return settings;
		}
	}
}
