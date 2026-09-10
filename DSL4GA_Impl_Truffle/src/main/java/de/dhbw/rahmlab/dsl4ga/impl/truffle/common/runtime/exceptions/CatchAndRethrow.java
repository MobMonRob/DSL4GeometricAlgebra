package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.AbstractExternalException;

/**
 * Bundles commonly used catch-and-rethrow commands which in turn are needed to ensure that emitted exceptions
 * contain the node closest to the cause as location.
 */
public abstract class CatchAndRethrow {

	public static interface ReturningExecutable<E> {

		E execute() throws Throwable;
	}

	public static <T> T catchAndRethrow(GeomAlgeLangBaseNode location, ReturningExecutable<T> executable) {
		try {
			return executable.execute();
		} catch (Throwable ex) {
			handle(ex, location);
			// Re-throw it. GraalVM LSP listens for exceptions originating here
            // and translates them directly to NetBeans editor squiggles.
			
			// To make sure the GraalVM Language Server Protocol (LSP) correctly intercepts your exceptions 
			// and translates them into NetBeans editor red squiggles / diagnostics, you must throw a subclass 
			// of:com.oracle.truffle.api.exception.AbstractTruffleException 
            // [1] (https://www.graalvm.org/jdk24/graalvm-as-a-platform/language-implementation-framework/Options/), 
			// [2] (https://www.graalvm.org/truffle/javadoc/org/graalvm/polyglot/PolyglotException.html)Truffle-aware	
			// tools, instruments, and the built-in language server specifically catch AbstractTruffleException. 
			// This base class ensures the underlying runtime treats the error as an expected guest language 
			// runtime or syntax error rather than an internal host-side crash (like a standard RuntimeException 
			// or NullPointerException, which the LSP framework safely swallows or treats as an internal server 
			// bug). 
			// [1] (https://www.graalvm.org/truffle/javadoc/org/graalvm/polyglot/PolyglotException.html), 
			// [2] (https://www.graalvm.org/sdk/javadoc/org/graalvm/polyglot/PolyglotException.html)
			
			// TODO AssertionError scheint mir demnach hier falsch zu sein?
			// vielleicht ist das hier doch richtig, da dies gerade nur die Fälle verarbeitet bei einem echten
			// Absturz und bei einem Syntax-Error wird die method weiter unten aufgerufen die auch die passende
			// Ex re-threwed
			throw new AssertionError();
		}
	}

	public static interface Executable {

		void execute() throws Throwable;
	}

	public static void catchAndRethrow(GeomAlgeLangBaseNode location, Executable executable) {
		try {
			executable.execute();
		} catch (Throwable ex) {
			handle(ex, location);
		}
	}

	public static void handle(Throwable exc, GeomAlgeLangBaseNode location) {
		switch (exc) {
			case AbstractExternalException ex -> {
				if (!ex.hasSourceLocation() && (location != null) && location.hasSourceSection()) {
					System.out.flush();
					System.out.print("case 1: ");
					System.out.println(locationName(location));
					System.out.flush();
					if (ex.getCause() == null) {
						throw new LanguageRuntimeException(ex, location);
					} else {
						throw new LanguageRuntimeException(ex.getCause(), location);
					}
				}
				System.out.flush();
				System.out.print("case 2: ");
				System.out.print("exLoc: " + locationName(ex.location()) + "; currentLoc: ");
				System.out.println(locationName(location));
				System.out.flush();
				throw ex;
			}
			default -> {
				System.out.flush();
				System.out.print("case 3: ");
				System.out.println(locationName(location));
				System.out.flush();
				throw new LanguageRuntimeException(exc, location);
			}
		}
	}

	private static String locationName(GeomAlgeLangBaseNode location) {
		if (location == null) {
			return "(Unknown location)";
		}
		return location.getClass().getSimpleName();
	}
}
