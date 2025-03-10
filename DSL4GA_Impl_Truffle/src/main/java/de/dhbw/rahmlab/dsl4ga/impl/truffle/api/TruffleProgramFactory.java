package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.dhbw.rahmlab.dsl4ga.common.AutoCloser;
import de.dhbw.rahmlab.dsl4ga.common.LifeTimeExtender;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import java.io.Reader;
import java.net.URL;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

public class TruffleProgramFactory implements iProgramFactory<TruffleProgram> {

	protected static Context createContext() {
		Engine engine = Engine.create(GeomAlgeLang.LANGUAGE_ID);
		Context.Builder builder = Context.newBuilder(GeomAlgeLang.LANGUAGE_ID)
			.allowAllAccess(true)
			.engine(engine);
		Context context = builder.build();
		context.initialize(GeomAlgeLang.LANGUAGE_ID);
		return context;
	}

	private final AutoCloser<Context> contextCloser = AutoCloser.create(createContext());

	/**
	 * Debugging needs the URL to the file.
	 */
	@Override
	public TruffleProgram parse(URL url) {
		var context = contextCloser.get();
		var truffleProgam = new TruffleProgram(context, url);
		LifeTimeExtender.extend(contextCloser, truffleProgam);
		return truffleProgam;
	}

	@Override
	public TruffleProgram parse(Reader sourceReader) {
		var context = contextCloser.get();
		var truffleProgam = new TruffleProgram(context, sourceReader);
		LifeTimeExtender.extend(contextCloser, truffleProgam);
		return truffleProgam;
	}
}
