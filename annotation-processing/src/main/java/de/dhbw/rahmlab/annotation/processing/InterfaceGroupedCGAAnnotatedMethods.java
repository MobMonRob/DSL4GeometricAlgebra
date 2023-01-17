package de.dhbw.rahmlab.annotation.processing;

import java.util.List;

public class InterfaceGroupedCGAAnnotatedMethods {

	protected final String qualifiedInterfaceName;

	// Set would be more correct.
	protected final List<CGAAnnotatedMethod> annotatedMethods;

	public InterfaceGroupedCGAAnnotatedMethods(String qualifiedInterfaceName, List<CGAAnnotatedMethod> annotatedMethods) {
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
}
