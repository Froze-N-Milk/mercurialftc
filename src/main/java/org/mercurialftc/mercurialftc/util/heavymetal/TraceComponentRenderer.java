package org.mercurialftc.mercurialftc.util.heavymetal;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TraceComponentRenderer {
	private final String title;
	private final TraceComponent[] traceComponents;
	private final RenderOrder renderOrder;

	private TraceComponentRenderer(String title, TraceComponent[] traceComponents, RenderOrder renderOrder) {
		this.title = title;
		this.traceComponents = traceComponents;
		this.renderOrder = renderOrder;
	}

	@NonNull
	@NotNull
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(title).append("\n");
		for (TraceComponent trace : traceComponents) {
			if (trace == null) continue;
			builder.append(trace);
		}
		return builder.toString();
	}

	public void add(Class<? extends TraceComponent> traceClass, TraceMessage message) {
		Integer index = renderOrder.getIndex(traceClass);
		if (index == null) throw new RuntimeException("Target trace class was not in the render order map");
		traceComponents[index].add(message);
	}

	public static class Builder {
		private final TraceComponent.TraceComponentBuilder[] traceComponentBuilders;
		private final RenderOrder renderOrder;

		public Builder(@NotNull RenderOrder renderOrder) {
			this.renderOrder = renderOrder;
			traceComponentBuilders = new TraceComponent.TraceComponentBuilder[renderOrder.getOrderMapping().size()];
			for (Map.Entry<Class<? extends TraceComponent>, Integer> component : renderOrder.getOrderMapping().entrySet()) {
				try {
					@SuppressWarnings("unchecked")
					Class<? extends TraceComponent.TraceComponentBuilder> builder = (Class<? extends TraceComponent.TraceComponentBuilder>) component.getKey().getDeclaredClasses()[0];
					traceComponentBuilders[component.getValue()] = builder.newInstance();
				} catch (IllegalAccessException | InstantiationException e) {
					throw new RuntimeException(e);
				}
			}
		}

		public Builder add(Class<? extends TraceComponent> traceClass, TraceMessage message) {
			Integer index = renderOrder.getIndex(traceClass);
			if (index == null) throw new RuntimeException("Target trace class was not in the render order map");
			traceComponentBuilders[index].add(message);
			return this;
		}

		public Builder add(Class<? extends TraceComponent> traceClass) {
			Integer index = renderOrder.getIndex(traceClass);
			if (index == null) throw new RuntimeException("Target trace class was not in the render order map");
			traceComponentBuilders[index].add();
			return this;
		}

		public TraceComponentRenderer build(String title) {
			TraceComponent[] traceComponents = new TraceComponent[traceComponentBuilders.length];
			for (int i = 0; i < traceComponentBuilders.length; i++) {
				TraceComponent.TraceComponentBuilder builder = traceComponentBuilders[i];
				traceComponents[i] = builder.build();
			}
			return new TraceComponentRenderer(title, traceComponents, renderOrder);
		}
	}

	public static class RenderOrder {
		private final static RenderOrder defaultMapping = new RenderOrder(DataBlock.class, MessageBoard.class);
		private final LinkedHashMap<Class<? extends TraceComponent>, Integer> orderMapping;

		@SafeVarargs
		public RenderOrder(@NotNull Class<? extends TraceComponent>... traceComponents) {
			LinkedHashMap<Class<? extends TraceComponent>, Integer> map = new LinkedHashMap<>();
			for (int i = 0; i < traceComponents.length; i++) {
				map.put(traceComponents[i], i);
			}
			this.orderMapping = map;
		}

		public static RenderOrder getDefaultMapping() {
			return defaultMapping;
		}

		public LinkedHashMap<Class<? extends TraceComponent>, Integer> getOrderMapping() {
			return orderMapping;
		}

		@Nullable
		public Integer getIndex(Class<? extends TraceComponent> componentClass) {
			return orderMapping.get(componentClass);
		}
	}
}
