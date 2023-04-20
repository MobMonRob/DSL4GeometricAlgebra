package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import java.util.List;

public class MethodInvocationRepresentation {

	public final MethodRepresentation method;
	public final String cgaVarName;
	public final List<String> arguments; //unmodifiable Collections

	public MethodInvocationRepresentation(MethodRepresentation method, String cgaVarName, List<String> arguments) {
		this.method = method;
		this.cgaVarName = cgaVarName;
		this.arguments = arguments;
	}
}
