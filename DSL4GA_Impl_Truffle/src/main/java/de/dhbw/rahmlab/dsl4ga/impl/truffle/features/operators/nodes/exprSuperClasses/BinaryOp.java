package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.dsl.NodeChild;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;

@NodeChild(value = "argumentLeft", type = ExpressionBaseNode.class)
@NodeChild(value = "argumentRight", type = ExpressionBaseNode.class)
public abstract class BinaryOp extends ExpressionBaseNode {
}
