package de.orat.math.graalvmlsp;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgram;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.gacalc.api.GAFunction;
import javax.swing.SwingUtilities;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CCodeGenerator {
	
	private CCodeGenerator(){}
	
	public static void main(String[] args) throws InterruptedException {
		TruffleProgramFactory fac = new TruffleProgramFactory();
		TruffleProgram prog = fac.parse(url);
		GAFunction func = prog.invokeSymAsFunction();
		// func.generateC(folderPath, fileName+".c");
		func.generateC("", fileName + ".c");

	}
}
