/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang;

import java.io.IOException;
import java.util.Map;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 *
 * @author fabian
 */
public class LanguageInvocation {

	public static String invoke(String program, Map<String, Object> inputVars) throws IOException {
		Context context = Context.newBuilder("geomalgelang")
			.allowAllAccess(true)
			.build();
		Source source = Source.newBuilder("geomalgelang", program, "MATH")
			.build();

		Value bindings = context.getBindings("geomalgelang");

		for (var var : inputVars.entrySet()) {
			bindings.putMember(var.getKey(), var.getValue());
		}

		String answer = null;

		try {
			Value value = context.eval(source);
			answer = value.toString();
		} finally {
			context.close();
		}

		return answer;
	}
}
