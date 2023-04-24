package de.dhbw.rahmlab.geomalgelang.annotation.processing.common;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.codeGeneration.MethodCodeGenerator;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.codeGeneration.ClassCodeGenerator;
import com.google.auto.service.AutoService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.cga.CGAMethodCodeGenerator;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.cga.CGAAnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService.ArgumentsMethodInvocation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ResultMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.AnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ArgumentsRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.MethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ResultRepresentation;
import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGAPATH;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class CGAProcessor extends AbstractProcessor {

	protected Elements elementUtils;
	protected Types typeUtils;
	protected Filer filer;

	protected ExceptionHandler exceptionHandler;
	protected ArgumentsMethodMatchingService argumentsMethodMatcherService;
	protected ResultMethodMatchingService resultMethodMatchingService;

	protected static final Set<String> supportedAnnotationTypes
		= Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[]{CGA.class.getCanonicalName(), CGAPATH.class.getCanonicalName()})));

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

		TypeElement argumentsTypeElement = elementUtils.getTypeElement(Arguments.class.getCanonicalName());
		ArgumentsRepresentation argumentsRepresentation = new ArgumentsRepresentation(argumentsTypeElement);
		this.argumentsMethodMatcherService = new ArgumentsMethodMatchingService(argumentsRepresentation);

		TypeElement resultTypeElement = elementUtils.getTypeElement(Result.class.getCanonicalName());
		ResultRepresentation resultRepresentation = new ResultRepresentation(resultTypeElement);
		this.resultMethodMatchingService = new ResultMethodMatchingService(resultRepresentation);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		this.exceptionHandler.handle(() -> {
			List<CGAAnnotatedMethodRepresentation> annotatedMethods = computeCGAAnnotatedMethodsFrom(roundEnv);
			Map<String, List<CGAAnnotatedMethodRepresentation>> interfaceGroupedCGAAnnotatedMethods = computeInterfaceGroupedCGAAnnotatedMethodsFrom(annotatedMethods);
			List<ClassCodeGenerator> classCodeGenerators = computeClassCodeGeneratorsFrom(interfaceGroupedCGAAnnotatedMethods);
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

	protected List<ClassCodeGenerator> computeClassCodeGeneratorsFrom(Map<String, List<CGAAnnotatedMethodRepresentation>> interfaceGroupedCGAAnnotatedMethods) throws AnnotationException {
		List<ClassCodeGenerator> classCodeGenerators = new ArrayList<>(interfaceGroupedCGAAnnotatedMethods.size());
		for (var methodGroupEntry : interfaceGroupedCGAAnnotatedMethods.entrySet()) {
			String qualifiedInterfaceName = methodGroupEntry.getKey();
			var methodGroup = methodGroupEntry.getValue();
			List<MethodCodeGenerator> methodCodeGenerators = computeMethodCodeGenerators(methodGroup);
			ClassCodeGenerator classCodeGenerator = new ClassCodeGenerator(qualifiedInterfaceName, methodCodeGenerators);
			classCodeGenerators.add(classCodeGenerator);
		}

		return classCodeGenerators;
	}

	protected List<MethodCodeGenerator> computeMethodCodeGenerators(List<CGAAnnotatedMethodRepresentation> methodGroup) throws AnnotationException {
		List<MethodCodeGenerator> methodCodeGenerators = new ArrayList<>(methodGroup.size());
		for (CGAAnnotatedMethodRepresentation cgaCGAAnnotatedMethod : methodGroup) {
			exceptionHandler.handle(() -> {
				var argumentMethodInvocations = this.argumentsMethodMatcherService.computeMatchingArgumentsMethods(cgaCGAAnnotatedMethod);
				var resultMethodName = this.resultMethodMatchingService.computeMatchingResultMethod(cgaCGAAnnotatedMethod).identifier;
				MethodCodeGenerator methodCodeGenerator = new CGAMethodCodeGenerator(cgaCGAAnnotatedMethod, argumentMethodInvocations, resultMethodName);
				methodCodeGenerators.add(methodCodeGenerator);
			});
		}
		return methodCodeGenerators;
	}

	protected Map<String, List<CGAAnnotatedMethodRepresentation>> computeInterfaceGroupedCGAAnnotatedMethodsFrom(List<CGAAnnotatedMethodRepresentation> annotatedMethods) {
		Map<String, List<CGAAnnotatedMethodRepresentation>> interfaceGroupedCGAAnnotatedMethods = annotatedMethods.stream()
			.collect(Collectors.groupingBy(am -> am.enclosingInterfaceQualifiedName));
		return interfaceGroupedCGAAnnotatedMethods;
	}

	protected List<CGAAnnotatedMethodRepresentation> computeCGAAnnotatedMethodsFrom(RoundEnvironment roundEnv) throws AnnotationException {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(CGA.class);
		List<CGAAnnotatedMethodRepresentation> annotatedMethods = new ArrayList<>(annotatedElements.size());

		for (Element annotatedElement : annotatedElements) {
			// ExecutableElement is a safe assumption, because @Target(ElementType.METHOD) in CGA.java.
			ExecutableElement method = (ExecutableElement) annotatedElement;
			CGAAnnotatedMethodRepresentation annotatedMethod = new CGAAnnotatedMethodRepresentation(method);

			annotatedMethods.add(annotatedMethod);
		}

		return annotatedMethods;
	}
	
	protected record MCGargument<AMR2 extends AnnotatedMethodRepresentation>(AMR2 annotatedMethod, List<ArgumentsMethodInvocation> argumentMethodInvocations, String resultMethodName) {
		
	}

	protected <AMR extends AnnotatedMethodRepresentation, MCG extends MethodCodeGenerator<AMR>> List<MCG> computeMethodCodeGeneratorsFrom(List<AMR> methodGroup, Function<MCGargument<AMR>, MCG> methodCodeGeneratorConstructor) throws AnnotationException {
		List<MCG> methodCodeGenerators = new ArrayList<>(methodGroup.size());
		for (AMR annotatedMethod : methodGroup) {
			exceptionHandler.handle(() -> {
				List<ArgumentsMethodInvocation> argumentMethodInvocations = this.argumentsMethodMatcherService.computeMatchingArgumentsMethods(annotatedMethod);
				String resultMethodName = this.resultMethodMatchingService.computeMatchingResultMethod(annotatedMethod).identifier;
				MCGargument<AMR> argument = new MCGargument(annotatedMethod, argumentMethodInvocations, resultMethodName);
				MCG methodCodeGenerator = methodCodeGeneratorConstructor.apply(argument);
				methodCodeGenerators.add(methodCodeGenerator);
			});
		}
		return methodCodeGenerators;
	}

	protected <AMR extends AnnotatedMethodRepresentation> List<AMR> computeAnnotatedMethodsFrom(RoundEnvironment roundEnv, Class<? extends Annotation> annotationClass, Function<ExecutableElement, AMR> annotatedMethodRepresentationConstructor) throws AnnotationException {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotationClass);
		List<AMR> annotatedMethods = new ArrayList<>(annotatedElements.size());

		for (Element annotatedElement : annotatedElements) {
			// ExecutableElement is a safe assumption, because @Target(ElementType.METHOD) in CGA.java.
			ExecutableElement method = (ExecutableElement) annotatedElement;
			AMR annotatedMethod = annotatedMethodRepresentationConstructor.apply(method);

			annotatedMethods.add(annotatedMethod);
		}

		return annotatedMethods;
	}
}
