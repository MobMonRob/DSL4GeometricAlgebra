package de.dhbw.rahmlab.geomalgelang.annotation.processing;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.cgapath.CGAPATHAnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import java.util.List;
import org.graalvm.polyglot.Source;

public class CGAPATHMethodCodeGenerator extends MethodCodeGenerator<CGAPATHAnnotatedMethodRepresentation> {

	protected static final ClassName sourceClass = ClassName.get(Source.class);

	public CGAPATHMethodCodeGenerator(CGAPATHAnnotatedMethodRepresentation annotatedMethod, List<ArgumentsMethodMatchingService.ArgumentsMethodInvocation> argumentMethodInvocations, String resultMethodName) {
		super(annotatedMethod, argumentMethodInvocations, resultMethodName);
	}

	@Override
	protected CodeBlock sourceProvidingBody(CodeBlock sourceUsingBody) {
		CodeBlock.Builder builder = CodeBlock.builder();

		ClassName enclosingInterface = ClassName.get(super.annotatedMethod.enclosingInterface);
		String path = super.annotatedMethod.cgaPathMethodAnnotation.value();

		builder
			.beginControlFlow("""
				try ( InputStream in = %T.class.getResourceAsStream(%S);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in)) )
				""", enclosingInterface, path)
			.add("$T source = Source.newBuilder($T.LANGUAGE_ID, reader, \"noname\").build();", sourceClass, programClass)
			.add(sourceUsingBody)
			.endControlFlow();

		return builder.build();
	}

}
