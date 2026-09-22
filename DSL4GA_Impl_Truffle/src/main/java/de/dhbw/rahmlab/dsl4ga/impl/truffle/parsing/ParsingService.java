package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing;

import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ContextParseCancellationException;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.gacalc.api.GAFactory;
import java.util.Map;

/** Public compatibility facade for parsing GA sources into Truffle functions. */
public final class ParsingService {

	public static record FactoryAndMain(GAFactory fac, Function main) {
	}

	public static record FactoryAndFunctions(GAFactory fac, Map<String, Function> functions) {
	}

	private final TruffleSourceCompiler sourceCompiler = new TruffleSourceCompiler();

	private ParsingService() {
	}

	public static ParsingService instance() {
		return new ParsingService();
	}

	/** Parses a Truffle source and binds all created AST nodes to that source. */
	public FactoryAndMain parse(Source source, GeomAlgeLangContext context) {
		try {
			TruffleSourceCompiler.CompiledMain compiled = sourceCompiler.compileMain(source, context);
			return new FactoryAndMain(compiled.factory(), compiled.main());
		} catch (ValidationParsingException | ContextParseCancellationException exception) {
			throw SourceExceptionDecorator.decorate(exception, source);
		}
	}

	/**
	 * Compatibility entry point for callers outside Truffle that do not have a
	 * {@link Source}. Such parses receive a synthetic source name.
	 */
	public FactoryAndMain parse(CharStreamSupplier program, GeomAlgeLangContext context) {
		Source source = Source.newBuilder(GeomAlgeLang.LANGUAGE_ID, program.get().toString(), "in-memory.ga").build();
		try {
			TruffleSourceCompiler.CompiledMain compiled = sourceCompiler.compileMain(program, source, context);
			return new FactoryAndMain(compiled.factory(), compiled.main());
		} catch (ValidationParsingException | ContextParseCancellationException exception) {
			throw SourceExceptionDecorator.decorate(exception, source);
		}
	}

}
