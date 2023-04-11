package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.FunctionArgumentReader;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.FunctionBody;
import de.orat.math.cga.api.CGAMultivector;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = FunctionArgumentReader[].class)
public abstract class BuiltinFunctionBody extends FunctionBody {

	public abstract CGAMultivector executeGeneric(VirtualFrame frame);
}
