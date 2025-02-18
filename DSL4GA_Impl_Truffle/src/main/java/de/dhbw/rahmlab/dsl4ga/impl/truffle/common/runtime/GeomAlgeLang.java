package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.ExecutionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;
import java.io.IOException;

@TruffleLanguage.Registration(
	id = "ga",
	name = "ga",
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

	public static final String LANGUAGE_ID = "ga";

	private GeomAlgeLangContext context;

	@Override
	protected GeomAlgeLangContext createContext(Env env) {
		this.context = new GeomAlgeLangContext(this, env);
		return this.context;
	}

	//TODO
	// hier muss ich vermutlich noch ein paar mehr Methoden überschreiben
	   /**
         * Declarative list of {@link TruffleFile.FileTypeDetector} classes provided by this
         * language.
         * <p>
         * The language has to support all MIME types recognized by the registered
         * {@link TruffleFile.FileTypeDetector file type detectors}.
         *
         * @return list of file type detectors
         * @since 19.0
		 * 
		 * https://github.com/oracle/graal/blob/master/truffle/src/com.oracle.truffle.api/src/com/oracle/truffle/api/TruffleFile.java
         */
        /*Class<? extends TruffleFile.FileTypeDetector>[] fileTypeDetectors(){
			return new TruffleFile.FileTypeDetector.class{GAFileTypeDetector.class};
		}*/
	
	@Override
	protected CallTarget parse(ParsingRequest request) throws IOException {
		Source source = request.getSource();
		this.context.setSource(source);
		Function main = ParsingService.instance().parse(CharStreamSupplier.from(source.getReader()), this.context);
		ExecutionRootNode rootNode = new ExecutionRootNode(this, main);
		return rootNode.getCallTarget();
	}
}
