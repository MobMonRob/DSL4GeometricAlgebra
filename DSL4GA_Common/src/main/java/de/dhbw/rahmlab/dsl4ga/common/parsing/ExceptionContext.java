package de.dhbw.rahmlab.dsl4ga.common.parsing;

public class ExceptionContext {

	public final int fromIndex;
	public final int toIndexInclusive;

	public ExceptionContext(int fromIndex, int toIndexInclusive) {
		this.fromIndex = fromIndex;
		this.toIndexInclusive = toIndexInclusive;
	}
}
