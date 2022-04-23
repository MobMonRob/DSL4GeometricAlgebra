/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.rahmlab.geomalgelang;

import java.io.StringWriter;
import org.antlr.v4.runtime.*;

public class GeomAlgeErrorListener extends BaseErrorListener {

	private String _symbol = "";
	private StringWriter _stream;

	public GeomAlgeErrorListener(StringWriter stream) {
		this._stream = stream;
	}

	public String getSymbol() {
		return _symbol;
	}

	public StringWriter getStream() {
		return _stream;
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
		Object offendingSymbol,
		int line,
		int charPositionInLine,
		String msg,
		RecognitionException e) {
		this._stream.write(msg);
		this._stream.write(System.getProperty("line.separator"));

		if (offendingSymbol.getClass().getName() == "org.antlr.v4.runtime.CommonToken") {
			CommonToken token = (CommonToken) offendingSymbol;
			this._symbol = token.getText();
		}
	}
;
}
