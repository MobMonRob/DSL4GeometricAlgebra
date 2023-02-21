package de.dhbw.rahmlab.geomalgelang.api.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface CGA {

	/*
	 * Name "value" allows to use the annotation without the methodname,
	 * like: @CGA("a") instead of @CGA(value= "a")
	 */
	String value();
}
