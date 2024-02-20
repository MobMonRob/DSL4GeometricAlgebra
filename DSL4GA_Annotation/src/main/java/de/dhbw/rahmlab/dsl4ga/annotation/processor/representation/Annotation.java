package de.dhbw.rahmlab.dsl4ga.annotation.processor.representation;

import de.dhbw.rahmlab.dsl4ga.annotation.api.GAFILES;
import de.dhbw.rahmlab.dsl4ga.annotation.processor.GAFILESProcessor.Utils;
import de.dhbw.rahmlab.dsl4ga.annotation.processor.common.AnnotationException;
import de.dhbw.rahmlab.dsl4ga.annotation.processor.common.Classes;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

public final class Annotation {

	public final TypeElement programFactory;
	public final TypeElement program;
	public final String path;

	protected Annotation(GAFILES correspondingAnnotation, Utils utils) throws AnnotationException {
		try {
			correspondingAnnotation.value().getClass();
			throw new AssertionError("Should have thrown a MirroredTypeException before this.");
		} catch (MirroredTypeException mte) {
			// Save assumption because classes are DeclaredTypes.
			this.programFactory = (TypeElement) ((DeclaredType) mte.getTypeMirror()).asElement();
		}

		// Save assumption, because every programFactory implements iProgramFactory.
		DeclaredType iProgramFactoryType = this.programFactory.getInterfaces().stream()
			.map(i -> (DeclaredType) i)
			.filter(i -> ((TypeElement) i.asElement()).getQualifiedName().toString().equals(Classes.iProgramFactory.rawType.canonicalName()))
			.findFirst()
			.get();

		// Save, because iProgramFactory has only one typeArgument.
		this.program = (TypeElement) ((DeclaredType) iProgramFactoryType.getTypeArguments().get(0)).asElement();

		this.path = correspondingAnnotation.path();
	}
}
