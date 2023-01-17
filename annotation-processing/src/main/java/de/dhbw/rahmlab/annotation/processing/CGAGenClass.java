package de.dhbw.rahmlab.annotation.processing;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

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

	public void generateCode(Elements elementUtils, Filer filer) throws IOException, CGAAnnotationException {
		TypeElement implementingInterfaceName = elementUtils.getTypeElement(this.qualifiedInterfaceName);
		String genClassName = implementingInterfaceName.getSimpleName() + SUFFIX;

		int nameSeparatorIndex = this.qualifiedInterfaceName.lastIndexOf(".");
		String packageName = this.qualifiedInterfaceName.substring(0, nameSeparatorIndex) + ".gen";

		List<MethodSpec> methods = new ArrayList<>(this.annotatedMethods.size());
		for (CGAAnnotatedMethod annotatedMethod : this.annotatedMethods) {
			MethodSpec method = annotatedMethod.generateCode();
			methods.add(method);
		}

		FieldSpec instance = FieldSpec.builder(TypeName.get(implementingInterfaceName.asType()), "INSTANCE", Modifier.PUBLIC, Modifier.STATIC)
			.initializer("new $L()", genClassName)
			.build();

		TypeSpec cgaGen = TypeSpec.classBuilder(genClassName)
			.addModifiers(Modifier.PUBLIC)
			.addSuperinterface(implementingInterfaceName.asType())
			.addField(instance)
			.addMethods(methods)
			.build();

		JavaFile javaFile = JavaFile.builder(packageName, cgaGen)
			.build();

		// javaFile.writeTo(filer);
		javaFile.writeTo(System.out);
		// JavaFileObject jfo = filer.createSourceFile(this.qualifiedInterfaceName + SUFFIX);
		// Writer writer = jfo.openWriter();
	}
}
