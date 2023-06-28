package de.dhbw.rahmlab.geomalgelang.parsing.astConstruction;

import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;

public class ProgramTransform extends GeomAlgeParserBaseListener {

	protected final GeomAlgeLangContext geomAlgeLangContext;

	protected ProgramTransform(GeomAlgeLangContext geomAlgeLangContext) {
		this.geomAlgeLangContext = geomAlgeLangContext;
	}

	public static void generate(GeomAlgeParser.ProgramContext ctx, GeomAlgeLangContext geomAlgeLangContext) {
		ProgramTransform transform = new ProgramTransform(geomAlgeLangContext);
		SkippingParseTreeWalker.walk(transform, ctx, GeomAlgeParser.FunctionContext.class);

	}
}
