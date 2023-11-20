package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.ExecutionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import java.io.IOException;

@TruffleLanguage.Registration(
	id = "geomalgelang",
	name = "GeomAlgeLang",
	version = "0.0.1")
public class GeomAlgeLang extends TruffleLanguage<GeomAlgeLangContext> {

	private GeomAlgeLangContext context;

	@Override
	protected GeomAlgeLangContext createContext(Env env) {
		this.context = new GeomAlgeLangContext(this, env);
		return this.context;
	}

	@Override
	protected CallTarget parse(ParsingRequest request) throws IOException {
		Source source = request.getSource();
		this.context.setSource(source);
		Function main = ParsingService.parse(CharStreamSupplier.from(source), this.context);
		ExecutionRootNode rootNode = new ExecutionRootNode(this, main);
		return rootNode.getCallTarget();
	}
}
