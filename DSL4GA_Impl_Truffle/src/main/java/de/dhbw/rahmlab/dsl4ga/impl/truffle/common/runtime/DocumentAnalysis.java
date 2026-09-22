package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable symbol information produced together with one successfully parsed
 * GA document.
 *
 * <p>This deliberately records only source-level symbols needed by editor
 * features. It neither mirrors the Truffle AST nor evaluates expressions.</p>
 */
public final class DocumentAnalysis {

    public enum SymbolKind {
        LOCAL_VARIABLE,
        FUNCTION
    }

    /** A half-open character range in its owning {@link Source}. */
    public record SourceRange(int startOffset, int endOffset) {
        public SourceRange {
            if (startOffset < 0 || endOffset < startOffset) {
                throw new IllegalArgumentException("Invalid source range.");
            }
        }

        public boolean contains(int offset) {
            return startOffset <= offset && offset < endOffset;
        }
    }

    /** A source declaration that can serve as a navigation target. */
    public record SymbolDefinition(String name, SymbolKind kind, Source source,
            SourceRange nameRange, int frameSlot, int visibleFromOffset) {

        public SymbolDefinition {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(kind, "kind");
            Objects.requireNonNull(source, "source");
            Objects.requireNonNull(nameRange, "nameRange");
        }
    }

    /** A source occurrence already resolved by the compiler. */
    public record SymbolReference(String name, SourceRange range,
            SymbolDefinition target) {

        public SymbolReference {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(range, "range");
            Objects.requireNonNull(target, "target");
        }
    }

    /** The locals visible within one function source range. */
    public record FunctionScope(SourceRange range,
            List<SymbolDefinition> localDefinitions) {

        public FunctionScope {
            Objects.requireNonNull(range, "range");
            localDefinitions = List.copyOf(localDefinitions);
        }

        public List<SymbolDefinition> getVisibleLocals(int offset) {
            if (!range.contains(offset)) {
                return List.of();
            }
            return localDefinitions.stream()
                    .filter(definition -> definition.visibleFromOffset() <= offset)
                    .toList();
        }
    }

    private final List<SymbolDefinition> functionDefinitions;
    private final List<SymbolReference> references;
    private final List<FunctionScope> functionScopes;
    private final Map<Function, SymbolDefinition> definitionsByFunction;

    DocumentAnalysis(List<SymbolDefinition> functionDefinitions,
            List<SymbolReference> references, List<FunctionScope> functionScopes,
            Map<Function, SymbolDefinition> definitionsByFunction) {
        this.functionDefinitions = List.copyOf(functionDefinitions);
        this.references = List.copyOf(references);
        this.functionScopes = List.copyOf(functionScopes);
        this.definitionsByFunction = Map.copyOf(definitionsByFunction);
    }

    public List<SymbolDefinition> getFunctionDefinitions() {
        return functionDefinitions;
    }

    /**
     * Returns this document's functions that the compiler had already made
     * visible at {@code offset}.
     *
     * <p>Functions imported from another document are deliberately not listed
     * here: their declaration and visibility belong to that other document.</p>
     */
    public List<SymbolDefinition> getVisibleOwnFunctionDefinitions(int offset) {
        return functionDefinitions.stream()
                .filter(definition -> definition.visibleFromOffset() <= offset)
                .toList();
    }

    public List<SymbolReference> getReferences() {
        return references;
    }

    public List<FunctionScope> getFunctionScopes() {
        return functionScopes;
    }

    public Optional<SymbolDefinition> findFunctionDefinition(Function function) {
        return Optional.ofNullable(definitionsByFunction.get(function));
    }

    public Optional<SymbolReference> findReferenceAt(int offset) {
        return references.stream().filter(reference -> reference.range().contains(offset)).findFirst();
    }
}
