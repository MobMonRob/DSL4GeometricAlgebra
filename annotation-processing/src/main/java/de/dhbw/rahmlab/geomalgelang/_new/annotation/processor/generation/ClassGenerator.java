package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import de.dhbw.rahmlab.geomalgelang._common.api.iProgram;
import de.dhbw.rahmlab.geomalgelang._common.api.iProgramFactory;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation.Interface;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation.Method;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

public final class ClassGenerator {

	private ClassGenerator() {

	}

	protected static final ClassName iProgramClass = ClassName.get(iProgram.class);
	protected static final ClassName iProgramFactoryClass = ClassName.get(iProgramFactory.class);

	protected static void generate(Interface i, Method m, Filer filer) throws IOException {
		String packageName = String.format("%s.gen.%s", i.enclosingQualifiedName, i.simpleName.toLowerCase());
		String className = m.name.substring(0, 1).toUpperCase() + m.name.substring(1);
		ClassName genClass = ClassName.get(packageName, className);

		FieldSpec programField = FieldSpec.builder(iProgramClass, "program", Modifier.PROTECTED)
			.build();

		MethodSpec constructor = MethodSpec.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.addParameter(iProgramFactoryClass, "programFactory")
			.build();

		MethodSpec invokation = MethodSpec.methodBuilder(m.name)
			.addModifiers(Modifier.PUBLIC)
			.build();

		TypeSpec genClassSpec = TypeSpec.classBuilder(genClass)
			.addModifiers(Modifier.PUBLIC)
			.addField(programField)
			.addMethod(constructor)
			.addMethod(invokation)
			.build();

		JavaFile javaFile = JavaFile.builder(packageName, genClassSpec)
			.skipJavaLangImports(true)
			.indent("\t")
			.build();

		javaFile.writeTo(filer);
	}
}
