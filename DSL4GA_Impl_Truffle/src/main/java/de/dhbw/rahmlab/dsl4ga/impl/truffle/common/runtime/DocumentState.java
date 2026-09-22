package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.gacalc.api.GAFactory;
import java.util.Map;
import java.util.Objects;

/**
 * Analysis data belonging to one parsed source document.
 *
 * <p>The state is owned by the AST roots created for the document. It is not
 * stored in a global document cache, so obsolete parse results can be garbage
 * collected together with their AST.</p>
 */
public final class DocumentState {

    private final Source source;
    private volatile CompletedAnalysis completedAnalysis;

    public DocumentState(Source source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    public Source getSource() {
        return source;
    }

    public GAFactory getFactory() {
        CompletedAnalysis analysis = completedAnalysis;
        return analysis == null ? null : analysis.factory();
    }

    public Map<String, Function> getFunctions() {
        CompletedAnalysis analysis = completedAnalysis;
        return analysis == null ? Map.of() : analysis.functions();
    }

    /** Completes this state once its source unit has been parsed successfully. */
    public synchronized void complete(GAFactory factory, Map<String, Function> functions) {
        if (completedAnalysis != null) {
            throw new IllegalStateException("Document state has already been completed.");
        }
        // A single volatile reference publishes the immutable result atomically
        // to later LSP requests, which can run on another thread.
        this.completedAnalysis = new CompletedAnalysis(
                Objects.requireNonNull(factory, "factory"), Map.copyOf(functions));
    }

    private record CompletedAnalysis(GAFactory factory, Map<String, Function> functions) {
    }
}
