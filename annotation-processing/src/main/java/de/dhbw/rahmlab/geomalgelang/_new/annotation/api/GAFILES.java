package de.dhbw.rahmlab.geomalgelang._new.annotation.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GAFILES {

	/*
	 * Name "value" allows to use the annotation without the methodname.
	 */
	// String value() default "";
}
