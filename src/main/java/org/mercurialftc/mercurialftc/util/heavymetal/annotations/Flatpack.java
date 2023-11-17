package org.mercurialftc.mercurialftc.util.heavymetal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Flatpack {
	boolean includeDefaults() default true;

	String[] targets() default {};
}
