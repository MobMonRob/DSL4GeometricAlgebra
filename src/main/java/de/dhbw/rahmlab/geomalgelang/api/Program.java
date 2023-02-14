package de.dhbw.rahmlab.geomalgelang.api;

import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBox;
import de.orat.math.cga.api.CGAMultivector;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.SourceSection;
import org.graalvm.polyglot.Value;

public class Program implements AutoCloseable {

	protected Context context;
	protected Value program;

	public Program(String source) {
		this(Source.create("geomalgelang", source));
	}

	public Program(Source source) {
		Engine engine = Engine.create("geomalgelang");

		Context.Builder builder = Context.newBuilder("geomalgelang")
			.allowAllAccess(true)
			.engine(engine);

		this.context = builder.build();

		try {
			program = context.parse(source);
			// parsing succeeded
		} catch (PolyglotException e) {
			if (e.isSyntaxError()) {
				SourceSection location = e.getSourceLocation();
				// syntax error detected at location
			} else {
				// other guest error detected
			}
			context.close();
			throw e;
		}
	}

	@Override
	public void close() {
		context.close();
	}

	public Result invoke(Arguments arguments) {
		System.out.println("variable assignments: ");
		arguments.argsMap.forEach((name, value) -> {
			String varString = "\t" + name + " := " + value.toString();
			System.out.println(varString);
		});

		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html
		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/library/package-summary.html
		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/TruffleLanguage.Env.html#importSymbol-java.lang.String-
		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/TruffleLanguage.Env.html#exportSymbol-java.lang.String-java.lang.Object-
		// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/TruffleLanguage.Env.html#getPolyglotBindings--
		// Env is available in GeomAlgeLang.java
		// program.invokeMember(identifier, arguments); // Alternative for main() function
		// Do it similar to simple language: launcher / SLmain.java
		Value bindings = context.getBindings("geomalgelang"); //polyglotBindings

		arguments.argsMap.forEach((name, value) -> {
			bindings.putMember(name, new CgaTruffleBox(value));
		});

		CGAMultivector answer;

		try {
			// later: execute with arguments XOR getMember "main" and execute it with arguments (instead of bindings.putMember)
			Value result = program.execute();
			answer = result.as(CgaTruffleBox.class).inner;
		} finally {
			// Will be executed regardless if an exception is thrown or not
			// context.close();
		}

		return new Result(answer);
	}
}
