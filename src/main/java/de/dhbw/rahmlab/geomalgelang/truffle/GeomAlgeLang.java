/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.ProgramRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.ExpressionBaseNode;
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

	private final GeomAlgeLangContext context;

	public GeomAlgeLang() {
		super();
		this.context = new GeomAlgeLangContext(this);
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
		ProgramRootNode rootNode = parseSource(request.getSource());
		return rootNode.getCallTarget();
	}

	private ProgramRootNode parseSource(Source source) throws IOException, GeomAlgeLangException {
		ExpressionBaseNode topNode = ParsingService.getAST(CharStreamSupplier.from(source), this.context);
		// Semantic validation takes place here.
		ProgramRootNode rootNode = new ProgramRootNode(this, new FrameDescriptor(), topNode, new ExecutionValidation(this.context));
		return rootNode;
	}
}
