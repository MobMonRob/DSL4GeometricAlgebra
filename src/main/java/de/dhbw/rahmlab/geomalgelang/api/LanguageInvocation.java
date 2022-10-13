/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.api;

import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_Generic;
import de.dhbw.rahmlab.geomalgelang.cga.Current_ICGAMultivector_Processor;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector_Processor_Concrete;
import java.io.IOException;
import java.util.Map;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.SourceSection;
import org.graalvm.polyglot.Value;

/**
 *
 * @author fabian
 */
public class LanguageInvocation {

	public static <T> ICGAMultivector invoke(String program, Map<String, ICGAMultivector> inputVars, ICGAMultivector_Processor_Concrete<T> concreteProcessor) throws IOException {
		Source source = Source.newBuilder("geomalgelang", program, "MATH")
			.build();
		ICGAMultivector answer = invoke(source, inputVars, concreteProcessor);
		return answer;
	}

	public static <T> ICGAMultivector invoke(Source source, Map<String, ICGAMultivector> inputVars, ICGAMultivector_Processor_Concrete<T> concreteProcessor) {
		Current_ICGAMultivector_Processor.cga_processor = new CGAMultivector_Processor_Generic<T>(concreteProcessor);

		Engine engine = Engine.create("geomalgelang");

		Context.Builder builder = Context.newBuilder("geomalgelang")
			.allowAllAccess(true)
			.engine(engine);

		Context context = builder.build();

		Value program;
		try {
			program = context.parse(source);
			// parsing succeeded
		} catch (PolyglotException e) {
			if (e.isSyntaxError()) {
				SourceSection location = e.getSourceLocation();
				// syntax error detected at location
			} else {
				// other guest error detected
			}
			context.close();
			throw e;
		}

		Value bindings = context.getBindings("geomalgelang");

		for (var var : inputVars.entrySet()) {
			bindings.putMember(var.getKey(), var.getValue());
		}

		ICGAMultivector answer;

		try {
			// later: execute with arguments XOR getMember "main" and execute it with arguments (instead of bindings.putMember)
			Value result = program.execute();
			answer = result.as(ICGAMultivector.class);
			//answer = result.toString();
		} finally {
			// Will be executed regardless if an exception is thrown or not
			context.close();
		}

		return answer;
	}
}
