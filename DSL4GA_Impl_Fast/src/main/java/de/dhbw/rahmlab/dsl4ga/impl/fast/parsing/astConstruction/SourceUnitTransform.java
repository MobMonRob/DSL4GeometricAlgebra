package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.FunctionContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.api.GAFactoryService;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.ParsingService.FactoryAndMain;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SourceUnitTransform extends GeomAlgeParserBaseListener {

	protected SourceUnitTransform() {
	}

	public static FactoryAndMain generate(GeomAlgeParser parser, GeomAlgeParser.SourceUnitContext ctx) throws ValidationParsingException {
		Map<String, GAFunction> functions = new HashMap<>();
		Map<String, GAFunction> functionsView = Collections.unmodifiableMap(functions);

		// SourceUnitTransform transform = new SourceUnitTransform(geomAlgeLangContext);
		// SkippingParseTreeWalker.walk(transform, ctx, GeomAlgeParser.FunctionBodyContext.class);
		GAFactory fac = GAFactoryService.getFactory(ctx);

		for (FunctionContext functionCtx : ctx.functions) {
			GAFunction function = FuncTransform.generate(fac, parser, functionCtx, functionsView);
			String functionName = function.getName();
			if (functions.containsKey(functionName)) {
				int line = functionCtx.start.getLine();
				throw new ValidationParsingRuntimeException(line, String.format("Function with name \"%s\" has been already declared.", functionName));
			}
			functions.put(functionName, function);
		}

		GAFunction main = functions.get("main");
		if (main == null) {
			throw new ValidationParsingRuntimeException("No main function has been defined.");
		}

		return new FactoryAndMain(fac, main);
	}
}
