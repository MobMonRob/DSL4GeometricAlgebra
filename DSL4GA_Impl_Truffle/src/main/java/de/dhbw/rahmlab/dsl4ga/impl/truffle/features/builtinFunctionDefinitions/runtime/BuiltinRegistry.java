package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.runtime;

import com.oracle.truffle.api.dsl.NodeFactory;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.BuiltinFunctionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.arrayBuiltins.ConcatFactory;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.arrayBuiltins.RangeFactory;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.arrayBuiltins.ReversedFactory;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins.*;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.hofBuiltins.MapNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.hofBuiltins.MapaccumNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.hofBuiltins.MapfoldNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.expr.FunctionArgumentReader;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
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
		
		// in/out functions
		this.installBuiltin(UpFactory.getInstance());
		this.installBuiltin(DownFactory.getInstance());
		// scalar functions
		this.installBuiltin(AbsFactory.getInstance());
		this.installBuiltin(Atan2Factory.getInstance());
		// this.installBuiltin(GetLastListReturnFactory.getInstance()); // Only for internal use
		
		// NSELGA
		this.installBuiltin(NormalizeFactory.getInstance());
		this.installBuiltin(SqrtFactory.getInstance());
		this.installBuiltin(ExpFactory.getInstance());
		this.installBuiltin(LogFactory.getInstance());
		
		// new scalar functions
		this.installBuiltin(AcosFactory.getInstance());
		this.installBuiltin(AsinFactory.getInstance());
		this.installBuiltin(AtanFactory.getInstance());
		this.installBuiltin(CosFactory.getInstance());
		this.installBuiltin(SinFactory.getInstance());
		this.installBuiltin(TanFactory.getInstance());
		this.installBuiltin(SignFactory.getInstance());
		
		// new products
		this.installBuiltin(DotFactory.getInstance());
		this.installBuiltin(IpFactory.getInstance());
		this.installBuiltin(ScpFactory.getInstance());

		// Array
		this.installBuiltin(ReversedFactory.getInstance());
		this.installBuiltin(ConcatFactory.getInstance());
		this.installBuiltin(RangeFactory.getInstance());

		// HOF
		this.installBuiltin("map", MapNodeGen.create());
		this.installBuiltin("mapaccum", MapaccumNodeGen.create());
		this.installBuiltin("mapfold", MapfoldNodeGen.create());
	}

	private void installBuiltin(String name, AbstractFunctionBody funcBody) {
		BuiltinFunctionRootNode builtinFunctionRootNode = new BuiltinFunctionRootNode(truffleLanguage, funcBody, name);
		Function function = new Function(builtinFunctionRootNode, Function.UNKNOWN_ARITY);
		this.builtins.put(function.getName(), function);
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

	public boolean hasBuiltinFunction(String name) {
		return this.builtins.containsKey(name);
	}

	public Function getBuiltinFunction(String name) {
		if (!this.builtins.containsKey(name)) {
			throw new ValidationException("BuiltinFunction \"" + name + "\" does not exist.");
		}

		return this.builtins.get(name);
	}
}
