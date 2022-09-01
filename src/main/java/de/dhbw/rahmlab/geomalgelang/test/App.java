package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.api.LanguageInvocation;
import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_CGA1Multivector;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class App {

	public static void main(String[] args) throws Exception {
		//String program = "a ε₀ ⋅ (1.5 + 5.3)†";
		String program = "a (b) c";

		System.out.println("inputed program: " + program);

		parseTestExtended(program);
		//parseTestSimple(program);
		//invocationTest(program);
		//charsetTest();
	}

	private static void charsetTest() throws Exception {
		String test = "*"; //FE FF 03 B5 20 81 00 00 00
		//String test = "" + '\u03b5' + '\u2081'; //FE FF 03 B5 20 81 00 00 00
		ByteBuffer buf = StandardCharsets.UTF_16.encode(test);
		byte[] byteArray = buf.array();
		StringBuilder hex = new StringBuilder(byteArray.length * 2);
		for (byte b : byteArray) {
			hex.append(String.format("%02X ", b));
		}
		System.out.println(hex.toString());
	}

	private static void invocationTest(String program) throws Exception {
		Map<String, Object> inputVars = new HashMap<>();
		Double a = 5.0;
		inputVars.put("a", a);
		inputVars.put("b", a);

		String answer = LanguageInvocation.invoke(program, inputVars, new CGAMultivector_Processor_CGA1Multivector());
		System.out.println("answer: " + answer);
		System.out.println();
	}

	private static void parseTestSimple(String program) throws Exception {
		ParsingService parsingService = new ParsingService(program);
		parsingService.processANTLRTestRig();
	}

	private static void parseTestExtended(String program) throws Exception {
		String astString = ParsingService.getAstString(program);
		System.out.println(astString);
	}
}
