package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.ExecutionRootNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;
import java.io.IOException;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.interop.TruffleObject;
import java.util.Collections;
//import com.oracle.truffle.api.Scope;

/**
 * Der TruffleLanguageProvider wird von der Truffle-DSL automatisch generiert, 
 * wenn die Sprachklasse mit @TruffleLanguage.Registration annotiert wird. 
 */
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
	// StandardTags.ExpressionTag.class,
	StandardTags.ReadVariableTag.class,
	StandardTags.WriteVariableTag.class,
	StandardTags.RootBodyTag.class,
	StandardTags.RootTag.class,
	DebuggerTags.AlwaysHalt.class
})
public class GeomAlgeLang extends TruffleLanguage<GeomAlgeLangContext> {

	// All these need to be compatible with https://github.com/orat/netbeans-ocga .
	// Otherwise, debugging stops working.
	public static final String LANGUAGE_ID = "ocga";
	public static final String FILE_ENDING = ".ocga";
	public static final String MIME_TYPE = "text/x-ocga"; // application/x-ocga";

	private GeomAlgeLangContext context;

	@Override
	protected GeomAlgeLangContext createContext(Env env) {
		this.context = new GeomAlgeLangContext(this, env);
		return this.context;
	}

	@Override
	protected CallTarget parse(ParsingRequest request) throws IOException {
		return catchAndRethrow(null, () -> parseImpl(request));
	}

	private CallTarget parseImpl(ParsingRequest request) throws IOException {
		Source source = request.getSource();
		this.context.setSource(source);
		ParsingService.FactoryAndMain factoryAndMain = ParsingService.instance().parse(CharStreamSupplier.from(source.getReader()), this.context);
		this.context.setFac(factoryAndMain.fac()); // Set in ParsingService::invoke, too.
		Function main = factoryAndMain.main();
		this.context.setMainArity(main.getArity());
		ExecutionRootNode rootNode = new ExecutionRootNode(this, main);
		return rootNode.getCallTarget();
	}
	
	
	// vergleiche mit impl oben TODO
	// Ensure that your language's root parsing entry point doesn't swallow parser exceptions. Let them
	//propagate up so the GraalVM engine can deliver them to the LSP layer.
	/*@Override
    public CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();
        
        try {
            // Call your custom compiler/parser front-end here
            RootNode rootNode = MyParser.parseFile(this, source); 
            return rootNode.getCallTarget();
            
        } catch (MyLanguageSyntaxException e) {
            // Re-throw it. GraalVM LSP listens for exceptions originating here
            // and translates them directly to NetBeans editor squiggles.
            throw e; 
        }
    }*/
}
