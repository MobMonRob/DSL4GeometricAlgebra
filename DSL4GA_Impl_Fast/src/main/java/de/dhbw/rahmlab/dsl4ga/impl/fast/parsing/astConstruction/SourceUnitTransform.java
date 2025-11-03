package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.FunctionContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.ParsingService.FactoryAndMain;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;
import de.orat.math.gacalc.api.GAServiceLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.Token;

public class SourceUnitTransform extends GeomAlgeParserBaseListener {

	protected SourceUnitTransform() {
	}

	public static FactoryAndMain generate(GeomAlgeParser parser, GeomAlgeParser.SourceUnitContext ctx) {
		Map<String, GAFunction> functions = new HashMap<>();
		Map<String, GAFunction> functionsView = Collections.unmodifiableMap(functions);

		// SourceUnitTransform transform = new SourceUnitTransform(geomAlgeLangContext);
		// SkippingParseTreeWalker.walk(transform, ctx, GeomAlgeParser.FunctionBodyContext.class);
		var algebraContext = ctx.algebra();
		String algebraID = algebraContext.algebraID.getText();
		Token implID = algebraContext.implID;

		GAFactory fac;
		if (implID != null) {
			fac = GAServiceLoader.getGAFactoryThrowing(algebraID, implID.getText());
		} else {
			fac = GAServiceLoader.getGAFactoryThrowing(algebraID);
		}

		for (FunctionContext functionCtx : ctx.functions) {
			GAFunction function = FuncTransform.generate(fac, parser, functionCtx, functionsView);
			String functionName = function.getName();
			if (functions.containsKey(functionName)) {
				int line = functionCtx.start.getLine();
				throw new ValidationException(line, String.format("Function with name \"%s\" has been already declared.", functionName));
			}
			functions.put(functionName, function);
		}

		GAFunction main = functions.get("main");
		if (main == null) {
			throw new ValidationException("No main function has been defined.");
		}

		return new FactoryAndMain(fac, main);
	}
}
