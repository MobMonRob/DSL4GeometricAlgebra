package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.AbstractExternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.TruffleBox;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.SourceSection;
import org.graalvm.polyglot.Value;

public class TruffleProgram implements iProgram {

	private final Source source;
	private final Value parsedProgram;

	protected TruffleProgram(Context context, Reader sourceReader) {
		try {
			this.source = Source.newBuilder(GeomAlgeLang.LANGUAGE_ID, sourceReader, "TruffleProgram").build();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		try {
			this.parsedProgram = context.parse(source);
		} catch (PolyglotException ex) {
			throw enrichException(ex);
		}
	}

	protected TruffleProgram(Context context, URL url) {
		try {
			this.source = Source.newBuilder(GeomAlgeLang.LANGUAGE_ID, url).build();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		try {
			this.parsedProgram = context.parse(source);
		} catch (PolyglotException ex) {
			throw enrichException(ex);
		}
	}

	@Override
	public List<SparseDoubleMatrix> invoke(List<SparseDoubleMatrix> arguments) {
		try {
			TruffleBox<List<SparseDoubleMatrix>> truffleArgs = new TruffleBox<>(arguments);
			Value result = this.parsedProgram.execute(truffleArgs);
			TruffleBox<List<SparseDoubleMatrix>> truffleResults = (TruffleBox<List<SparseDoubleMatrix>>) result.as(TruffleBox.class);
			List<SparseDoubleMatrix> results = truffleResults.getInner();
			return results;
		} catch (PolyglotException ex) {
			throw enrichException(ex);
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
