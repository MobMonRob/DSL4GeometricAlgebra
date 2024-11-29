package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingServiceProvider;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaMapTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.AbstractExternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;
import de.orat.math.cga.api.CGAMultivector;
import java.util.List;
import java.util.Map;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Engine.Builder;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.SourceSection;
import org.graalvm.polyglot.Value;

public class Program implements AutoCloseable {

	public final Context context;
	public Value parsedProgram;
	public static final String LANGUAGE_ID = "ga"; //geomalgelang";
	public final Engine engine;
	public final Source source;

	public Program(String source) {
		this(Source.create(LANGUAGE_ID, source), null);
	}

	public Program(Source source, Map<String,String> options) {
		this.source = source;
		Builder engineBuilder = Engine.newBuilder(LANGUAGE_ID);
		engine = engineBuilder.build();
		Context.Builder contextBuilder = Context.newBuilder(LANGUAGE_ID)
			.allowAllAccess(true);
		if (source != null){
			//FIXME wozu?
			contextBuilder = contextBuilder.in(System.in).out(System.out);
		}
		this.context = contextBuilder.engine(engine).build();
		//FIXME
		// warum ist das hier nötig? im lsp Test fehlt dieser Aufruf
		this.context.initialize(LANGUAGE_ID);
		//FIXME warum ist das nötig?
		// im Zusammenhang mit dem LSP und Test-Code korrespondierend zu simple language
		// schlägt das parsing fehl, da dort der parsingService nicht gesetzt wird
		ParsingServiceProvider.setParsingService(ParsingService.instance());

		if (source != null){
			try {
				this.parsedProgram = this.context.parse(source);
				System.out.println("Program parsed!");
			} catch (PolyglotException ex) {
				try (this) {
					throw enrichException(ex);
				}
			}
		} else {
			System.out.println("No program to parse!");
		}
	}

	@Override
	public void close() {
		this.context.close(true);
	}

	public Result invoke(Arguments arguments) {
		try {
			Map<String, CGAMultivector> argsMapView = arguments.getArgsMapView();
			CgaMapTruffleBox args = new CgaMapTruffleBox(argsMapView);
			Value result = this.parsedProgram.execute(args);
			CgaListTruffleBox box = result.as(CgaListTruffleBox.class);
			List<CGAMultivector> answers = box.getInner();
			return new Result(answers);
		} catch (PolyglotException ex) {
			try (this) {
				throw enrichException(ex);
			}
		}
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
//			// Hier würde noch der Ort im ocga Quelltext fehlen.
//			// Und auch die Nachricht der geworfenen Exception.
//			Iterable<PolyglotException.StackFrame> polyglotStackTrace = ex.getPolyglotStackTrace();
//			String truffleStackFrames = StreamSupport.stream(polyglotStackTrace.spliterator(), false).filter(sf -> sf.isGuestFrame()).map(sf -> sf.getRootName()).collect(Collectors.joining("\n"));
//			return new RuntimeException("\n->TruffleStackFrames:\n" + truffleStackFrames + "\n");
			//
//			List<TruffleStackTraceElement> stackTrace = TruffleStackTrace.getStackTrace(langException);
//			String collect = stackTrace.stream().map(el -> el.getTarget().getRootNode().getName()).collect(Collectors.joining("\n"));
//			return new LanguageRuntimeException("\nCollect: " + collect + "\n", langException.location());
			//
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
