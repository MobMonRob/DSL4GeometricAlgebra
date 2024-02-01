package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation;

import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.GAFILESProcessor.Utils;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

/*
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;
 */

public class Method {

	public final String name;

	public Method(ExecutableElement correspondingElement, Utils utils) {
		if (correspondingElement.getKind() != ElementKind.METHOD) {
			throw new IllegalArgumentException(String.format(
				"Expected \"%s\" to be a method, but was \"%s\".",
				correspondingElement.getSimpleName(), correspondingElement.getKind()));
		}

		this.name = correspondingElement.getSimpleName().toString();

		///*
		var elementUtils = utils.elementUtils();
		var typeUtils = utils.typeUtils();
		/*
		TypeElement list = elementUtils.getTypeElement(List.class.getCanonicalName());
		TypeMirror sparseDoubleColumnVector = elementUtils.getTypeElement(SparseDoubleColumnVector.class.getCanonicalName()).asType();
		DeclaredType listOfSparseDoubleColumnVector = typeUtils.getDeclaredType(list, sparseDoubleColumnVector);
		TypeMirror returnType = correspondingElement.getReturnType();
		boolean assignable = typeUtils.isAssignable(returnType, listOfSparseDoubleColumnVector);
		 */

		TypeMirror returnType = correspondingElement.getReturnType();
		TypeMirror erasure = typeUtils.erasure(returnType);

		correspondingElement.getReturnType().toString();
		// Class.forName(name)
	}
}
