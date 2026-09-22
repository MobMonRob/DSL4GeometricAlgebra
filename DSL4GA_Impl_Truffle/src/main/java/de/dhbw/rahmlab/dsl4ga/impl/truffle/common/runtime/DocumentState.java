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
    private volatile GAFactory factory;
    private volatile CompletedAnalysis completedAnalysis;

    public DocumentState(Source source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    public Source getSource() {
        return source;
    }

    /**
     * Returns the factory selected while compiling this document.
     */
    public GAFactory getFactory() {
        GAFactory result = factory;
        if (result == null) {
            throw new IllegalStateException("Document algebra has not been resolved yet.");
        }
        return result;
    }

	public Map<String, Function> getFunctions() {
        CompletedAnalysis analysis = completedAnalysis;
        return analysis == null ? Map.of() : analysis.functions();
	}

	/**
	 * Returns functions already available before this document added its own
	 * declarations, such as builtins and its algebra library.
	 */
	public Map<String, Function> getExternalFunctions() {
		CompletedAnalysis analysis = completedAnalysis;
		return analysis == null ? Map.of() : analysis.externalFunctions();
	}

    /** Returns the immutable source-level symbol analysis of this document. */
    public DocumentAnalysis getDocumentAnalysis() {
        return getCompletedAnalysis().documentAnalysis();
    }

    /** Resolves the document algebra before its AST nodes are constructed. */
    public synchronized void setFactory(GAFactory factory) {
        if (this.factory != null) {
            throw new IllegalStateException("Document algebra has already been resolved.");
        }
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    /** Completes this state once its source unit has been parsed successfully. */
	public synchronized void complete(Map<String, Function> externalFunctions,
			Map<String, Function> functions,
			DocumentAnalysis documentAnalysis) {
        if (completedAnalysis != null) {
            throw new IllegalStateException("Document state has already been completed.");
        }
        // A single volatile reference publishes the immutable result atomically
        // to later LSP requests, which can run on another thread.
		this.completedAnalysis = new CompletedAnalysis(Map.copyOf(externalFunctions), Map.copyOf(functions),
				Objects.requireNonNull(documentAnalysis, "documentAnalysis"));
    }

    private CompletedAnalysis getCompletedAnalysis() {
        CompletedAnalysis analysis = completedAnalysis;
        if (analysis == null) {
            throw new IllegalStateException("Document analysis has not been completed yet.");
        }
        return analysis;
    }

	private record CompletedAnalysis(Map<String, Function> externalFunctions,
			Map<String, Function> functions,
			DocumentAnalysis documentAnalysis) {
    }
}
