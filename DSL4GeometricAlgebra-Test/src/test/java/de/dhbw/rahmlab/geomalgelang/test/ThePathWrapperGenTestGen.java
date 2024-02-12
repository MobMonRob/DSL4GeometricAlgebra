package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang._common.api.iProgram;
import de.dhbw.rahmlab.geomalgelang._common.api.iProgramFactory;
import de.dhbw.rahmlab.geomalgelang.test.common.PathWrapper;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Attrappe
 */
public final class ThePathWrapperGenTestGen<T extends iProgram> {

	private final T program;

	public ThePathWrapperGenTestGen(iProgramFactory<T> programFactory) {
		String path = "./test.ocga";
		try (var in = PathWrapper.class.getResourceAsStream(path)) {
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

	public List<SparseDoubleColumnVector> test(SparseDoubleColumnVector a, SparseDoubleColumnVector b) {
		var arguments = Arrays.asList(a, b);
		return this.program.invoke(arguments);
	}
}
