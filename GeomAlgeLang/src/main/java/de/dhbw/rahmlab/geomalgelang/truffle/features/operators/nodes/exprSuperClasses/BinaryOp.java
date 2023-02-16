package de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.dsl.NodeChild;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.cga.api.CGAMultivector;

@NodeChild("leftArgument")
@NodeChild("rightArgument")
public abstract class BinaryOp extends ExpressionBaseNode {

	protected abstract CGAMultivector execute(CGAMultivector left, CGAMultivector right);
}
