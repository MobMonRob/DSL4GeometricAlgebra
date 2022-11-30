package de.dhbw.rahmlab.geomalgelang.truffle.feature.builtins.runtime;

import com.oracle.truffle.api.dsl.NodeFactory;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLangException;
import de.dhbw.rahmlab.geomalgelang.truffle.feature.builtins.nodes.superClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.feature.function.runtime.Function;
import de.dhbw.rahmlab.geomalgelang.truffle.feature.builtins.nodes.expr.builtins.*;
import de.dhbw.rahmlab.geomalgelang.truffle.feature.function.nodes.FunctionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.feature.functionCall.nodes.expr.ReadFunctionArgument;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class BuiltinRegistry {

	private final Map<String, Function> builtins = new HashMap<>();
	private final GeomAlgeLang truffleLanguage;

	public BuiltinRegistry(GeomAlgeLang truffleLanguage) {
		this.truffleLanguage = truffleLanguage;
		installBuildins();
	}

	private void installBuildins() {
		this.installBuildin(InvoluteFactory.getInstance());
	}

	private void installBuildin(NodeFactory<? extends BuiltinFunctionBody> factory) {
		ReadFunctionArgument[] functionArguments = IntStream.range(0, factory.getExecutionSignature().size())
			.mapToObj(i -> new ReadFunctionArgument(i))
			.toArray(ReadFunctionArgument[]::new);

		BuiltinFunctionBody builtinFunctionBody = factory.createNode((Object[]) functionArguments);
		FunctionRootNode functionRootNode = new FunctionRootNode(truffleLanguage, builtinFunctionBody);
		Function function = new Function(functionRootNode);

		this.builtins.put(function.name, function);
	}

	public Function getBuiltinFunction(String name) {
		if (!this.builtins.containsKey(name)) {
			throw new GeomAlgeLangException("BuiltinFunction \"" + name + "\" does not exist.");
		}

		return this.builtins.get(name);
	}
}
