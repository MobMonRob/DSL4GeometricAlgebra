/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.GeomAlgeLangRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.runtime.ExecutionValidation;
import java.io.IOException;

/**
 *
 * @author fabian
 */
@TruffleLanguage.Registration(
	id = "geomalgelang",
	name = "GeomAlgeLang",
	version = "0.0.1")
public class GeomAlgeLang extends TruffleLanguage<GeomAlgeLangContext> {

	GeomAlgeLangContext context = new GeomAlgeLangContext();

	public GeomAlgeLang() {
		super();
	}

	@Override
	protected GeomAlgeLangContext createContext(Env env) {
		return this.context;
	}

	@Override
	protected Object getScope(GeomAlgeLangContext context) {
		return context.globalVariableScope;
	}

	@Override
	protected CallTarget parse(ParsingRequest request) throws IOException {
		GeomAlgeLangRootNode rootNode = parseSource(request.getSource());
		return rootNode.getCallTarget();
	}

	private GeomAlgeLangRootNode parseSource(Source source) throws IOException, GeomAlgeLangException {
		BaseNode topNode = ParsingService.sourceCodeToRootNode(source, this.context);
		// Semantic validation takes place here.
		GeomAlgeLangRootNode rootNode = new GeomAlgeLangRootNode(this, new FrameDescriptor(), topNode, new ExecutionValidation(this.context));
		return rootNode;
	}
}
