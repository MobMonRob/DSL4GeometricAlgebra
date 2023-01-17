package de.dhbw.rahmlab.annotation.processing;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

public class CGAGenClass {

	protected static final String SUFFIX = "Gen";

	protected final String qualifiedInterfaceName;

	// Set would be more correct.
	protected final List<CGAAnnotatedMethod> annotatedMethods;

	public CGAGenClass(String qualifiedInterfaceName, List<CGAAnnotatedMethod> annotatedMethods) {
		for (CGAAnnotatedMethod annotatedMethod : annotatedMethods) {
			String currentQualifiedInterfaceName = annotatedMethod.enclosingInterfaceQualifiedName;

			if (!(currentQualifiedInterfaceName.equals(qualifiedInterfaceName))) {
				throw new IllegalArgumentException(
					String.format(
						"Method \"%s\" in \"%s\" cannot be added to InterfaceGroupedCGAAnnotatedMethods with qualifiedInterfaceName set to \"%s\". This is an error of the annotation processor programmer.",
						annotatedMethod.identifier,
						currentQualifiedInterfaceName,
						qualifiedInterfaceName));
			}
		}

		this.qualifiedInterfaceName = qualifiedInterfaceName;
		this.annotatedMethods = annotatedMethods;
	}

	public void generateCode(Elements elementUtils, Filer filer) throws IOException {
		TypeElement implementingInterfaceName = elementUtils.getTypeElement(this.qualifiedInterfaceName);
		String genClassName = implementingInterfaceName.getSimpleName() + SUFFIX;

		JavaFileObject jfo = filer.createSourceFile(this.qualifiedInterfaceName + SUFFIX);
		Writer writer = jfo.openWriter();

	}
}
