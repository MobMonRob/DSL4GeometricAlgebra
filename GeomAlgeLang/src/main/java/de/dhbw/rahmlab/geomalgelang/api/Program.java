package de.dhbw.rahmlab.geomalgelang.api;

import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaListTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangException;
import de.orat.math.cga.api.CGAMultivector;
import java.util.List;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public class Program implements AutoCloseable {

	protected Context context;
	protected Value program;
	public static final String LANGUAGE_ID = "geomalgelang";

	public Program(String source) {
		this(Source.create(LANGUAGE_ID, source));
	}

	public Program(Source source) {
		Engine engine = Engine.create(LANGUAGE_ID);

		Context.Builder builder = Context.newBuilder(LANGUAGE_ID)
			.allowAllAccess(true)
			.engine(engine);

		this.context = builder.build();
		this.context.initialize(LANGUAGE_ID);

		try {
			program = this.context.parse(source);
		} catch (PolyglotException ex) {
			processAndRethrow(ex);
		}
	}

	@Override
	public void close() {
		this.context.close();
	}

	public Result invoke(Arguments arguments) {
		/// System.out.println("variable assignments: ");
		arguments.argsMap.forEach((name, value) -> {
			String varString = "\t" + name + " := " + value.toString();
			// System.out.println(varString);
		});

		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html
		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/library/package-summary.html
		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/TruffleLanguage.Env.html#importSymbol-java.lang.String-
		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/TruffleLanguage.Env.html#exportSymbol-java.lang.String-java.lang.Object-
		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/TruffleLanguage.Env.html#getPolyglotBindings--
		// Env is available in GeomAlgeLang.java
		// program.invokeMember(identifier, arguments); // Alternative for main() function
		// Do it similar to simple language: launcher / SLmain.java
		Value bindings = this.context.getBindings(LANGUAGE_ID); //polyglotBindings

		arguments.argsMap.forEach((name, value) -> {
			bindings.putMember(name, new CgaTruffleBox(value));
		});

		Value result = null;
		try {
			// later: execute with arguments XOR getMember "main" and execute it with arguments (instead of bindings.putMember)
			result = this.program.execute();
		} catch (PolyglotException ex) {
			processAndRethrow(ex);
		}

		CgaListTruffleBox box = result.as(CgaListTruffleBox.class);
		List<CGAMultivector> answers = box.getInner();

		return new Result(answers);
	}

	private void processAndRethrow(PolyglotException ex) {
		// // Print CGA functions stacktrace. ToDo: implement with the CGA functions feature.
		// Iterable<PolyglotException.StackFrame> polyglotStackTrace = ex.getPolyglotStackTrace();
		// ---
		// // Possible if GeomAlgeLangException exports this message.
		// SourceSection location = ex.getSourceLocation();
		// ---

		// Print the full originating error.
		// Can also be used to transfer additional information through GeomAlgeLangException.
		GeomAlgeLangException origin = null;
		try {
			origin = ex.getGuestObject().as(GeomAlgeLangException.class);
		} catch (Exception ex2) {

		}

		this.context.close();

		if (origin != null) {
			throw origin;
		} else {
			throw ex;
		}
	}
}
