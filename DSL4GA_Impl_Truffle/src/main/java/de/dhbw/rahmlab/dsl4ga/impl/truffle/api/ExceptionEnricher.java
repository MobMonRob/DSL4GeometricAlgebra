package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.AbstractExternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.SourceSection;

// Package-private
class ExceptionEnricher {

	static RuntimeException enrichException(PolyglotException ex) {
		// // Print CGA functions stacktrace. ToDo: implement with the CGA functions feature.
		// Iterable<PolyglotException.StackFrame> polyglotStackTrace = ex.getPolyglotStackTrace();
		// Can we use the Java default StackWalker here?
		// ---
		// Print the full originating error.
		AbstractExternalException origin = null;
		try {
			origin = ex.getGuestObject().as(AbstractExternalException.class);
		} catch (Throwable ex2) {
			RuntimeException fullEx = new RuntimeException(ex2);
			fullEx.addSuppressed(ex);
			return fullEx;
		}
		if (origin == null) {
			return new RuntimeException(ex);
		}
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
		return enrichLanguageException(ex, origin);
	}

	private static AbstractExternalException enrichLanguageException(PolyglotException containingException, AbstractExternalException langException) {
		SourceSection sourceSection = containingException.getSourceLocation();
		if (sourceSection == null) {
			return langException;
		}
		GeomAlgeLangBaseNode location = langException.location();
		String locationDescription = String.format("line %s, column %s", sourceSection.getStartLine(), sourceSection.getStartColumn());
		String nodeType = location.getClass().getSimpleName();
		String characters = sourceSection.getCharacters().toString();
		String message = null;
		for (Throwable currentMessager = langException; currentMessager != null; currentMessager = currentMessager.getCause()) {
			message = currentMessager.getMessage();
			if (message != null) {
				break;
			}
		}
		String newMessage = String.format("\nLocation: %s\nCharacters: \"%s\"\nNodeType: %s\nMessage: %s\n\n\n", locationDescription, characters, nodeType, message);
		return new LanguageRuntimeException(newMessage, langException, location);
	}
}
