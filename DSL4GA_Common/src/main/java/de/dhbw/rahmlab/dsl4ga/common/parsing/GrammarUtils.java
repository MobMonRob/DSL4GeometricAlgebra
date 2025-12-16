package de.dhbw.rahmlab.dsl4ga.common.parsing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.TerminalNode;

public class GrammarUtils {

	// public static final Set<String> constantsLiteralNames = Collections.unmodifiableSet(computeConstantsLiteralNames());

	// Credits: https://stackoverflow.com/questions/11145681/how-to-convert-a-string-with-unicode-encoding-to-a-string-of-letters/63746515#63746515
	protected static class UnicodeUtils {

		protected static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");

		protected static String unescapeUnicode(String unescaped) {
			return UNICODE_PATTERN.matcher(unescaped).replaceAll(r -> String.valueOf((char) Integer.parseInt(r.group(1), 16)));
		}
	}

	// Code correct. But not needed anymore. Constants are not set by the lexer anymore.
	/*
	protected static HashSet<String> computeConstantsLiteralNames() {
		Vocabulary vocabulary = GeomAlgeParser.VOCABULARY;
		int max = vocabulary.getMaxTokenType();

		HashSet<String> constantsSymbolicNames = Stream.of(GeomAlgeParser.LiteralConstantContext.class.getDeclaredMethods())
			.filter(m -> m.getReturnType().equals(TerminalNode.class))
			.map(m -> m.getName())
			.collect(Collectors.toCollection(HashSet::new));

		HashSet<String> constantsLiteralNames_local = new HashSet<>(constantsSymbolicNames.size());
		for (int i = 0; i <= max; ++i) {
			String symbolicName = vocabulary.getSymbolicName(i);
			if (constantsSymbolicNames.contains(symbolicName)) {
				String literalName = vocabulary.getLiteralName(i);
				// remove ''
				literalName = literalName.substring(1, literalName.length() - 1);
				// unescapeUnicode
				literalName = UnicodeUtils.unescapeUnicode(literalName);

				constantsLiteralNames_local.add(literalName);
			}
		}

		return constantsLiteralNames_local;
	}
	 */
}
