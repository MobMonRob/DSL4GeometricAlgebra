package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.runtime;

import com.oracle.truffle.api.dsl.NodeFactory;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.BuiltinFunctionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins.AbsFactory;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins.Atan2Factory;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins.ExpFactory;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins.NormalizeFactory;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins.SqrtFactory;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionArgumentReader;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import java.util.HashMap;
import java.util.Map;

public class BuiltinRegistry {

	private final Map<String, Function> builtins = new HashMap<>();
	private final GeomAlgeLang truffleLanguage;

	public BuiltinRegistry(GeomAlgeLang truffleLanguage) {
		this.truffleLanguage = truffleLanguage;
		installBuiltins();
	}

	private void installBuiltins() {
		this.installBuiltin(AbsFactory.getInstance());
		this.installBuiltin(Atan2Factory.getInstance());
		this.installBuiltin(ExpFactory.getInstance());
		this.installBuiltin(NormalizeFactory.getInstance());
		this.installBuiltin(SqrtFactory.getInstance());
	}

	private void installBuiltin(NodeFactory<? extends BuiltinFunctionBody> factory) {
		String name = factory.getNodeClass().getSimpleName().toLowerCase();
		installBuiltin(name, factory);
	}

	private void installBuiltin(String name, NodeFactory<? extends BuiltinFunctionBody> factory) {
		final int arity = factory.getExecutionSignature().size();

		FunctionArgumentReader[] functionArguments = FunctionArgumentReader.createArray(0, arity);

		BuiltinFunctionBody builtinFunctionBody = factory.createNode((Object) functionArguments);
		BuiltinFunctionRootNode builtinFunctionRootNode = new BuiltinFunctionRootNode(truffleLanguage, builtinFunctionBody);
		Function function = new Function(builtinFunctionRootNode, name, arity);

		this.builtins.put(function.name, function);
	}

	public Function getBuiltinFunction(String name) throws InterpreterInternalException {
		if (!this.builtins.containsKey(name)) {
			throw new InterpreterInternalException("BuiltinFunction \"" + name + "\" does not exist.");
		}

		return this.builtins.get(name);
	}
}
