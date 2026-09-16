package de.dhbw.rahmlab.dsl4ga.impl.truffle;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgram;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.gacalc.api.GAFunction;
import java.net.URL;

public class CTest {

	public static void main(String[] args) {
		String path = "./ctest.ocga";
		URL url = CTest.class.getResource(path);
		if (url == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}

		TruffleProgramFactory fac = new TruffleProgramFactory();
		TruffleProgram prog = fac.parse(url);
		GAFunction func = prog.invokeSymAsFunction();
		// "" as path: output will be in DSL4GA_Impl_Truffle directory.
		func.generateC("", "gen.c");
	}
}
