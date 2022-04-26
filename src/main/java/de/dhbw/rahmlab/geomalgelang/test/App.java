package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.api.LanguageInvocation;
import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_CGA1Multivector;
import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_Generic;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.BaseNode;
import de.orat.math.cga.impl1.CGA1Multivector;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class App {

	public static void main(String[] args) throws Exception {
		String program = "a b ⋅ (1,5 + 5,3)†";
		//................
		// Folgend nur STAR Grammatik + Semantik.
		// Behalten für regression Testcases!
		// Tokenkette // Ergebnis -> Anmerkung
		//String program = "a*b"; //binaryOp STAR -> passt
		//String program = "a* b"; //binaryOp STAR -> Fehler
		//String program = "a*  b"; //binaryOp STAR -> Fehler
		//String program = "a *b"; //binaryOp STAR -> passt
		//String program = "a  *b"; //binaryOp STAR -> passt
		//String program = "a * b"; //binaryOp STAR -> passt
		//String program = "a  * b"; //binaryOp STAR -> passt
		//String program = "a *  b"; //binaryOp STAR -> passt
		//String program = "a(*)b"; //binaryOp STAR -> Fehler. Sollte kein Wort sein.
		//String program = "(a*)b"; //binaryOp STAR; kein Wort -> Je nachdem, wie man das mit dem SPACE Operator sieht.
		//String program = "(a*) b"; //unaryOp STAR, binaryOP SPACE -> passt
		//String program = "(a* )b"; //unaryOp STAR; Wort, aber ohne Operator vor B -> total falsch
		//String program = "(a* ) b"; //unaryOp STAR, binaryOP SPACE -> passt
		//String program = "(a *)b"; //unaryOp STAR; kein Wort -> Je nachdem, wie man das mit dem SPACE Operator sieht.
		//String program = "(a *) b"; //unaryOp START, binaryOP SPACE -> passt
		System.out.println("inputed program: " + program);

		parseTestExtended(program);
		//parseTestSimple(program);
		//invocationTest(program);
		//CGAAdapterTest(program);
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

	private static void CGAAdapterTest(String program) throws Exception {
		CGAMultivector_Processor_CGA1Multivector concreteProcessor = new CGAMultivector_Processor_CGA1Multivector();
		CGAMultivector_Processor_Generic genericProcessor = new CGAMultivector_Processor_Generic(concreteProcessor);

		CGA1Multivector innerst = new CGA1Multivector();
		ICGAMultivector wrapper = new ICGAMultivector(innerst);

		ICGAMultivector result = genericProcessor.meet(wrapper, wrapper);
	}

	private static void invocationTest(String program) throws Exception {
		Map<String, Object> inputVars = new HashMap<>();
		Double a = 5.0;
		inputVars.put("a", a);
		inputVars.put("b", a);

		String answer = LanguageInvocation.invoke(program, inputVars);
		System.out.println("answer: " + answer);
		System.out.println();
	}

	private static void parseTestSimple(String program) throws Exception {
		ParsingService parsingService = new ParsingService(program);
		parsingService.processANTLRTestRig();
	}

	private static void parseTestExtended(String program) throws Exception {
		ParsingService parsingService = new ParsingService(program);
		BaseNode root = parsingService.getTruffleTopNode();
	}
}
