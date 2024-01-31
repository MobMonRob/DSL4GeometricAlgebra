package de.dhbw.rahmlab.geomalgelang._common.api;

import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Attrappe
 */
public class ThePathWrapperGenTestGen /* implements PathWrapper */ {

	protected iProgram program;

	public ThePathWrapperGenTestGen(iProgramFactory programFactory) {
		String path = "./test.ocga";
		try (InputStream in = ThePathWrapperGenTestGen.class.getResourceAsStream(path)) {
			if (in == null) {
				throw new RuntimeException(String.format("Path not found: %s", path));
			}
			try (var reader = new BufferedReader(new InputStreamReader(in))) {
				this.program = programFactory.parse(reader);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	// @Override
	public List<SparseDoubleColumnVector> test(SparseDoubleColumnVector a, SparseDoubleColumnVector b) {
		List<SparseDoubleColumnVector> arguments = new ArrayList<>();
		arguments.add(a);
		arguments.add(b);
		return this.program.invoke(arguments);
	}

	// @Override
	public List<SparseDoubleColumnVector> ik(SparseDoubleColumnVector a) {
		throw new UnsupportedOperationException();
	}
}
