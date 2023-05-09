package de.dhbw.rahmlab.geomalgelang.parsing;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

public class SyntaxErrorListener extends BaseErrorListener {

	public static SyntaxErrorListener INSTANCE = new SyntaxErrorListener();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {
		GeomAlgeParser parser = (GeomAlgeParser) recognizer;
		Token token = (Token) offendingSymbol;

		StringBuilder errorMessageBuilder = new StringBuilder();

		String tokenText = token.getText().replace("\n", "\\n");
		errorMessageBuilder
			.append(String.format("\nline: %s, position: %s, token: \'%s\', msg: \'%s\'", line, charPositionInLine, tokenText, msg));

		List<String> lines = token.getInputStream().toString().lines().toList();
		int linesSize = lines.size();
		// Anltr4 line starts with 1; lines.get() starts with 0

		int previousLine = line - 1;
		if (previousLine > 0 && previousLine <= linesSize) {
			String previousLineString = lines.get(previousLine - 1);
			errorMessageBuilder
				.append(String.format("\nprevious line (%s): %s", previousLine, previousLineString));
		}

		if (line <= linesSize) {
			String lineString = lines.get(line - 1);
			String before = lineString.substring(0, charPositionInLine);
			errorMessageBuilder
				.append(String.format("\nbefore token: \'%s\'", before))
				.append(String.format("\n_______token: \'%s\'", tokenText));

			int lineStringLenght = lineString.length();
			int afterCharPositionInLine = charPositionInLine + 1;
			if (afterCharPositionInLine < lineStringLenght) {
				String after = lineString.substring(afterCharPositionInLine, lineStringLenght);
				errorMessageBuilder
					.append(String.format("\n_after token: \'%s\'", after));
			}
		}

		int nextLine = line + 1;
		if (nextLine <= linesSize) {
			String nextLineString = lines.get(nextLine - 1);
			errorMessageBuilder
				.append(String.format("\nnext line (%s): %s", nextLine, nextLineString));
		}

		// Dann würde ich eigentlich erwarten, dass es mir anzeigt in diesem Fall, dass es keine retExpr gefunden hat.
		String escapedTokens = parser.getExpectedTokensWithinCurrentRule().toString(GeomAlgeParser.VOCABULARY);
		String expectedTokens = GrammarUtils.UnicodeUtils.unescapeUnicode(escapedTokens);
		errorMessageBuilder
			.append(String.format("\nexpected Tokens: %s", expectedTokens));

		ParserRuleContext context = parser.getContext();

		errorMessageBuilder
			.append(String.format("\ncontext (deepest part left): %s", context.toString(parser)));

		List<ParseTree> contextChildren = context.children;
		if (contextChildren != null) {

			HashSet<Class<? extends ParserRuleContext>> childrenContexts = contextChildren.stream()
				.filter(c -> c instanceof ParserRuleContext)
				.map(c -> ((ParserRuleContext) c).getClass())
				.collect(Collectors.toCollection(HashSet::new));

			HashSet<Method> parserRuleContextMethods = new HashSet<>(Arrays.asList(ParserRuleContext.class.getMethods()));
			// m.getReturnType instanceof ParserRuleContext
			HashMap<Class<?>, List<Method>> ruleContextMethodReturnTypeClasses = Stream.of(context.getClass().getMethods())
				.filter(m -> !parserRuleContextMethods.contains(m))
				.filter(m -> ParserRuleContext.class.isAssignableFrom(m.getReturnType()))
				.collect(Collectors.groupingBy(m -> m.getReturnType(), HashMap::new, Collectors.toList()));

			// returnTypeClasses with NO available childrenContext, such that childrenContext is instanceof the returnTypeClass
			List<Method> missingNonTerminalSymbols = ruleContextMethodReturnTypeClasses.entrySet().stream()
				.filter(returnTypeClassEntry -> !childrenContexts.stream().anyMatch(childrenContext -> returnTypeClassEntry.getKey().isAssignableFrom(childrenContext)))
				.flatMap(entry -> entry.getValue().stream())
				.toList();

			if (!missingNonTerminalSymbols.isEmpty()) {
				errorMessageBuilder
					.append(String.format("\nmissing subcontexts: "));
			}
			for (var missing : missingNonTerminalSymbols) {
				errorMessageBuilder
					.append(String.format("\n- %s", missing.getName()));
			}
		}

		/*
		List<String> stack = parser.getRuleInvocationStack();
		Collections.reverse(stack);
		String stackString = String.join("\n", stack);
		errorMessageBuilder
			.append("\nRule invocation stack:")
			.append("\n")
			.append(stackString);
		 */
		String errorMessage = errorMessageBuilder.toString();
		throw new ParseCancellationException(errorMessage, e);
	}
}
