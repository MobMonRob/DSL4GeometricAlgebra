/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.runtime;

import com.oracle.truffle.api.interop.UnsupportedTypeException;
import de.dhbw.rahmlab.geomalgelang.cga.Current_ICGAMultivector_Processor;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;

/**
 *
 * @author fabian
 */
public abstract class InputValidator {

	public static void ensureIsCGA(Object object) throws UnsupportedTypeException {
		if (!Current_ICGAMultivector_Processor.cga_processor.isCGA(object)) {
			throw new IllegalArgumentException("\"" + object.getClass().getSimpleName() + "\" is not a proper cga type");
		}
	}

	// This construction is used to get compiler errors for at least some incompatible changes in the Lexer.
	private static final String SMALL_O = GeomAlgeLexer.VOCABULARY.getLiteralName(GeomAlgeParser.SMALL_O).replaceAll("\'", "");
	private static final String SMALL_N = GeomAlgeLexer.VOCABULARY.getLiteralName(GeomAlgeParser.SMALL_N).replaceAll("\'", "");

	public static void ensureIsValidVariableName(String name) {
		if (name.equals(SMALL_O) || name.equals(SMALL_N)) {
			StringBuilder message = new StringBuilder();
			message
				.append("\"")
				.append(name)
				.append("\" is not a valid variable name because it is used as a constant symbol. Therefore assignment is not allowed.");
			throw new IllegalArgumentException(message.toString());
		}
	}
}
