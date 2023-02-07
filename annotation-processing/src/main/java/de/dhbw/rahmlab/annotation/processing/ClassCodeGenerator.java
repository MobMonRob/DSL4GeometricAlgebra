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

public class ClassCodeGenerator {

	protected static final String CLASS_SUFFIX = "Gen";
	protected static final String PACKAGE_SUFFIX = ".gen";

	protected final String qualifiedInterfaceName;

	protected final List<CGAMethodCodeGenerator> methodCodeGenerators;

	public ClassCodeGenerator(String qualifiedInterfaceName, List<CGAMethodCodeGenerator> methodCodeGenerators) {
		this.qualifiedInterfaceName = qualifiedInterfaceName;
		this.methodCodeGenerators = methodCodeGenerators;
	}

	public void generateCode(Elements elementUtils, Filer filer) throws IOException, AnnotationException {
		TypeElement implementingInterfaceName = elementUtils.getTypeElement(this.qualifiedInterfaceName);
		String genClassName = implementingInterfaceName.getSimpleName() + CLASS_SUFFIX;

		int nameSeparatorIndex = this.qualifiedInterfaceName.lastIndexOf(".");
		String packageName = this.qualifiedInterfaceName.substring(0, nameSeparatorIndex) + PACKAGE_SUFFIX;

		List<MethodSpec> methods = new ArrayList<>(this.methodCodeGenerators.size());
		for (CGAMethodCodeGenerator methodCodeGenerator : this.methodCodeGenerators) {
			MethodSpec method = methodCodeGenerator.generateCode();
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
