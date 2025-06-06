package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.runtime.BuiltinRegistry;
import de.orat.math.gacalc.api.ExprGraphFactory;
import java.util.ArrayList;

public final class GeomAlgeLangContext {

	protected static final ContextReference<GeomAlgeLangContext> contextRef = ContextReference.create(GeomAlgeLang.class);

	public static GeomAlgeLangContext get(Node node) {
		return GeomAlgeLangContext.contextRef.get(node);
	}

	public final BuiltinRegistry builtinRegistry;
	public final GeomAlgeLang truffleLanguage;
	public final TruffleLanguage.Env env;
	public ExprGraphFactory exprGraphFactory;
	public CgaListTruffleBox lastListReturn = new CgaListTruffleBox(new ArrayList<>());
	private Source source = null;

	public GeomAlgeLangContext() {
		this(null, null);
	}

	public GeomAlgeLangContext(GeomAlgeLang truffleLanguage, Env env) {
		this.builtinRegistry = new BuiltinRegistry(truffleLanguage);
		this.truffleLanguage = truffleLanguage;
		this.env = env;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Source getSource() {
		return this.source;
	}
}
