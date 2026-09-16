package de.dhbw.rahmlab.dsl4ga.impl.truffle;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;

public class CTest {

	public static void main(String[] args) {
		String path = "./ctest.ocga";
		var url = CTest.class.getResource(path);
		if (url == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}

		var fac = new TruffleProgramFactory();
		var prog = fac.parse(url);
		var func = prog.invokeSymAsFunction();
		// "" as path: output will be in DSL4GA_Impl_Truffle directory.
		func.generateC("", "gen.c");
	}
}
