package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.dhbw.rahmlab.dsl4ga.common.AutoCloser;
import de.dhbw.rahmlab.dsl4ga.common.LifeTimeExtender;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingServiceProvider;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import java.io.Reader;
import java.net.URL;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

public class TruffleProgramFactory implements iProgramFactory<TruffleProgram> {

	static {
		ParsingServiceProvider.setParsingService(ParsingService.instance());
	}

	private static Context createContext() {
		Engine engine = Engine.create(GeomAlgeLang.LANGUAGE_ID);
		Context.Builder builder = Context.newBuilder(GeomAlgeLang.LANGUAGE_ID)
			.allowAllAccess(true)
			.engine(engine);
		Context context = builder.build();
		context.initialize(GeomAlgeLang.LANGUAGE_ID);
		return context;
	}

	private final AutoCloser<Context> contextCloser = AutoCloser.create(createContext());
	private final ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.getExprGraphFactoryThrowing();

	/**
	 * Debugging needs the URL to the file.
	 */
	@Override
	public TruffleProgram parse(URL url) {
		var context = contextCloser.get();
		var truffleProgam = new TruffleProgram(exprGraphFactory, context, url);
		LifeTimeExtender.extend(contextCloser, truffleProgam);
		return truffleProgam;
	}

	@Override
	public TruffleProgram parse(Reader sourceReader) {
		var context = contextCloser.get();
		var truffleProgam = new TruffleProgram(exprGraphFactory, context, sourceReader);
		LifeTimeExtender.extend(contextCloser, truffleProgam);
		return truffleProgam;
	}
}
