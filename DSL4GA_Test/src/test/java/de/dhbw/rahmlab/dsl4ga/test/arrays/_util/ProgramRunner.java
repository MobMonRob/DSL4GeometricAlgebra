package de.dhbw.rahmlab.dsl4ga.test.arrays._util;

import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.List;

public class ProgramRunner {
	private final List<SparseDoubleMatrix> noArguments = new ArrayList<>();
	private final List<String> answerStrings = new ArrayList<>();
	private final ImplementationSpecifics specifics;
	private final iProgramFactory factory;
	
	public ProgramRunner(ImplementationSpecifics specifics) {
		this.specifics = specifics;
		this.factory = specifics.buildFactory();

	}
		
	public void parseAndRun(String code){
		iProgram prog = factory.parse(code);
		List<? extends Object> answers = prog.invoke(noArguments);
		answers.forEach(answer -> {
			answerStrings.add(specifics.createMultivectorString(answer));
		});
	}

	public List<String> getAnswerStrings() {
		return answerStrings;
	}	
}
