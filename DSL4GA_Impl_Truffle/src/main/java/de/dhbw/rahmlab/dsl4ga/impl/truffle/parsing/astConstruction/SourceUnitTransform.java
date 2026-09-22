package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.FunctionContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SourceUnitTransform extends GeomAlgeParserBaseListener {

	protected final GeomAlgeLangContext geomAlgeLangContext;

	protected SourceUnitTransform(GeomAlgeLangContext geomAlgeLangContext) {
		this.geomAlgeLangContext = geomAlgeLangContext;
	}

	public static Map<String, Function> generate(Map<String, Function> initialFunctionsView, GeomAlgeParser parser, GeomAlgeParser.SourceUnitContext ctx, GeomAlgeLangContext geomAlgeLangContext) throws ValidationParsingException {
		Map<String, Function> functions = new HashMap<>(initialFunctionsView);
		Map<String, Function> functionsView = Collections.unmodifiableMap(functions);

		// SourceUnitTransform transform = new SourceUnitTransform(geomAlgeLangContext);
		// SkippingParseTreeWalker.walk(transform, ctx, GeomAlgeParser.FunctionBodyContext.class);
		for (FunctionContext functionCtx : ctx.functions) {
			Function function = FuncTransform.generate(parser, functionCtx, geomAlgeLangContext, functionsView);
			String functionName = function.getName();
			if (functions.containsKey(functionName)) {
				int line = functionCtx.start.getLine();
				throw new ValidationException(line, String.format("Function with name \"%s\" has been already declared.", functionName));
			}
			functions.put(functionName, function);
		}

		return functionsView;
	}
}
