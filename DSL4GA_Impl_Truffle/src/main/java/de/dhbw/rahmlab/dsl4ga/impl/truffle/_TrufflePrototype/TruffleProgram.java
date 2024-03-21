package de.dhbw.rahmlab.dsl4ga.impl.truffle._TrufflePrototype;

import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;

public class TruffleProgram implements iProgram {

	public final Context context;

	public TruffleProgram(Context context) {
		this.context = context;
	}

	@Override
	public List<SparseDoubleMatrix> invoke(List<SparseDoubleMatrix> arguments) {
		System.out.print(" ");
		return null;
	}

	public void finalize() {
		System.out.print(".");
	}
}
