package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AnnotatedMethodRepresentation extends MethodRepresentation {

	public final TypeElement enclosingInterface;
	public final String enclosingInterfaceQualifiedName;

	public AnnotatedMethodRepresentation(ExecutableElement methodElement) throws AnnotationException {
		super(methodElement);
		this.enclosingInterface = getEnclosingInterface(methodElement);
		this.enclosingInterfaceQualifiedName = this.enclosingInterface.getQualifiedName().toString();
		ensureModifiersContainPublic(methodElement);
	}

	protected static void ensureModifiersContainPublic(ExecutableElement methodElement) throws AnnotationException {
		Set<Modifier> modifiers = methodElement.getModifiers();
		boolean containsPublic = modifiers.contains(Modifier.PUBLIC);
		if (!containsPublic) {
			throw AnnotationException.create(methodElement, "Method needs to be \"public\".");
		}
	}

	protected static TypeElement getEnclosingInterface(ExecutableElement methodElement) throws AnnotationException {
		Element directEnclosingElement = methodElement.getEnclosingElement();
		ElementKind directEnclosingElementKind = directEnclosingElement.getKind();

		TypeElement enclosingInterface = null;
		if (directEnclosingElementKind == ElementKind.INTERFACE) {
			enclosingInterface = (TypeElement) directEnclosingElement;
		} else {
			throw AnnotationException.create(methodElement, "Expected method to be enclosed by an INTERFACE, but was enclosed by \"%s\"", directEnclosingElementKind.toString());
		}

		return enclosingInterface;
	}
}
