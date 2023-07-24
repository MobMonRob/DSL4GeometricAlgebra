package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.ExecutionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import java.io.IOException;

@TruffleLanguage.Registration(
	id = "geomalgelang",
	name = "GeomAlgeLang",
	version = "0.0.1")
public class GeomAlgeLang extends TruffleLanguage<GeomAlgeLangContext> {

	private GeomAlgeLangContext context;

	public GeomAlgeLang() {
		super();
	}

	@Override
	protected GeomAlgeLangContext createContext(Env env) {
		this.context = new GeomAlgeLangContext(this, env);
		return this.context;
	}

	@Override
	protected Object getScope(GeomAlgeLangContext context) {
		return context.globalVariableScope;
	}

	@Override
	protected CallTarget parse(ParsingRequest request) throws IOException {
		RootNode rootNode = parseSource(request.getSource());
		return rootNode.getCallTarget();
	}

	private RootNode parseSource(Source source) throws IOException {
		this.context.setSource(source);
		Function main = ParsingService.parse(CharStreamSupplier.from(source), this.context);

		ExecutionValidation executionValidation = new ExecutionValidation(this.context);
		ExecutionRootNode rootNode = new ExecutionRootNode(this, ((FunctionDefinitionRootNode) main.getRootNode()), executionValidation);

		return rootNode;
	}
}
