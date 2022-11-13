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

	private CharStream charStream;

	private CharStreamSupplier(CharStream charStream) {
		this.charStream = charStream;
	}

	public static CharStreamSupplier from(CharStream charStream) {
		return new CharStreamSupplier(charStream);
	}

	public static CharStreamSupplier from(String input) {
		return new CharStreamSupplier(CharStreams.fromString(input));
	}

	public static CharStreamSupplier from(Source input) throws IOException {
		return new CharStreamSupplier(CharStreams.fromReader(input.getReader()));
	}

	public CharStream get() {
		return this.charStream;
	}
}
