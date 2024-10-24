package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingServiceProxy;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.ExecutionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.io.IOException;

@TruffleLanguage.Registration(
	id = "geomalgelang",
	name = "GeomAlgeLang",
	version = "0.0.1"
)
@ProvidedTags({
	StandardTags.CallTag.class,
	StandardTags.StatementTag.class,
	StandardTags.ExpressionTag.class,
	StandardTags.ReadVariableTag.class,
	StandardTags.WriteVariableTag.class,
	DebuggerTags.AlwaysHalt.class
})
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
		Function main = ParsingServiceProxy.parse(CharStreamSupplier.from(source.getReader()), this.context);
		ExecutionRootNode rootNode = new ExecutionRootNode(this, main);
		return rootNode.getCallTarget();
	}
}
