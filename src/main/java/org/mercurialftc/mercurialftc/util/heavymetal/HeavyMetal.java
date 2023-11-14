package org.mercurialftc.mercurialftc.util.heavymetal;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.DataField;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.OnCall;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.Traced;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.ArrayMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class HeavyMetal {
	private final Field output;
	private final Object outputHolder;

	private final HashMap<String, TraceComponentRenderer> traceComponents;
	private final TraceComponentRenderer.RenderOrder renderOrder;
	private final LinkedHashMap<Object, ArrayMap.Entry<String, Method>> onCalls;
	private final long startTime;

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
		this.startTime = System.nanoTime();
		this.onCalls = new LinkedHashMap<>();
	}

	public HeavyMetal(Object outputHolder, Field outputField, TraceComponentRenderer.RenderOrder renderOrder) {
		this.outputHolder = outputHolder;
		this.output = outputField;
		output.setAccessible(true);

		this.traceComponents = new HashMap<>();
		this.renderOrder = renderOrder;
		this.startTime = System.nanoTime();
		this.onCalls = new LinkedHashMap<>();
	}

	void set(String output) {
		try {
			this.output.set(outputHolder, output);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * a standalone publish that requires no annotation on the method calling this
	 *
	 * @param group
	 * @param label
	 * @param contents
	 */
	public void publish(String group, String label, String contents) {
		traceComponents.putIfAbsent(group, new TraceComponentRenderer.Builder(renderOrder).add(MessageBoard.class).build(group));
		TraceComponentRenderer renderer = this.traceComponents.get(group);
		if (renderer == null) return;
		renderer.add(MessageBoard.class, new TimedTraceMessage(startTime, label, contents));
	}

	/**
	 * publishes the contents of the onCall annotation attached to this method, if there is no annotation, does nothing
	 *
	 * @param object target object, usually should be 'this'
	 */
	public void publish(Object object) {
		ArrayMap.Entry<String, Method> target = onCalls.get(object);
		if (target == null) return;
		OnCall annotation = target.getValue().getDeclaredAnnotation(OnCall.class);
		if (annotation == null) return;
		String group = annotation.group();
		if (Objects.equals("", group)) group = target.getKey();
		traceComponents.putIfAbsent(group, new TraceComponentRenderer.Builder(renderOrder).add(MessageBoard.class).build(group));
		TraceComponentRenderer renderer = this.traceComponents.get(group);
		if (renderer == null) return;
		String label = annotation.label();
		if (Objects.equals("", label)) label = target.getValue().getName();
		String contents = annotation.contents();
		if (Objects.equals("", contents)) contents = "was invoked";
		renderer.add(MessageBoard.class, new TimedTraceMessage(startTime, label, contents));
	}

	/**
	 * publishes the contents of the onCall annotation attached to this method, if there is no annotation, does nothing
	 *
	 * @param object target object, usually should be 'this'
	 */
	public void publish(Object object, String contents) {
		ArrayMap.Entry<String, Method> target = onCalls.get(object);
		if (target == null) return;
		OnCall annotation = target.getValue().getDeclaredAnnotation(OnCall.class);
		if (annotation == null) return;
		String group = annotation.group();
		if (Objects.equals("", group)) group = target.getKey();
		traceComponents.putIfAbsent(group, new TraceComponentRenderer.Builder(renderOrder).add(MessageBoard.class).build(group));
		TraceComponentRenderer renderer = this.traceComponents.get(group);
		if (renderer == null) return;
		String label = annotation.label();
		if (Objects.equals("", label)) label = target.getValue().getName();
		renderer.add(MessageBoard.class, new TimedTraceMessage(startTime, label, contents));
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
		for (Method method : instance.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(OnCall.class)) {
				method.setAccessible(true);

				Traced annotation = instance.getClass().getDeclaredAnnotation(Traced.class);
				String parentGroup = "";
				if (annotation != null) {
					parentGroup = annotation.group();
				} else {
					parentGroup = instance.getClass().getSimpleName();
				}

				onCalls.put(instance, new ArrayMap.Entry<>(parentGroup, method));
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
					for (Method method : searchTargetClass.getDeclaredMethods()) {
						if (method.isAnnotationPresent(OnCall.class)) {
							method.setAccessible(true);

							String parentGroup = Objects.requireNonNull(entry.getValue().getDeclaredAnnotation(Traced.class)).group();
							if (Objects.equals(parentGroup, "") || parentGroup == null) {
								parentGroup = entry.getValue().getName();
							}
							onCalls.put(entry.getValue().get(entry.getKey()), new ArrayMap.Entry<>(parentGroup, method));
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

			Object target;
			try {
				target = field.get(object);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			builder.add(DataBlock.class, new TraceMessage(label, target != null ? target::toString : null));
		}

		for (Map.Entry<Object, ArrayMap.Entry<String, Method>> onCall : onCalls.entrySet()) {
			OnCall annotation = Objects.requireNonNull(onCall.getValue().getValue().getAnnotation(OnCall.class));

			String group = annotation.group();
			if (Objects.equals(group, "") || group == null) {
				String parentGroup = onCall.getValue().getKey();
				if (Objects.equals(parentGroup, "") || parentGroup == null) {
					onCall.getKey().getClass().getSimpleName();
				}
				group = parentGroup;
			}

			builderMap.putIfAbsent(group, new TraceComponentRenderer.Builder(renderOrder));
			TraceComponentRenderer.Builder builder = builderMap.get(group);
			if (builder == null) continue;

			builder.add(MessageBoard.class);
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
