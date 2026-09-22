package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Mutable collector used only while one source document is compiled.
 *
 * <p>Keeping construction outside {@link DocumentAnalysis} makes the latter a
 * plainly immutable value exposed to later editor requests.</p>
 */
public final class DocumentAnalysisBuilder {

    /** Mutable view of the declarations being built for one function. */
    public final class FunctionScopeBuilder {

        private final DocumentAnalysis.SourceRange range;
        private final List<DocumentAnalysis.SymbolDefinition> localDefinitions = new ArrayList<>();
        private final Map<Integer, DocumentAnalysis.SymbolDefinition> definitionsByFrameSlot = new HashMap<>();

        private FunctionScopeBuilder(DocumentAnalysis.SourceRange range) {
            this.range = range;
        }

        public DocumentAnalysis.SymbolDefinition declareLocal(String name,
                DocumentAnalysis.SourceRange nameRange, int frameSlot,
                int visibleFromOffset) {
            DocumentAnalysis.SymbolDefinition definition = new DocumentAnalysis.SymbolDefinition(name,
                    DocumentAnalysis.SymbolKind.LOCAL_VARIABLE, source, nameRange, frameSlot,
                    visibleFromOffset);
            localDefinitions.add(definition);
            definitionsByFrameSlot.put(frameSlot, definition);
            return definition;
        }

        public Optional<DocumentAnalysis.SymbolDefinition> findLocal(int frameSlot) {
            return Optional.ofNullable(definitionsByFrameSlot.get(frameSlot));
        }

        private DocumentAnalysis.FunctionScope build() {
            return new DocumentAnalysis.FunctionScope(range, localDefinitions);
        }
    }

    private final Source source;
    private final List<DocumentAnalysis.SymbolDefinition> functionDefinitions = new ArrayList<>();
    private final List<DocumentAnalysis.SymbolReference> references = new ArrayList<>();
    private final List<DocumentAnalysis.FunctionScope> functionScopes = new ArrayList<>();
    private final Map<Function, DocumentAnalysis.SymbolDefinition> definitionsByFunction = new IdentityHashMap<>();

    public DocumentAnalysisBuilder(Source source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    public FunctionScopeBuilder beginFunction(DocumentAnalysis.SourceRange range) {
        return new FunctionScopeBuilder(range);
    }

    /**
     * Makes a function available after its whole declaration, matching the
     * compiler: the function enters the name map only after its body was built.
     */
    public void completeFunction(Function function, String name,
            DocumentAnalysis.SourceRange nameRange, int visibleFromOffset,
            FunctionScopeBuilder scopeBuilder) {
        DocumentAnalysis.SymbolDefinition definition = new DocumentAnalysis.SymbolDefinition(name,
                DocumentAnalysis.SymbolKind.FUNCTION, source, nameRange, -1, visibleFromOffset);
        functionDefinitions.add(definition);
        definitionsByFunction.put(function, definition);
        functionScopes.add(scopeBuilder.build());
    }

    public Optional<DocumentAnalysis.SymbolDefinition> findFunctionDefinition(Function function) {
        DocumentAnalysis.SymbolDefinition localDefinition = definitionsByFunction.get(function);
        if (localDefinition != null) {
            return Optional.of(localDefinition);
        }
        if (function.getRootNode() instanceof FunctionDefinitionRootNode rootNode) {
            return rootNode.getDocumentState().getDocumentAnalysis()
                    .findFunctionDefinition(function);
        }
        return Optional.empty();
    }

    public void recordReference(String name, DocumentAnalysis.SourceRange range,
            DocumentAnalysis.SymbolDefinition target) {
        references.add(new DocumentAnalysis.SymbolReference(name, range, target));
    }

    public DocumentAnalysis build() {
        return new DocumentAnalysis(functionDefinitions, references, functionScopes,
                definitionsByFunction);
    }
}
