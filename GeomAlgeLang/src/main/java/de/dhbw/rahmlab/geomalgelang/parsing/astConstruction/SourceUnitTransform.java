package de.dhbw.rahmlab.geomalgelang.parsing.astConstruction;

import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;

public class SourceUnitTransform extends GeomAlgeParserBaseListener {

	protected final GeomAlgeLangContext geomAlgeLangContext;

	protected SourceUnitTransform(GeomAlgeLangContext geomAlgeLangContext) {
		this.geomAlgeLangContext = geomAlgeLangContext;
	}

	public static void generate(GeomAlgeParser.SourceUnitContext ctx, GeomAlgeLangContext geomAlgeLangContext) {
		SourceUnitTransform transform = new SourceUnitTransform(geomAlgeLangContext);
		SkippingParseTreeWalker.walk(transform, ctx, GeomAlgeParser.FunctionContext.class);

	}
}
