package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.ExecutionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;
import java.io.IOException;

// contextPolicy = ContextPolicy.SHARED
@TruffleLanguage.Registration(
	id = GeomAlgeLang.LANGUAGE_ID,
	name = GeomAlgeLang.LANGUAGE_ID,
	version = "0.0.1",
	characterMimeTypes = GeomAlgeLang.MIME_TYPE,
	defaultMimeType = GeomAlgeLang.MIME_TYPE,
	fileTypeDetectors = GeomAlgeLangFileDetector.class
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

	// All these need to be compatible with https://github.com/orat/netbeans-ocga .
	// Otherwise, debugging seems to stop working.
	public static final String LANGUAGE_ID = "ocga";
	public static final String FILE_ENDING = ".ocga";
	public static final String MIME_TYPE = "application/x-ocga";

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
		ParsingService.FactoryAndMain factoryAndMain = ParsingService.instance().parse(CharStreamSupplier.from(source.getReader()), this.context);
		this.context.exprGraphFactory = factoryAndMain.fac();
		ExecutionRootNode rootNode = new ExecutionRootNode(this, factoryAndMain.main(), factoryAndMain.fac());
		return rootNode.getCallTarget();
	}
}
