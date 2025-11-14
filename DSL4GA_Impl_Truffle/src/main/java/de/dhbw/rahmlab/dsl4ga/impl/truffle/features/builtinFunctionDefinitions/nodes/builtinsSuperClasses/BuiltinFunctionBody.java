package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.expr.FunctionArgumentReader;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = FunctionArgumentReader[].class)
public abstract class BuiltinFunctionBody extends AbstractFunctionBody {

}
