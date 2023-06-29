package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.ExecutionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;
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
		this.context = new GeomAlgeLangContext(this);
		return this.context;
	}

	@Override
	protected Object getScope(GeomAlgeLangContext context) {
		return context.globalVariableScope;
	}

	@Override
	protected CallTarget parse(ParsingRequest request) throws IOException {
		ExecutionRootNode rootNode = parseSource(request.getSource());
		return rootNode.getCallTarget();
	}

	private ExecutionRootNode parseSource(Source source) throws IOException {
		this.context.setSource(source);
		FunctionDefinitionBody functionDefinitionBody = ParsingService.parse(CharStreamSupplier.from(source), this.context);

		FrameDescriptor frameDescriptor = new FrameDescriptor();
		AbstractFunctionRootNode functionRootNode = new FunctionDefinitionRootNode(this, functionDefinitionBody);
		ExecutionValidation executionValidation = new ExecutionValidation(this.context);

		ExecutionRootNode rootNode = new ExecutionRootNode(this, frameDescriptor, functionRootNode, executionValidation);
		return rootNode;
	}
}
