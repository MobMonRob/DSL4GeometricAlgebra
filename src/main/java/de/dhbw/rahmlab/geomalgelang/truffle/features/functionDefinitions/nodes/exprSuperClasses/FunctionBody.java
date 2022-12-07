package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.exprSuperClasses;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionArgumentReader;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = FunctionArgumentReader[].class)
public abstract class FunctionBody extends ExpressionBaseNode {

}
