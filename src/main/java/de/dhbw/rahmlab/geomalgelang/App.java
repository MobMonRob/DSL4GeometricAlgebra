package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.api.LanguageInvocation;
import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_CGAMultivector;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class App {

	public static void main(String[] args) throws Exception {
		//encodingTest();
		invocationTest();
	}

	private static void encodingTest() {
		System.out.println(System.getProperty("stdout.encoding"));
		System.out.println(System.getProperty("sun.stdout.encoding"));
		System.out.println(System.getProperty("native.encoding"));
		System.out.println(Charset.defaultCharset().toString());
		System.out.println(System.getProperty("file.encoding"));

		System.out.println("ä π");
	}

	private static void invocationTest() throws Exception {
		CGAMultivector_Processor_CGAMultivector cgaMultivector_Processor_CGAMultivector = new CGAMultivector_Processor_CGAMultivector();

		// String program = "a b";
		// String program = "involuten(a b)";
		// String program = "involuten()";
		// String program = "involuten(a,b)";
		String program = "involute(a b)";

		Map<String, ICGAMultivector> inputVars = new HashMap<>();
		ICGAMultivector a = new ICGAMultivector(cgaMultivector_Processor_CGAMultivector.create(5.0));
		inputVars.put("a", a);
		inputVars.put("b", a);

		ICGAMultivector answer = LanguageInvocation.invoke(program, inputVars, cgaMultivector_Processor_CGAMultivector);
		String answerString = answer.getInner().toString();

		System.out.println("program: " + program);
		System.out.println("variable assignments: ");
		for (var inputVar : inputVars.entrySet()) {
			String varString = "\t" + inputVar.getKey() + " := " + inputVar.getValue().getInner().toString();
			System.out.println(varString);
		}
		System.out.println("answer: " + answerString);
		System.out.println();
	}
}
