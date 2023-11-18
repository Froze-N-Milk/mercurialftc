package org.mercurialftc.mercurialftc.util.heavymetal;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.ArrayMap;

import java.util.Iterator;
import java.util.Map;

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
		ArrayMap.Entry<Integer, RenderOrder.Render<?>> entry = renderOrder.getEntry(traceClass);
		if (entry == null) throw new RuntimeException("Target trace class was not in the render order map");
		traceComponents[entry.getKey()].add(message);
	}

	public static class Builder {
		private final TraceComponent.TraceComponentBuilder[] traceComponentBuilders;
		private final RenderOrder renderOrder;

		public Builder(@NotNull RenderOrder renderOrder) {
			this.renderOrder = renderOrder;
			traceComponentBuilders = new TraceComponent.TraceComponentBuilder[renderOrder.getOrderMapping().size()];
			for (Map.Entry<Class<? extends TraceComponent>, ArrayMap.Entry<Integer, RenderOrder.Render<?>>> component : renderOrder.getOrderMapping().entrySet()) {
				try {
					Class<? extends TraceComponent.TraceComponentBuilder> builder = component.getValue().getValue().getComponentBuilder();
					traceComponentBuilders[component.getValue().getKey()] = builder.newInstance();
				} catch (IllegalAccessException | InstantiationException e) {
					throw new RuntimeException(e);
				}
			}
		}

		public Builder add(Class<? extends TraceComponent> traceClass, TraceMessage message) {
			ArrayMap.Entry<Integer, RenderOrder.Render<?>> entry = renderOrder.getEntry(traceClass);
			if (entry == null) throw new RuntimeException("Target trace class was not in the render order map");
			traceComponentBuilders[entry.getKey()].add(message);
			return this;
		}

		public Builder add(Class<? extends TraceComponent> traceClass) {
			ArrayMap.Entry<Integer, RenderOrder.Render<?>> entry = renderOrder.getEntry(traceClass);
			if (entry == null) throw new RuntimeException("Target trace class was not in the render order map");
			traceComponentBuilders[entry.getKey()].add();
			return this;
		}

		public TraceComponentRenderer build(String title) {
			TraceComponent[] traceComponents = new TraceComponent[traceComponentBuilders.length];
			int i = 0;
			for (Iterator<Map.Entry<Class<? extends TraceComponent>, ArrayMap.Entry<Integer, RenderOrder.Render<?>>>> it = renderOrder.getOrderMapping().entrySet().iterator(); it.hasNext(); i++) {
				Map.Entry<Class<? extends TraceComponent>, ArrayMap.Entry<Integer, RenderOrder.Render<?>>> render_entry = it.next();
				TraceComponent.TraceComponentBuilder builder = traceComponentBuilders[render_entry.getValue().getKey()];
				traceComponents[i] = builder.build(render_entry.getValue().getValue().getSettings());
			}
			return new TraceComponentRenderer(title, traceComponents, renderOrder);
		}
	}

}
