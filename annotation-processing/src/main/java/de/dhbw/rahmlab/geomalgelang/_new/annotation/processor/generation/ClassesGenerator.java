package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.generation;

import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation.Interface;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation.Method;
import java.io.IOException;
import java.util.List;
import javax.annotation.processing.Filer;

public final class ClassesGenerator {

	private ClassesGenerator() {

	}

	public static void generate(List<Interface> interfaces, Filer filer) throws IOException, ClassNotFoundException {
		for (Interface i : interfaces) {
			for (Method m : i.methods) {
				ClassGenerator.generate(i, m, filer);
			}
		}
	}
}
