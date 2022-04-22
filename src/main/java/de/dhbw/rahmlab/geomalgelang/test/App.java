package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.api.LanguageInvocation;
import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_CGA1Multivector;
import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_Generic;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.debug.AntlrTestRig;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.orat.math.cga.impl1.CGA1Multivector;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.*;

public class App {

	public static void main(String[] args) throws Exception {
		//System.setOut(new PrintStream(System.out, true, "UTF8"));

		//String test = "ε₁"; //FE FF 03 B5 20 81 00 00 00
		String test = "" + '\u03b5' + '\u2081'; //FE FF 03 B5 20 81 00 00 00
		ByteBuffer buf = StandardCharsets.UTF_16.encode(test);
		byte[] byteArray = buf.array();
		StringBuilder hex = new StringBuilder(byteArray.length * 2);
		for (byte b : byteArray) {
			hex.append(String.format("%02X ", b));
		}
		System.out.println(hex.toString());

		//String program = "a b ⋅ (ε₁ + ε₂)†* * ∞";
		String program = "a *b "; //richtig
		//->Was ist oben die Erwartung? BinaryOp oder Fehler?
		//Eigentlich BinaryOp. Also doch richtig.
		//String program = "(a *)b "; //richtig (Nicht in der Sprache)
		//String program = "a * b "; //richtig
		//String program = "(a*) b"; //richtig
		//String program = "a* b"; //richtig
		//String program = "a*  b"; //richtig
		//String program = "(a*)  b"; //richtig
		System.out.println("inputed program: " + program);

		parseTest(program);
		invocationTest(program);
		//otherTest(program);
	}

	private static void otherTest(String program) throws Exception {
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

	private static void parseTest(String program) throws Exception {
		CodePointCharStream inputStream = CharStreams.fromString(program);
		GeomAlgeLexer lexer = new GeomAlgeLexer(inputStream);
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);

		AntlrTestRig testRig = new AntlrTestRig();
		testRig.process(lexer, parser, inputStream, "program");
	}
}
