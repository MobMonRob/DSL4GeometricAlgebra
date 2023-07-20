package de.dhbw.rahmlab.geomalgelang.parsing.astConstruction;

import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser.FunctionContext;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SourceUnitTransform extends GeomAlgeParserBaseListener {

	protected final GeomAlgeLangContext geomAlgeLangContext;

	protected SourceUnitTransform(GeomAlgeLangContext geomAlgeLangContext) {
		this.geomAlgeLangContext = geomAlgeLangContext;
	}

	public static Function generate(GeomAlgeParser.SourceUnitContext ctx, GeomAlgeLangContext geomAlgeLangContext) {
		Map<String, Function> functions = new HashMap<>();
		Map<String, Function> functionsView = Collections.unmodifiableMap(functions);

		/*
		SourceUnitTransform transform = new SourceUnitTransform(geomAlgeLangContext);
		SkippingParseTreeWalker.walk(transform, ctx, GeomAlgeParser.FunctionBodyContext.class);
		 */
		for (FunctionContext functionCtx : ctx.functions) {
			Function function = FuncTransform.generate(functionCtx, geomAlgeLangContext, functionsView);
			if (functions.containsKey(function.name)) {
				// ToDo: Display position of the function.
				throw new ValidationException(String.format("Function with name \"%s\" hab been already declared.", function.name));
			}
			functions.put(function.name, function);
		}

		Function main = functions.get("main");
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
