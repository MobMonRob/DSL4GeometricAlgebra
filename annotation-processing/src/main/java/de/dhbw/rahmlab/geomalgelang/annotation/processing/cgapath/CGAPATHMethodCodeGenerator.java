package de.dhbw.rahmlab.geomalgelang.annotation.processing.cgapath;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.codeGeneration.MethodCodeGenerator;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.graalvm.polyglot.Source;

public class CGAPATHMethodCodeGenerator extends MethodCodeGenerator<CGAPATHAnnotatedMethodRepresentation> {

	protected static final ClassName sourceClass = ClassName.get(Source.class);
	protected static final ClassName inputStreamClass = ClassName.get(InputStream.class);
	protected static final ClassName bufferedReaderClass = ClassName.get(BufferedReader.class);
	protected static final ClassName inputStreamReaderClass = ClassName.get(InputStreamReader.class);
	protected static final ClassName ioExceptionClass = ClassName.get(IOException.class);
	protected static final ClassName runtimeExceptionClass = ClassName.get(RuntimeException.class);

	public CGAPATHMethodCodeGenerator(CGAPATHAnnotatedMethodRepresentation annotatedMethod, List<ArgumentsMethodMatchingService.ArgumentsMethodInvocation> argumentMethodInvocations, String resultMethodName) {
		super(annotatedMethod, argumentMethodInvocations, resultMethodName);
	}

	@Override
	protected CodeBlock sourceProvidingBody(CodeBlock sourceUsingBody) {
		CodeBlock.Builder builder = CodeBlock.builder();

		ClassName enclosingInterface = ClassName.get(super.annotatedMethod.enclosingInterface);
		String path = super.annotatedMethod.cgaPathMethodAnnotation.value();

		builder
			.addStatement("String path = $S", path)
			.beginControlFlow("""
				try ( $T in = $T.class.getResourceAsStream(path);
					var reader = new $T(new $T(in)) )
				""", inputStreamClass, enclosingInterface, bufferedReaderClass, inputStreamReaderClass)
			.addStatement("var source = $T.newBuilder($T.LANGUAGE_ID, reader, \"noname\").build()", sourceClass, programClass)
			.add(sourceUsingBody)
			.endControlFlow()
			.beginControlFlow("catch ($T ex)", ioExceptionClass)
			.addStatement("throw new $T(ex)", runtimeExceptionClass)
			.endControlFlow();

		return builder.build();
	}

}
