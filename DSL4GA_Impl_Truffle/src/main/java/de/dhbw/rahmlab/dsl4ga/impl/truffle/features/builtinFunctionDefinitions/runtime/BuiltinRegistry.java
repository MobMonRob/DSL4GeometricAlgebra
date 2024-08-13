package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.runtime;

import com.oracle.truffle.api.dsl.NodeFactory;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.BuiltinFunctionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins.*;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionArgumentReader;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
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
		this.installBuiltin(GetLastListReturnFactory.getInstance());
	}

	private void installBuiltin(NodeFactory<? extends BuiltinFunctionBody> factory) {
		String simpleName = factory.getNodeClass().getSimpleName();
		String first = simpleName.substring(0, 1).toLowerCase();
		String residue = simpleName.substring(1, simpleName.length());
		String name = first + residue;
		installBuiltin(name, factory);
	}

	private void installBuiltin(String name, NodeFactory<? extends BuiltinFunctionBody> factory) {
		final int arity = factory.getExecutionSignature().size();

		FunctionArgumentReader[] functionArguments = FunctionArgumentReader.createArray(0, arity);

		BuiltinFunctionBody builtinFunctionBody = factory.createNode((Object) functionArguments);
		BuiltinFunctionRootNode builtinFunctionRootNode = new BuiltinFunctionRootNode(truffleLanguage, builtinFunctionBody, name);
		Function function = new Function(builtinFunctionRootNode, arity);

		this.builtins.put(function.getName(), function);
	}

	public Function getBuiltinFunction(String name) throws InterpreterInternalException {
		if (!this.builtins.containsKey(name)) {
			throw new InterpreterInternalException("BuiltinFunction \"" + name + "\" does not exist.");
		}

		return this.builtins.get(name);
	}
}
