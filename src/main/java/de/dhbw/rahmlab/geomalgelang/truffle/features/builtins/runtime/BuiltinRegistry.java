package de.dhbw.rahmlab.geomalgelang.truffle.features.builtins.runtime;

import com.oracle.truffle.api.dsl.NodeFactory;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtins.nodes.exprSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.runtime.Function;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.nodes.FunctionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.expr.ReadFunctionArgument;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtins.nodes.expr.builtins.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class BuiltinRegistry {

	private final Map<String, Function> builtins = new HashMap<>();
	private final GeomAlgeLang truffleLanguage;

	public BuiltinRegistry(GeomAlgeLang truffleLanguage) {
		this.truffleLanguage = truffleLanguage;
		installBuiltins();
	}

	private void installBuiltins() {
		this.installBuiltin(InvoluteFactory.getInstance());
	}

	private void installBuiltin(NodeFactory<? extends BuiltinFunctionBody> factory) {
		String name = factory.getNodeClass().getSimpleName().toLowerCase();
		installBuiltin(name, factory);
	}

	private void installBuiltin(String name, NodeFactory<? extends BuiltinFunctionBody> factory) {
		final int arity = factory.getExecutionSignature().size();

		ReadFunctionArgument[] functionArguments = IntStream.range(0, arity)
			.mapToObj(i -> new ReadFunctionArgument(i))
			.toArray(ReadFunctionArgument[]::new);

		BuiltinFunctionBody builtinFunctionBody = factory.createNode((Object) functionArguments);
		FunctionRootNode functionRootNode = new FunctionRootNode(truffleLanguage, builtinFunctionBody);
		Function function = new Function(functionRootNode, name, arity);

		this.builtins.put(function.name, function);
	}

	public Function getBuiltinFunction(String name) {
		if (!this.builtins.containsKey(name)) {
			throw new GeomAlgeLangException("BuiltinFunction \"" + name + "\" does not exist.");
		}

		return this.builtins.get(name);
	}
}
