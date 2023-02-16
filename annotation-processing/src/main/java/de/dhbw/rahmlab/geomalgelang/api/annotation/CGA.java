package de.dhbw.rahmlab.geomalgelang.api.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface CGA {

	String source();
}
