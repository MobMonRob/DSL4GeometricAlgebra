package de.dhbw.rahmlab.annotation.processing;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface CGA {

	String source();
}
