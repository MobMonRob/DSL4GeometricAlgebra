package de.dhbw.rahmlab.annotation.processing.api;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface CGA {

	String source();
}
