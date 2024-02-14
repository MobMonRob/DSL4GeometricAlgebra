package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.common.Classes;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation.Interface;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation.Method;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation.Parameter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

final class ClassGenerator {

	private ClassGenerator() {

	}

	protected static void generate(Interface i, Method m, Filer filer) throws IOException, ClassNotFoundException {
		String packageName = String.format("%s.gen.%s", i.enclosingQualifiedName, i.simpleName.toLowerCase());
		String className = m.name.substring(0, 1).toUpperCase() + m.name.substring(1);
		ClassName genClass = ClassName.get(packageName, className);

		FieldSpec programField = FieldSpec.builder(
			ClassName.get(i.annotation.program), "program", Modifier.PRIVATE, Modifier.FINAL)
			.build();

		MethodSpec constructor = ClassGenerator.constructor(i, m);

		MethodSpec invokation = ClassGenerator.invokation(m);

		TypeSpec genClassSpec = TypeSpec.classBuilder(genClass)
			.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
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

	private static MethodSpec constructor(Interface i, Method m) throws ClassNotFoundException {
		MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

		// Signature
		constructorBuilder
			.addModifiers(Modifier.PUBLIC);

		// Body
		CodeBlock.Builder bodyBuilder = CodeBlock.builder();
		bodyBuilder
			.addStatement("String path = \"./$L.ocga\"", m.name)
			.beginControlFlow("try (var in = $T.class.getResourceAsStream(path))", i.correspondingElement)
			// x
			.beginControlFlow("if (in == null)")
			// xx
			.addStatement("throw new $T(String.format(\"Path not found: %s\", path))", RuntimeException.class)
			// xx
			.endControlFlow()
			// x
			.beginControlFlow("try (var reader = new $T(new $T(in)))", BufferedReader.class, InputStreamReader.class)
			// xx
			.addStatement("var programFactory = new $T()", ClassName.get(i.annotation.programFactory))
			.addStatement("this.program = programFactory.parse(reader)")
			// xx
			.endControlFlow()
			// x
			.nextControlFlow("catch ($T ex)", IOException.class)
			// x
			.addStatement("throw new $T(ex)", RuntimeException.class)
			.endControlFlow();

		//
		constructorBuilder
			.addCode(bodyBuilder.build());
		return constructorBuilder.build();
	}

	private static MethodSpec invokation(Method m) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(m.name);

		// Signature
		methodBuilder
			.addModifiers(Modifier.PUBLIC)
			.returns(Classes.listOfSparseDoubleColumnVector);

		for (Parameter parameter : m.parameters) {
			methodBuilder
				.addParameter(Classes.sparseDoubleColumnVector, parameter.identifier);
		}

		// Body
		String args = m.parameters.stream().map(p -> p.identifier).collect(Collectors.joining(", "));
		CodeBlock.Builder bodyBuilder = CodeBlock.builder()
			.addStatement("var arguments = $T.asList($L)", Arrays.class, args)
			.addStatement("return this.program.invoke(arguments)");

		//
		methodBuilder
			.addCode(bodyBuilder.build());
		return methodBuilder.build();
	}
}
