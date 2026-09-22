package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;

/**
 * Editor-only placeholder for a syntactically valid value name that has not
 * been declared yet. Its enclosing statement provides the completion scope.
 */
public final class UnresolvedReferenceNode extends ExpressionBaseNode {

    private final String name;

    public UnresolvedReferenceNode(String name) {
        this.name = name;
    }

    @Override
    protected Object execute(VirtualFrame frame) {
        throw new ValidationException("Variable or function \"" + name
                + "\" has not been declared before.", null, this);
    }
}
