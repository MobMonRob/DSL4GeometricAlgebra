package de.dhbw.rahmlab.geomalgelang.api;

import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaListTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.AbstractExternalException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.orat.math.cga.api.CGAMultivector;
import java.util.ArrayList;
import java.util.List;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.SourceSection;
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
			RuntimeException enrichedException = enrichException(ex);
			this.context.close();
			throw enrichedException;
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

		Value result = null;
		try {
			/*
			arguments.argsMap.forEach((name, value) -> {
				bindings.putMember(name, new CgaTruffleBox(value));
			});
			 */

			List<CGAMultivector> argsList = new ArrayList<>(arguments.argsMap.values());
			CgaListTruffleBox args = new CgaListTruffleBox(argsList);

			// later: execute with arguments XOR getMember "main" and execute it with arguments (instead of bindings.putMember)
			result = this.program.execute(args);
		} catch (PolyglotException ex) {
			RuntimeException enrichedException = enrichException(ex);
			this.context.close();
			throw enrichedException;
		}

		CgaListTruffleBox box = result.as(CgaListTruffleBox.class);
		List<CGAMultivector> answers = box.getInner();

		return new Result(answers);
	}

	private RuntimeException enrichException(PolyglotException ex) {
		// // Print CGA functions stacktrace. ToDo: implement with the CGA functions feature.
		// Iterable<PolyglotException.StackFrame> polyglotStackTrace = ex.getPolyglotStackTrace();
		// Can we use the Java default StackWalker here?
		// ---

		// Print the full originating error.
		AbstractExternalException origin = null;
		try {
			origin = ex.getGuestObject().as(AbstractExternalException.class);
		} catch (Exception ex2) {
			return ex;
		}
		if (origin == null) {
			return ex;
		}

		if (origin instanceof LanguageRuntimeException langException) {
			return enrichLanguageRuntimeException(ex, langException);
		} else if (origin instanceof ValidationException validationException) {
			return enrichValidationException(ex, validationException);
		}
		throw new AssertionError(String.format(
			"The given AbstractExternalException instance was of unexpected subtype: %s",
			origin.getClass().getCanonicalName()
		));
	}

	private LanguageRuntimeException enrichLanguageRuntimeException(
		PolyglotException containingException,
		LanguageRuntimeException langException
	) {

		SourceSection sourceSection = containingException.getSourceLocation();
		if (sourceSection == null) {
			return langException;
		}

		GeomAlgeLangBaseNode location = langException.location();

		String locationDescription = String.format(
			"line %s, column %s",
			sourceSection.getStartLine(),
			sourceSection.getStartColumn()
		);
		String nodeType = location.getClass().getSimpleName();
		String characters = sourceSection.getCharacters().toString();

		String newMessage = String.format(
			"\nLocation: %s\nCharacters: \"%s\"\nNodeType: %s",
			locationDescription,
			characters,
			nodeType
		);

		return new LanguageRuntimeException(newMessage, langException, location);
	}

	private RuntimeException enrichValidationException(
		PolyglotException containingException,
		ValidationException validationException
	) {
		return validationException;
	}
}
