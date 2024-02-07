package de.dhbw.rahmlab.geomalgelang._new.annotation.processor;

import de.dhbw.rahmlab.geomalgelang._new.annotation.api.GAFILES;
import com.google.auto.service.AutoService;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.common.ExceptionHandler;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.generation.ClassesGenerator;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation.Interface;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public final class GAFILESProcessor extends AbstractProcessor {

	public static record Utils(ExceptionHandler exceptionHandler, Elements elementUtils, Types typeUtils) {

	}

	private Utils utils;

	private Filer filer;

	private volatile boolean initialized = false;

	protected static final Set<String> supportedAnnotationTypes = Set.of(GAFILES.class.getCanonicalName());

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return supportedAnnotationTypes;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_17;
	}

	@Override
	protected synchronized boolean isInitialized() {
		return super.isInitialized() && this.initialized;
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.filer = processingEnv.getFiler();

		var exceptionHandler = new ExceptionHandler(processingEnv.getMessager());
		var elementUtils = processingEnv.getElementUtils();
		var typeUtils = processingEnv.getTypeUtils();
		this.utils = new Utils(exceptionHandler, elementUtils, typeUtils);

		this.initialized = true;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!isInitialized()) {
			throw new IllegalStateException("Can't proccess if not initialized properly.");
		}

		this.utils.exceptionHandler().handle(() -> {
			List<Interface> interfaces = GAFILESProcessor.computeInterfaces(roundEnv, this.utils);
			ClassesGenerator.generate(interfaces, this.filer);
		});

		// The return boolean value should be true if your annotation processor has processed all the passed annotations, and you don't want them to be passed to other annotation processors down the list.
		return true;
	}

	private static List<Interface> computeInterfaces(RoundEnvironment roundEnv, Utils utils) {
		// Safe cast because "@Target(ElementType.TYPE)" of GAFILES.
		Set<TypeElement> annotatedTypes = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(GAFILES.class);

		List<Interface> interfaces = new ArrayList<>(annotatedTypes.size());
		for (TypeElement annotatedType : annotatedTypes) {
			utils.exceptionHandler().handle(() -> {
				Interface interfaceRepr = new Interface(annotatedType, utils);
				interfaces.add(interfaceRepr);
			});
		}

		return interfaces;
	}
}
