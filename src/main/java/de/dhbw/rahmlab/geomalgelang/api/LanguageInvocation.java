/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.api;

import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_Generic;
import de.dhbw.rahmlab.geomalgelang.cga.Current_ICGAMultivector_Processor;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector_Processor_Concrete;
import java.io.IOException;
import java.util.Map;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 *
 * @author fabian
 */
public class LanguageInvocation {

	public static String invoke(String program, Map<String, Object> inputVars, ICGAMultivector_Processor_Concrete<?> concreteProcessor) throws IOException {
		Source source = Source.newBuilder("geomalgelang", program, "MATH")
			.build();
		String answer = invoke(source, inputVars, concreteProcessor);
		return answer;
	}

	public static String invoke(Source program, Map<String, Object> inputVars, ICGAMultivector_Processor_Concrete<?> concreteProcessor) {
		Current_ICGAMultivector_Processor.cga_processor = new CGAMultivector_Processor_Generic(concreteProcessor);

		Engine engine = Engine.create("geomalgelang");

		Context.Builder builder = Context.newBuilder("geomalgelang")
			.allowAllAccess(true)
			.engine(engine);

		Context context = builder.build();

		Value bindings = context.getBindings("geomalgelang");

		for (var var : inputVars.entrySet()) {
			bindings.putMember(var.getKey(), var.getValue());
		}

		String answer = null;

		try {
			Value value = context.eval(program);
			answer = value.toString();
		} finally {
			context.close();
		}

		return answer;
	}
}
