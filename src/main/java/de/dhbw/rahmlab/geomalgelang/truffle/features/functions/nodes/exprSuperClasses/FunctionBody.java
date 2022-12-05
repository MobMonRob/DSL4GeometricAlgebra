package de.dhbw.rahmlab.geomalgelang.truffle.features.functions.nodes.exprSuperClasses;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.expr.ReadFunctionArgument;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = ReadFunctionArgument[].class)
public abstract class FunctionBody extends ExpressionBaseNode {

}
