package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.dsl.NodeChild;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;

@NodeChild(value = "argument", type = ExpressionBaseNode.class)
public abstract class UnaryOp extends ExpressionBaseNode {
}
