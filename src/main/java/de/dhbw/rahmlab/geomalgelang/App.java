package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.api.LanguageInvocation;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAScalar;
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
		// String program = "a b";
		// String program = "normalize(a b)";
		// String program = "normalize()";
		String program = "normalize(a b)";

		Map<String, CGAMultivector> inputVars = new HashMap<>();
		CGAMultivector a = new CGAScalar(5.0);
		inputVars.put("a", a);
		inputVars.put("b", a);

		CGAMultivector answer = LanguageInvocation.invoke(program, inputVars);
		String answerString = answer.toString();

		System.out.println("program: " + program);
		System.out.println("variable assignments: ");
		inputVars.forEach((name, value) -> {
			String varString = "\t" + name + " := " + value.toString();
			System.out.println(varString);
		});
		System.out.println("answer: " + answerString);
		System.out.println();
	}
}
