package org.mercurialftc.mercurialftc.util.heavymetal;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.DataField;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.Traced;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.ArrayMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class HeavyMetal {
	private final Field output;
	private final Object outputHolder;

	private final HashMap<String, TraceComponentRenderer> traceComponents;
	private final TraceComponentRenderer.RenderOrder renderOrder;

	public HeavyMetal(@NotNull Telemetry telemetry, TraceComponentRenderer.RenderOrder renderOrder) {
		telemetry.setAutoClear(false);
		telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
		telemetry.setCaptionValueSeparator("");
		telemetry.setItemSeparator("");
		try {
			this.outputHolder = telemetry.addLine("");
			this.output = outputHolder.getClass().getField("lineCaption");
			output.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}

		this.traceComponents = new HashMap<>();
		this.renderOrder = renderOrder;
	}

	public HeavyMetal(Object outputHolder, Field outputField, TraceComponentRenderer.RenderOrder renderOrder) {
		this.outputHolder = outputHolder;
		this.output = outputField;
		output.setAccessible(true);

		this.traceComponents = new HashMap<>();
		this.renderOrder = renderOrder;
	}

	void set(String output) {
		try {
			this.output.set(outputHolder, output);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void update() {
		StringBuilder builder = new StringBuilder();

		for (TraceComponentRenderer traceComponent : traceComponents.values()) {
			builder.append(traceComponent);
			builder.append("\n");
		}

		set(builder.toString());
	}

	/**
	 * finds traces of heavy metal in the bloodstream
	 *
	 * @param bloodstream the root class to start searching from
	 */
	public void findTraces(Object instance, Class<?> bloodstream) {
		Map<String, TraceComponentRenderer.Builder> builderMap = new HashMap<>();

		ArrayMap<Object, Field> tracedFields;
		try {
			tracedFields = tracedFields(instance, bloodstream);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		ArrayMap<Object, ArrayMap.Entry<String, Field>> dataFields = new ArrayMap<>();

		for (Field field : instance.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(DataField.class)) {
				field.setAccessible(true);

				Traced annotation = instance.getClass().getDeclaredAnnotation(Traced.class);
				String parentGroup = "";
				if (annotation != null) {
					parentGroup = annotation.group();
				} else {
					parentGroup = instance.getClass().getSimpleName();
				}

				dataFields.add(instance, new ArrayMap.Entry<>(parentGroup, field));
			}
		}
		for (ArrayMap.Entry<Object, Field> entry : tracedFields) {
			try {
				Object searchTarget = Objects.requireNonNull(entry.getValue().get(entry.getKey()));
				Class<?> searchTargetClass = searchTarget.getClass();

				while (searchTargetClass != Object.class && searchTargetClass != null) {
					for (Field field : searchTargetClass.getDeclaredFields()) {
						if (field.isAnnotationPresent(DataField.class)) {
							field.setAccessible(true);

							String parentGroup = Objects.requireNonNull(entry.getValue().getDeclaredAnnotation(Traced.class)).group();
							if (Objects.equals(parentGroup, "") || parentGroup == null) {
								parentGroup = entry.getValue().getName();
							}
							dataFields.add(entry.getValue().get(entry.getKey()), new ArrayMap.Entry<>(parentGroup, field));
						}
					}
					searchTargetClass = searchTargetClass.getSuperclass();
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		for (ArrayMap.Entry<Object, ArrayMap.Entry<String, Field>> dataField : dataFields) {
			DataField annotation = Objects.requireNonNull(dataField.getValue().getValue().getAnnotation(DataField.class));

			String group = annotation.group();
			if (Objects.equals(group, "") || group == null) {
				String parentGroup = dataField.getValue().getKey();
				if (Objects.equals(parentGroup, "") || parentGroup == null) {
					dataField.getKey().getClass().getSimpleName();
				}
				group = parentGroup;
			}

			String label = annotation.label();
			if (Objects.equals(label, "") || label == null) {
				label = dataField.getValue().getValue().getName();
			}

			builderMap.putIfAbsent(group, new TraceComponentRenderer.Builder(renderOrder));
			TraceComponentRenderer.Builder builder = builderMap.get(group);
			if (builder == null) continue;

			Field field = dataField.getValue().getValue();
			Object object = dataField.getKey();
			if (field == null || object == null || !field.isAccessible()) continue;

			Object target = null;
			try {
				target = field.get(object);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			builder.add(DataBlock.class, new TraceMessage(label, target != null ? target::toString : null));
		}

		// builds the renders
		for (Map.Entry<String, TraceComponentRenderer.Builder> renderBuilderEntry : builderMap.entrySet()) {
			traceComponents.put(renderBuilderEntry.getKey(), renderBuilderEntry.getValue().build(renderBuilderEntry.getKey()));
		}
	}

	/**
	 * recursively finds the fields which contain traces in the blood stream
	 *
	 * @param bloodstream root object
	 * @param target      class of the root object
	 */
	@NotNull
	private ArrayMap<Object, Field> tracedFields(@NotNull Object bloodstream, @NotNull Class<?> target) throws IllegalAccessException {
		ArrayMap<Object, Field> traceMap = new ArrayMap<>();
		for (Field field : target.getDeclaredFields()) {
			if (field.isAnnotationPresent(Traced.class)) {
				field.setAccessible(true);
				traceMap.add(bloodstream, field);
			}
		}

		ArrayMap<Object, Field> rTraceMap = new ArrayMap<>();

		Class<?> parent = target.getSuperclass();
		if (parent != null && parent != Object.class) {
			rTraceMap.addAll(tracedFields(bloodstream, parent));
		}

		for (ArrayMap.Entry<Object, Field> entry : traceMap) {
			Object downstream = entry.getValue().get(entry.getKey());
			if (downstream == null) continue;
			rTraceMap.addAll(tracedFields(downstream, downstream.getClass()));
		}

		traceMap.addAll(rTraceMap);

		return traceMap;
	}
}
