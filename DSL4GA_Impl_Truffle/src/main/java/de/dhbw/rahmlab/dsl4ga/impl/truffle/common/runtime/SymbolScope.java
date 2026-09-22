package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.debugging.runtime.ScopeVariablesNames;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Immutable, static interop scope used by the debugger and GraalVM's generic
 * LSP completion handler.
 *
 * <p>Its member list is flattened over its parents. GraalVM's completion
 * handler relies on that convention when it removes names already supplied by
 * an outer scope.</p>
 */
@ExportLibrary(InteropLibrary.class)
public final class SymbolScope implements TruffleObject {

    private static final String CONSTANT_VALUE = "constant";

    private final String displayName;
    private final Map<String, Object> members;
    private final SymbolScope parent;
    private final List<String> visibleMemberNames;

    public SymbolScope(String displayName, Map<String, ?> members, SymbolScope parent) {
        this.displayName = displayName;
        this.members = Collections.unmodifiableMap(new LinkedHashMap<>(members));
        this.parent = parent;

        LinkedHashSet<String> names = new LinkedHashSet<>(this.members.keySet());
        if (parent != null) {
            // The generic LSP handler subtracts the number of parent members
            // and therefore expects the inherited suffix at the end.
            names.removeAll(parent.visibleMemberNames);
            names.addAll(parent.visibleMemberNames);
        }
        this.visibleMemberNames = List.copyOf(names);
    }

    /** Builds the document-specific completion scope for one AST location. */
    public static SymbolScope forDocument(DocumentState documentState, int offset,
            SymbolScope globalScope) {
        Map<String, Object> symbols = new LinkedHashMap<>();
        for (String name : documentState.getFactory().getConstants().keySet()) {
            // Completions only need a readable value. Keeping constants as a
            // marker avoids evaluating or exposing backend-specific objects.
            symbols.put(name, CONSTANT_VALUE);
        }
        symbols.putAll(documentState.getExternalFunctions());
        for (DocumentAnalysis.SymbolDefinition definition
                : documentState.getDocumentAnalysis().getVisibleOwnFunctionDefinitions(offset)) {
            Function function = documentState.getFunctions().get(definition.name());
            if (function != null) {
                symbols.put(definition.name(), function);
            }
        }
        return new SymbolScope("document", symbols, globalScope);
    }

    /** Returns a value for a member already advertised by this scope chain. */
    public Object getMemberValue(String name) {
        Object member = members.get(name);
        return member != null ? member : parent == null ? null : parent.getMemberValue(name);
    }

    public List<String> getVisibleMemberNames() {
        return visibleMemberNames;
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends com.oracle.truffle.api.TruffleLanguage<?>> getLanguage() {
        return GeomAlgeLang.class;
    }

    @ExportMessage
    boolean isScope() {
        return true;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    ScopeVariablesNames getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new ScopeVariablesNames(visibleMemberNames);
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return visibleMemberNames.contains(member);
    }

    @ExportMessage
    Object readMember(String member) {
        return getMemberValue(member);
    }

    @ExportMessage
    boolean hasScopeParent() {
        return parent != null;
    }

    @ExportMessage
    Object getScopeParent() {
        return parent;
    }

    @ExportMessage
    String toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return displayName;
    }
}
