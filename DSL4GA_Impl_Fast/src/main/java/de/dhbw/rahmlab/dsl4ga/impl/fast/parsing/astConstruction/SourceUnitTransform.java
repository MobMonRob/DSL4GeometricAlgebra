package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.FunctionContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.orat.math.gacalc.api.FunctionSymbolic;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SourceUnitTransform extends GeomAlgeParserBaseListener {

	protected SourceUnitTransform() {
	}

	public static FunctionSymbolic generate(GeomAlgeParser parser, GeomAlgeParser.SourceUnitContext ctx) {
		Map<String, FunctionSymbolic> functions = new HashMap<>();
		Map<String, FunctionSymbolic> functionsView = Collections.unmodifiableMap(functions);

		/*
		SourceUnitTransform transform = new SourceUnitTransform(geomAlgeLangContext);
		SkippingParseTreeWalker.walk(transform, ctx, GeomAlgeParser.FunctionBodyContext.class);
		 */
		for (FunctionContext functionCtx : ctx.functions) {
			FunctionSymbolic function = FuncTransform.generate(parser, functionCtx, functionsView);
			String functionName = function.getName();
			if (functions.containsKey(functionName)) {
				// ToDo: Display position of the function.
				throw new ValidationException(String.format("Function with name \"%s\" has been already declared.", functionName));
			}
			functions.put(functionName, function);
		}

		FunctionSymbolic main = functions.get("main");
		if (main == null) {
			throw new ValidationException("No main function has been defined.");
		}

		return main;
	}

	/*
	@Override
	public void enterFunctionHead_(GeomAlgeParser.FunctionHead_Context ctx) {
		String name = ctx.name.getText();
	}
	 */
}
