package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;

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
			case LanguageRuntimeException ex -> {
				if (!ex.hasSourceLocation() && location.hasSourceSection()) {
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
