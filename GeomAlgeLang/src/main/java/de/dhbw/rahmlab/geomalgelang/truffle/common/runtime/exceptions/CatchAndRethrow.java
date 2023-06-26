package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions;

import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.internal.InterpreterInternalException;

/**
 * Bundles commonly used catch-and-rethrow commands which in turn are needed to ensure that emitted exceptions
 * contain the node closest to the cause as location.
 */
public abstract class CatchAndRethrow {

	public static interface ReturningExecutable<E> {

		E execute() throws InterpreterInternalException, LanguageRuntimeException, RuntimeException;
	}

	public static <T> T catchAndRethrow(GeomAlgeLangBaseNode location, ReturningExecutable<T> executable) {
		try {
			return executable.execute();
		} catch (InterpreterInternalException ex) {
			throw new LanguageRuntimeException(ex, location);
		} catch (LanguageRuntimeException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw new LanguageRuntimeException(ex, location);
		}
	}

	public static interface Executable {

		void execute() throws InterpreterInternalException, LanguageRuntimeException, RuntimeException;
	}

	public static void catchAndRethrow(GeomAlgeLangBaseNode location, Executable executable) {
		try {
			executable.execute();
		} catch (InterpreterInternalException ex) {
			throw new LanguageRuntimeException(ex, location);
		} catch (LanguageRuntimeException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw new LanguageRuntimeException(ex, location);
		}
	}
}
