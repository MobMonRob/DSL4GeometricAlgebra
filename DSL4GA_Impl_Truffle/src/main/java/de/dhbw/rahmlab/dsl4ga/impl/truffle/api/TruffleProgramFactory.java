package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.dhbw.rahmlab.dsl4ga.common.AutoCloser;
import de.dhbw.rahmlab.dsl4ga.common.LifeTimeExtender;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.orat.math.gacalc.api.GAFactory;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

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
		Source source = createSource(url);
		return parse(source);
	}

	@Override
	public TruffleProgram parse(Reader sourceReader) {
		Source source = createSource(sourceReader);
		return parse(source);
	}

	private TruffleProgram parse(Source source) {
		Context context = this.contextCloser.get();

		final Value parsedProgram;
		final GAFactory fac;
		try {
			parsedProgram = context.parse(source);
			fac = GeomAlgeLangContext.GA_FACTORY; // Correct use. Available after parsing.
		} catch (PolyglotException ex) {
			throw ExceptionEnricher.enrichException(ex);
		}

		TruffleProgram truffleProgram = new TruffleProgram(parsedProgram, fac);
		LifeTimeExtender.extend(this.contextCloser, truffleProgram);
		return truffleProgram;
	}

	private static Source createSource(URL url) {
		try {
			return Source.newBuilder(GeomAlgeLang.LANGUAGE_ID, url).build();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static Source createSource(Reader sourceReader) {
		try {
			return Source.newBuilder(GeomAlgeLang.LANGUAGE_ID, sourceReader, "TruffleProgram").build();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
