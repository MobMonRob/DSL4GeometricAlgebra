package de.dhbw.rahmlab.dsl4ga.common.parsing;

import java.io.PrintStream;
import java.util.BitSet;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;

public class CustumDiagnosticErrorListener extends DiagnosticErrorListener {

	protected final PrintStream printStream;

	public CustumDiagnosticErrorListener(PrintStream printStream) {
		this.printStream = printStream;
	}

	@Override
	public void reportAmbiguity(Parser recognizer,
		DFA dfa,
		int startIndex,
		int stopIndex,
		boolean exact,
		BitSet ambigAlts,
		ATNConfigSet configs) {

		String input = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
		String inputWithEscapedNewline = input.replace("\n", "\\n");

		// Innermost part of the context:
		String decision = getRuleDisplayName(recognizer, getDecisionRule(recognizer, dfa.decision));
		// String decision = super.getDecisionDescription(recognizer, dfa);

		// Kann es sein, dass das die Indizes der in der Grammatik mit | getrennten Subregeln sind?
		// -> Ja!
		// Dann muss ich nur da in die Grammatik rein schauen.
		BitSet conflictingAlts = super.getConflictingAlts(ambigAlts, configs);

		String context = recognizer.getContext().toString(recognizer);

		String format = "reportAmbiguity: input='%s'; decision='%s', conflictingAlts=%s; context (deepest part left)='%s'";
		String message = String.format(format, inputWithEscapedNewline, decision, conflictingAlts, context);

		printStream.println(message);
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer,
		DFA dfa,
		int startIndex,
		int stopIndex,
		BitSet conflictingAlts,
		ATNConfigSet configs) {

		String format = "reportAttemptingFullContext d=%s, input='%s'";
		String decision = super.getDecisionDescription(recognizer, dfa);
		String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
		String message = String.format(format, decision, text);

		printStream.println(message);
	}

	@Override
	public void reportContextSensitivity(Parser recognizer,
		DFA dfa,
		int startIndex,
		int stopIndex,
		int prediction,
		ATNConfigSet configs) {

		String format = "reportContextSensitivity d=%s, input='%s'";
		String decision = super.getDecisionDescription(recognizer, dfa);
		String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
		String message = String.format(format, decision, text);

		printStream.println(message);
	}

	// Thanks: https://stackoverflow.com/a/14753120
	public static int getDecisionRule(Recognizer<?, ?> recognizer, int decision) {
		if (recognizer == null || decision < 0) {
			return -1;
		}

		if (decision >= recognizer.getATN().decisionToState.size()) {
			return -1;
		}

		return recognizer.getATN().decisionToState.get(decision).ruleIndex;
	}

	// Thanks: https://stackoverflow.com/a/14753120
	public static String getRuleDisplayName(Recognizer<?, ?> recognizer, int ruleIndex) {
		if (recognizer == null || ruleIndex < 0) {
			return Integer.toString(ruleIndex);
		}

		String[] ruleNames = recognizer.getRuleNames();
		if (ruleIndex < 0 || ruleIndex >= ruleNames.length) {
			return Integer.toString(ruleIndex);
		}

		return ruleNames[ruleIndex];
	}
}
