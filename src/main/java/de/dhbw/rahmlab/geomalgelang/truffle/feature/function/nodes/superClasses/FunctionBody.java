package de.dhbw.rahmlab.geomalgelang.truffle.feature.function.nodes.superClasses;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import de.dhbw.rahmlab.geomalgelang.truffle.feature.functionCall.nodes.expr.ReadFunctionArgument;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.ExpressionBaseNode;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = ReadFunctionArgument[].class)
public abstract class FunctionBody extends ExpressionBaseNode {

}
