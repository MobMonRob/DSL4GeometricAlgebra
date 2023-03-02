package de.dhbw.rahmlab.geomalgelang.parsing;

import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class SyntaxErrorListener extends BaseErrorListener {

	public static SyntaxErrorListener INSTANCE = new SyntaxErrorListener();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {

		GeomAlgeParser parser = (GeomAlgeParser) recognizer;
		List<String> stack = parser.getRuleInvocationStack();
		Collections.reverse(stack);
		String stackString = String.join("\n", stack);

		Token token = ((Token) offendingSymbol);
		String tokenText = token.getText();

		String tokenSource = token.getTokenSource().getInputStream().toString();

		String before = tokenSource.substring(0, charPositionInLine);
		String after = tokenSource.substring(charPositionInLine + 1, tokenSource.length());

		// StringBuilder b;
		String errorMessage = String.format("line %s:%s at token \'%s\': %s\nbefore token: \'%s\'\n_______token: \'%s\'\n_after token: \'%s\'", line, charPositionInLine, tokenText, msg, before, tokenText, after);

		throw new ParseCancellationException(errorMessage + "\n" + stackString, e);
	}
}
