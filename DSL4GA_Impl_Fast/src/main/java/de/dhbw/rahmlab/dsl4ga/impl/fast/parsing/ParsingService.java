package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing;

import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction.SourceUnitTransform;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.AntlrParsing;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ContextParseCancellationException;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingRuntimeException;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;

public final class ParsingService {

	public static record FactoryAndMain(GAFactory fac, GAFunction main) {

	}

	private ParsingService() {

	}

	protected static FactoryAndMain invoke(GeomAlgeParser parser) {
		GeomAlgeParser.SourceUnitContext sourceUnit = parser.sourceUnit();
		FactoryAndMain factoryAndMain;
		try {
			factoryAndMain = SourceUnitTransform.generate(parser, sourceUnit);
		} catch (ValidationParsingException ex) {
			throw new ValidationParsingRuntimeException(ex);
		}
		return factoryAndMain;
	}

	public static FactoryAndMain parse(CharStreamSupplier program) {
		try {
			return AntlrParsing.parseWithFallback(program, ParsingService::invoke);
		} catch (ValidationParsingException ex) {
			throw new ValidationParsingRuntimeException(ex);
		} catch (ContextParseCancellationException ex) {
			throw new ValidationParsingRuntimeException(ex);
		}
	}
}
