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
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;
import java.io.IOException;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.interop.TruffleObject;
import java.util.Collections;
//import com.oracle.truffle.api.Scope;

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
		return catchAndRethrow(null, () -> parseImpl(request));
	}

	private CallTarget parseImpl(ParsingRequest request) throws IOException {
		Source source = request.getSource();
		this.context.setSource(source);
		ParsingService.FactoryAndMain factoryAndMain = ParsingService.instance().parse(CharStreamSupplier.from(source.getReader()), this.context);
		// Will be set at ParsingService::invoke.
		// this.context.setFac(factoryAndMain.fac());
		ExecutionRootNode rootNode = new ExecutionRootNode(this, factoryAndMain.main());
		return rootNode.getCallTarget();
	}
	
	// needed for usage of the experimental language server implemented in GraalVM/Truffle:
	// While the tags tell the LSP what the nodes are, the LSP still needs to ask your language: 
    // "What local variables are visible at this specific cursor position?"
    // You handle this by overriding findLocalScopes in your core TruffleLanguage class. GraalVM calls this           // method automatically when a user clicks a symbol inside NetBeans.
	// TODO unklar, wie das in der aktuellen Version erreicht wird

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
	
    // findLocalScopes() und Scope scheint es nicht mehr zu geben
    // MyFunctionRootNode is unklar
   	
	/*@Override
    public Iterable<Scope> findLocalScopes(GeomAlgeLangContext context, Node node, Frame frame) {
        // 1. Walk up the AST from the current node to find the enclosing function/block
        Node parent = node;
        while (parent != null && !(parent instanceof MyFunctionRootNode)) {
            parent = parent.getParent();
        }

        if (parent instanceof MyFunctionRootNode) {
            MyFunctionRootNode rootNode = (MyFunctionRootNode) parent;
            
            // 2. Build an LSP-compatible Scope object
            Scope.Builder scopeBuilder = Scope.newBuilder(rootNode.getName(), rootNode);
            
            // 3. Populate variables that belong to this local frame scope
            // (You fetch these from your parser's variable maps or FrameDescriptor)
            TruffleObject localVariables = fetchVariablesForNode(rootNode);
            scopeBuilder.variables(localVariables);
            
            return Collections.singletonList(scopeBuilder.build());
        }

        return Collections.emptyList();
    }
	
	private TruffleObject fetchVariablesForNode(MyFunctionRootNode root) {
        // Return a TruffleObject mapping local variable names to their values or descriptors
		// TODO
        return null; 
    }*/
}
