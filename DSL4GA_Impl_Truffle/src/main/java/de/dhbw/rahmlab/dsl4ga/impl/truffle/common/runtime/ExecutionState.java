package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.ArgsMapper;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Thread-bound state for one running GA program.
 *
 * <p>Parsing has a separate stack in {@link GeomAlgeLangContext}: source
 * sections are needed while constructing ASTs, whereas this state is needed
 * while executing already constructed ASTs.</p>
 */
final class ExecutionState {

    private final ThreadLocal<Deque<ArgsMapper>> externalArguments
            = ThreadLocal.withInitial(ArrayDeque::new);
    private final ThreadLocal<Deque<DocumentState>> activeDocuments
            = ThreadLocal.withInitial(ArrayDeque::new);

    void pushExternalArguments(ArgsMapper arguments) {
        externalArguments.get().addLast(arguments);
    }

    void popExternalArguments() {
        externalArguments.get().removeLast();
    }

    ArgsMapper getExternalArguments() {
        ArgsMapper arguments = getExternalArgumentsOrNull();
        if (arguments == null) {
            throw new IllegalStateException("No GA program is currently executing.");
        }
        return arguments;
    }

    ArgsMapper getExternalArgumentsOrNull() {
        return externalArguments.get().peekLast();
    }

    void pushDocument(DocumentState documentState) {
        activeDocuments.get().addLast(documentState);
    }

    void popDocument() {
        activeDocuments.get().removeLast();
    }

    DocumentState getActiveDocument() {
        DocumentState documentState = activeDocuments.get().peekLast();
        if (documentState == null) {
            throw new IllegalStateException("No GA document is currently executing.");
        }
        return documentState;
    }
}
