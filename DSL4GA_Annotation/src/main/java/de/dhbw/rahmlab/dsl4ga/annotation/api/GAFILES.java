package de.dhbw.rahmlab.dsl4ga.annotation.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The given class must have been compiled beforehand.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GAFILES {

	/*
	 * Name "value" allows to use the annotation without the methodname.
	 */
	Class<? extends iProgramFactory<? extends iProgram>> value();

	/**
	 * Either an absolute path or a relative path to the annotated interface.
	 */
	String path() default "./";
}
