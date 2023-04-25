package de.dhbw.rahmlab.geomalgelang.annotation.processing.common;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.codeGeneration.MethodCodeGenerator;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.codeGeneration.ClassCodeGenerator;
import com.google.auto.service.AutoService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.cga.CGAMethodCodeGeneratorFactory;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.cgapath.CGAPATHMethodCodeGeneratorFactory;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ResultMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ArgumentsRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ResultRepresentation;
import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGAPATH;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class CGAProcessor extends AbstractProcessor {

	protected Elements elementUtils;
	protected Types typeUtils;
	protected Filer filer;

	protected ExceptionHandler exceptionHandler;
	protected CGAMethodCodeGeneratorFactory cgaMethodCodeGeneratorFactory;
	protected CGAPATHMethodCodeGeneratorFactory cgapathMethodCodeGeneratorFactory;

	private volatile boolean initialized = false;

	protected static final Set<String> supportedAnnotationTypes
		= Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[]{
		CGA.class.getCanonicalName(),
		CGAPATH.class.getCanonicalName()})));

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return supportedAnnotationTypes;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_17;
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.elementUtils = processingEnv.getElementUtils();
		this.typeUtils = processingEnv.getTypeUtils();
		this.filer = processingEnv.getFiler();
		this.exceptionHandler = new ExceptionHandler(processingEnv.getMessager());

		this.exceptionHandler.handle(() -> {
			TypeElement argumentsTypeElement = elementUtils.getTypeElement(Arguments.class.getCanonicalName());
			ArgumentsRepresentation argumentsRepresentation = new ArgumentsRepresentation(argumentsTypeElement);
			ArgumentsMethodMatchingService argumentsMethodMatchingService = new ArgumentsMethodMatchingService(argumentsRepresentation);

			TypeElement resultTypeElement = elementUtils.getTypeElement(Result.class.getCanonicalName());
			ResultRepresentation resultRepresentation = new ResultRepresentation(resultTypeElement);
			ResultMethodMatchingService resultMethodMatchingService = new ResultMethodMatchingService(resultRepresentation);

			this.cgaMethodCodeGeneratorFactory = new CGAMethodCodeGeneratorFactory(argumentsMethodMatchingService, resultMethodMatchingService, this.exceptionHandler);
			this.cgapathMethodCodeGeneratorFactory = new CGAPATHMethodCodeGeneratorFactory(argumentsMethodMatchingService, resultMethodMatchingService, this.exceptionHandler);

			this.initialized = true;
		});

	}

	@Override
	protected synchronized boolean isInitialized() {
		return super.isInitialized() && this.initialized;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!isInitialized()) {
			throw new IllegalStateException("Can't proccess if not initialized properly.");
		}

		this.exceptionHandler.handle(() -> {
			List<ClassCodeGenerator> classCodeGenerators = computeClassCodeGenerators(roundEnv);
			generateCode(classCodeGenerators);
		});

		// The return boolean value should be true if your annotation processor has processed all the passed annotations, and you don't want them to be passed to other annotation processors down the list.
		return true;
	}

	protected void generateCode(List<ClassCodeGenerator> classCodeGenerators) throws IOException {
		for (ClassCodeGenerator classCodeGenerator : classCodeGenerators) {
			classCodeGenerator.generateCode(this.elementUtils, this.filer);
		}
	}

	protected List<ClassCodeGenerator> computeClassCodeGenerators(RoundEnvironment roundEnv) {
		List<MethodCodeGenerator> methodCodeGenerators = new ArrayList<>();
		methodCodeGenerators.addAll(this.cgaMethodCodeGeneratorFactory.compute(roundEnv));
		methodCodeGenerators.addAll(this.cgapathMethodCodeGeneratorFactory.compute(roundEnv));

		Map<String, List<MethodCodeGenerator>> interfaceGroupedMethodCodeGenerators = methodCodeGenerators.stream()
			.collect(Collectors.groupingBy(mcg -> mcg.annotatedMethod.enclosingInterfaceQualifiedName));

		List<ClassCodeGenerator> classCodeGenerators = new ArrayList<>(interfaceGroupedMethodCodeGenerators.size());
		for (var methodCodeGeneratorGroupEntry : interfaceGroupedMethodCodeGenerators.entrySet()) {
			String qualifiedInterfaceName = methodCodeGeneratorGroupEntry.getKey();
			List<MethodCodeGenerator> methodCodeGeneratorGroup = methodCodeGeneratorGroupEntry.getValue();
			ClassCodeGenerator classCodeGenerator = new ClassCodeGenerator(qualifiedInterfaceName, methodCodeGeneratorGroup);
			classCodeGenerators.add(classCodeGenerator);
		}

		return classCodeGenerators;
	}

}
