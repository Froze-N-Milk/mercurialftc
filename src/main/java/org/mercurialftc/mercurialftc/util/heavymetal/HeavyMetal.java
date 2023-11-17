package org.mercurialftc.mercurialftc.util.heavymetal;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.util.heavymetal.annotations.*;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.ArrayMap;
import org.mercurialftc.mercurialftc.util.heavymetal.collections.annotatedtargets.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class HeavyMetal {
	private final Field output;
	private final Object outputHolder;

	private final HashMap<String, TraceComponentRenderer> traceComponents;
	private final TraceComponentRenderer.RenderOrder renderOrder;
	private final LinkedHashMap<Object, AnnotatedOnCall> onCalls;
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

	@NotNull
	@Contract(pure = true)
	public static List<AnnotatedDataField> getDataFields(@NotNull Object bloodstream) {
		ArrayList<Field> fields = new ArrayList<>();
		Class<?> searchTargetClass = bloodstream.getClass();
		Traced traced = bloodstream.getClass().getAnnotation(Traced.class);
		String group = bloodstream.getClass().getSimpleName();
		if (traced != null) group = traced.group();
		String finalGroup = group;
		while (searchTargetClass != null && searchTargetClass != Object.class) {
			fields.addAll(Arrays.asList(searchTargetClass.getDeclaredFields()));
			searchTargetClass = searchTargetClass.getSuperclass();
		}
		return fields.stream()
				.filter(f -> f.isAnnotationPresent(DataField.class))
				.peek(f -> f.setAccessible(true))
				.map(f -> new AnnotatedDataField(bloodstream, f, finalGroup))
				.collect(Collectors.toList());
	}

	@NotNull
	@Contract(pure = true)
	public static Map<Object, AnnotatedOnCall> getOnCalls(@NotNull Object bloodstream) {
		ArrayList<Method> methods = new ArrayList<>();
		Class<?> searchTargetClass = bloodstream.getClass();
		Traced traced = bloodstream.getClass().getAnnotation(Traced.class);
		String group = bloodstream.getClass().getSimpleName();
		if (traced != null) group = traced.group();
		String finalGroup = group;
		while (searchTargetClass != null && searchTargetClass != Object.class) {
			methods.addAll(Arrays.asList(searchTargetClass.getDeclaredMethods()));
			searchTargetClass = searchTargetClass.getSuperclass();
		}
		return methods.stream()
				.filter(m -> m.isAnnotationPresent(DataField.class))
				.peek(m -> m.setAccessible(true))
				.map(m -> new AnnotatedOnCall(bloodstream, m, finalGroup))
				.collect(Collectors.toMap(AnnotatedTarget::getParentInstance, toCall -> toCall));
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
		AnnotatedOnCall onCall = onCalls.get(object);
		if (onCall == null) return;
		traceComponents.putIfAbsent(onCall.group(), new TraceComponentRenderer.Builder(renderOrder).add(MessageBoard.class).build(onCall.group()));
		TraceComponentRenderer renderer = this.traceComponents.get(onCall.group());
		if (renderer == null) return;
		renderer.add(MessageBoard.class, new TimedTraceMessage(startTime, onCall.label(), onCall.contents()));
	}

	/**
	 * publishes the contents of the onCall annotation attached to this method, if there is no annotation, does nothing
	 *
	 * @param object target object, usually should be 'this'
	 */
	public void publish(Object object, String contents) {
		AnnotatedOnCall onCall = onCalls.get(object);
		if (onCall == null) return;
		traceComponents.putIfAbsent(onCall.group(), new TraceComponentRenderer.Builder(renderOrder).add(MessageBoard.class).build(onCall.group()));
		TraceComponentRenderer renderer = this.traceComponents.get(onCall.group());
		if (renderer == null) return;
		renderer.add(MessageBoard.class, new TimedTraceMessage(startTime, onCall.label(), contents));
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
	public void findTraces(GroupedData bloodstream) {
		Map<String, TraceComponentRenderer.Builder> builderMap = new HashMap<>();
		ArrayList<TracedField> tracedFields;
		tracedFields = tracedFields(bloodstream, bloodstream.getClass(), bloodstream.group());

		ArrayList<AnnotatedDataField> dataFields = new ArrayList<>(getDataFields(bloodstream));
		onCalls.putAll(getOnCalls(bloodstream));

		for (TracedField tracedField : tracedFields) {
			tracedField.getDataFields().forEach(dataFields::add);
			tracedField.getOnCalls().forEach(onCall -> onCalls.put(onCall.getParentInstance(), onCall));
		}

		dataFields.forEach(dataField -> {
			builderMap.putIfAbsent(dataField.group(), new TraceComponentRenderer.Builder(renderOrder));
			TraceComponentRenderer.Builder builder = builderMap.get(dataField.group());
			if (builder == null) return;
			builder.add(DataBlock.class, new TraceMessage(dataField.label(), dataField.getChildInstance()::toString));
		});

		onCalls.forEach((k, onCall) -> {
			builderMap.putIfAbsent(onCall.group(), new TraceComponentRenderer.Builder(renderOrder));
			TraceComponentRenderer.Builder builder = builderMap.get(onCall.group());
			if (builder == null) return;
			builder.add(MessageBoard.class);
		});

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
	private ArrayList<TracedField> tracedFields(@NotNull Object bloodstream, @NotNull Class<?> target, String parentGroup) {
		ArrayList<TracedField> traceMap = new ArrayList<>();
		for (Field field : target.getDeclaredFields()) {
			if (field.isAnnotationPresent(Traced.class)) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(Flatpack.class)) {
					traceMap.add(new FlatpackedField(bloodstream, field, parentGroup));
				} else {
					traceMap.add(new TracedField(bloodstream, field));
				}
			}
		}

		ArrayList<TracedField> rTraceMap = new ArrayList<>();

		Class<?> parent = target.getSuperclass();
		if (parent != null && parent != Object.class) {
			rTraceMap.addAll(tracedFields(bloodstream, parent, parentGroup));
		}

		for (TracedField tracedField : traceMap) {
			if (tracedField instanceof FlatpackedField) {
				rTraceMap.addAll(tracedFields(tracedField.getChildInstance(), tracedField.getChildInstance().getClass(), parentGroup));
			} else {
				rTraceMap.addAll(tracedFields(tracedField.getChildInstance(), tracedField.getChildInstance().getClass(), tracedField.group()));
			}
		}

		traceMap.addAll(rTraceMap);

		return traceMap;
	}
}
