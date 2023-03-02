package de.dhbw.rahmlab.geomalgelang.parsing;

import java.io.PrintStream;
import java.util.BitSet;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Parser;
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

		String format = "reportAmbiguity d=%s: ambigAlts=%s, input='%s'";
		String decision = super.getDecisionDescription(recognizer, dfa);
		BitSet conflictingAlts = super.getConflictingAlts(ambigAlts, configs);
		String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
		String message = String.format(format, decision, conflictingAlts, text);

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
}
