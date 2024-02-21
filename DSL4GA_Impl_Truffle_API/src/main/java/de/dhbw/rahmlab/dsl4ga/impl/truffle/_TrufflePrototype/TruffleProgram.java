package de.dhbw.rahmlab.dsl4ga.impl.truffle._TrufflePrototype;

import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

public class TruffleProgram implements iProgram {

	public final Context context;

	public TruffleProgram(Context context) {
		this.context = context;
	}

	@Override
	public List<SparseDoubleColumnVector> invoke(List<SparseDoubleColumnVector> arguments) {
		System.out.print(" ");
		return null;
	}

	public void finalize() {
		System.out.print(".");
	}
}
