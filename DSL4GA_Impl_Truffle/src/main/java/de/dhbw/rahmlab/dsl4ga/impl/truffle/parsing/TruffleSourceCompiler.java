package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing;

import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.common.parsing.AntlrParsing;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.api.GAFactoryService;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.DocumentState;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.DocumentAnalysisBuilder;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.astConstruction.SourceUnitTransform;
import de.orat.math.gacalc.api.GAFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/** Compiles one GA source and its optional algebra library into Truffle functions. */
final class TruffleSourceCompiler {

	record CompiledSource(DocumentState documentState, Map<String, Function> functions) {
	}

	record CompiledMain(DocumentState documentState, Function main) {
	}

	CompiledMain compileMain(Source source, GeomAlgeLangContext context) throws ValidationParsingException {
		CompiledSource compiled = compile(Optional.empty(), context.builtinRegistry.getBuiltinsView(), source, context);
		Function main = compiled.functions().get("main");
		if (main == null) {
			throw new ValidationException("No main function has been defined.");
		}
		return new CompiledMain(compiled.documentState(), main);
	}

	CompiledMain compileMain(CharStreamSupplier program, Source source, GeomAlgeLangContext context)
			throws ValidationParsingException {
		CompiledSource compiled = compile(Optional.empty(), context.builtinRegistry.getBuiltinsView(), program, source, context);
		Function main = compiled.functions().get("main");
		if (main == null) {
			throw new ValidationException("No main function has been defined.");
		}
		return new CompiledMain(compiled.documentState(), main);
	}

	private CompiledSource compile(Optional<GAFactory> importedFactory, Map<String, Function> functions,
			Source source, GeomAlgeLangContext context) throws ValidationParsingException {
		try {
			return compile(importedFactory, functions, CharStreamSupplier.from(source.getReader()), source, context);
		} catch (IOException exception) {
			throw new ValidationException(exception);
		}
	}

	/**
	 * Keeps the ANTLR input separate from the Truffle source so legacy callers can
	 * retain their original CharStream while nodes still receive a source section.
	 */
	private CompiledSource compile(Optional<GAFactory> importedFactory, Map<String, Function> functions,
			CharStreamSupplier program, Source source, GeomAlgeLangContext context)
			throws ValidationParsingException {
		DocumentState documentState = new DocumentState(source);
        DocumentAnalysisBuilder analysisBuilder = new DocumentAnalysisBuilder(source);
		context.pushParsingDocumentState(documentState);
		try {
			return AntlrParsing.parseWithFallback(program,
					parser -> compileSourceUnit(importedFactory, functions, parser, context, analysisBuilder));
		} finally {
			context.popParsingDocumentState();
		}
	}

	private CompiledSource compileSourceUnit(Optional<GAFactory> importedFactory, Map<String, Function> functions,
			GeomAlgeParser parser, GeomAlgeLangContext context,
            DocumentAnalysisBuilder analysisBuilder) throws ValidationParsingException {
		GeomAlgeParser.SourceUnitContext sourceUnit = parser.sourceUnit();
		GAFactory factory = GAFactoryService.getFactory(sourceUnit);
		DocumentState documentState = context.getCurrentParsingDocumentState();
		documentState.setFactory(factory);
		Map<String, Function> allFunctions = functions;

		if (importedFactory.isEmpty()) {
			// Only the main document initializes its algebra library. The recursive
			// compile call pushes a second DocumentState, keeping source sections of
			// library and main-document nodes separate while both are constructed.
			Optional<Path> libraryFile = factory.getAlgebraLibFile();
			if (libraryFile.isPresent()) {
				allFunctions = compile(Optional.of(factory), allFunctions, createLibrarySource(libraryFile.get()), context).functions();
			}
		} else {
			ensureCompatibleImport(importedFactory.get(), factory);
		}

		allFunctions = SourceUnitTransform.generate(allFunctions, parser, sourceUnit, context, analysisBuilder);
		documentState.complete(allFunctions, analysisBuilder.build());
		return new CompiledSource(documentState, allFunctions);
	}

	private static Source createLibrarySource(Path libraryFile) {
		try {
			return Source.newBuilder(GeomAlgeLang.LANGUAGE_ID, libraryFile.toUri().toURL()).build();
		} catch (IOException exception) {
			throw new ValidationException(exception);
		}
	}

	private static void ensureCompatibleImport(GAFactory importedFactory, GAFactory factory) {
		if (!factory.getAlgebra().equals(importedFactory.getAlgebra())) {
			throw new ValidationException("Different algebra in import is not allowed.");
		}
		if (!factory.getImplementationName().equals(importedFactory.getImplementationName())) {
			throw new ValidationException("Different implementation in import is not allowed.");
		}
	}
}
