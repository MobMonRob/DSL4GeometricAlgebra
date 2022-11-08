/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing;

import com.oracle.truffle.api.source.Source;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

/**
 *
 * @author fabian
 */
public class CharStreamSupplier {

	private CharStreamSupplier() {
	}

	public static CharStream get(String input) {
		return CharStreams.fromString(input);
	}

	public static CharStream get(Source input) throws IOException {
		return CharStreams.fromReader(input.getReader());
	}
}
