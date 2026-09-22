package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.ArgsMapper;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.debug.Debugger;
import com.oracle.truffle.api.nodes.Node;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.runtime.BuiltinRegistry;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.MultivectorVariable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GeomAlgeLangContext {

	/** Metadata bridge used only by {@code Context.parse} callers. */
	public record ParsedProgramMetadata(GAFactory factory, int mainArity) {
	}

	protected static final ContextReference<GeomAlgeLangContext> contextRef = ContextReference.create(GeomAlgeLang.class);

	public static GeomAlgeLangContext get(Node node) {
		return GeomAlgeLangContext.contextRef.get(node);
	}

	public static GeomAlgeLangContext get() {
		return GeomAlgeLangContext.contextRef.get(null);
	}

	public final BuiltinRegistry builtinRegistry;
	private final SymbolScope globalScope;
	public final GeomAlgeLang truffleLanguage;
	public final TruffleLanguage.Env env;
	private final ThreadLocal<Deque<DocumentState>> parsingDocumentStates
		= ThreadLocal.withInitial(ArrayDeque::new);
	private final ExecutionState executionState = new ExecutionState();
	private final ThreadLocal<ParsedProgramMetadata> pendingParsedProgramMetadata = new ThreadLocal<>();
	private final ThreadLocal<Deque<List<MultivectorVariable>>> functionSpecializationVariableScopes
		= ThreadLocal.withInitial(ArrayDeque::new);

	/**
	 * Caution! params can be not enough if FunctionCache is activated. use
	 * getVisibleSimplificationVariables() instead. ToDo: Make safer if FunctionCache stays.
	 */
	public ArgsMapper getCurrentExternalArgs() {
		return this.executionState.getExternalArguments();
	}

	/** Opens the external-argument scope for one top-level program invocation. */
	public void pushExternalArguments(ArgsMapper currentExternalArgs) {
		this.executionState.pushExternalArguments(currentExternalArgs);
	}

	/** Closes the external-argument scope opened for one top-level invocation. */
	public void popExternalArguments() {
		this.executionState.popExternalArguments();
	}

	/**
	 * Makes the formals of one currently constructed function specialization
	 * visible to Maxima simplification on this evaluation thread.
	 */
	public void pushFunctionSpecializationVariables(List<MultivectorVariable> variables) {
		Map<String, MultivectorVariable> variablesByName = new LinkedHashMap<>();
		for (MultivectorVariable variable : variables) {
			variablesByName.putIfAbsent(variable.getName(), variable);
		}
		functionSpecializationVariableScopes.get().addLast(List.copyOf(variablesByName.values()));
	}

	/** Returns the depth of currently open function-specialization creators. */
	public int getFunctionSpecializationScopeDepth() {
		return functionSpecializationVariableScopes.get().size();
	}

	/** Removes the innermost currently constructed function specialization. */
	public void popFunctionSpecializationVariables() {
		functionSpecializationVariableScopes.get().removeLast();
	}

	/**
	 * Returns main parameters followed by the formals of open specialization
	 * creators, ordered from the outermost creator to the innermost one.
	 */
	public List<MultivectorVariable> getVisibleSimplificationVariables() {
		List<MultivectorVariable> visible = new ArrayList<>();
		ArgsMapper currentExternalArgs = executionState.getExternalArgumentsOrNull();
		if (currentExternalArgs != null) visible.addAll(currentExternalArgs.params);
		functionSpecializationVariableScopes.get().forEach(visible::addAll);
		return List.copyOf(visible);
	}

	public GeomAlgeLangContext() {
		this(null, null);
	}

	public GeomAlgeLangContext(GeomAlgeLang truffleLanguage, Env env) {
		this.builtinRegistry = new BuiltinRegistry(truffleLanguage);
		this.globalScope = new SymbolScope("builtins", builtinRegistry.getBuiltinsView(), null);
		this.truffleLanguage = truffleLanguage;
		this.env = env;
	}

	/** Global symbols are context-wide; document symbols must use a child scope. */
	public SymbolScope getGlobalScope() {
		return globalScope;
	}

	/** Activates the document whose AST root is currently executing. */
	public void pushExecutingDocument(DocumentState documentState) {
		this.executionState.pushDocument(documentState);
	}

	/** Restores the document active before the current AST root was entered. */
	public void popExecutingDocument() {
		this.executionState.popDocument();
	}

	public DocumentState getCurrentExecutingDocumentState() {
		return this.executionState.getActiveDocument();
	}

	public GAFactory getCurrentFactory() {
		return getCurrentExecutingDocumentState().getFactory();
	}

	/**
	 * Publishes metadata for the immediately returning {@code Context.parse}
	 * call. It is not consulted while compiling or executing ASTs.
	 */
	public void publishParsedProgramMetadata(DocumentState documentState, int mainArity) {
		this.pendingParsedProgramMetadata.set(new ParsedProgramMetadata(documentState.getFactory(), mainArity));
	}

	/** Consumes the metadata belonging to the preceding successful parse. */
	public ParsedProgramMetadata consumeParsedProgramMetadata() {
		ParsedProgramMetadata metadata = this.pendingParsedProgramMetadata.get();
		this.pendingParsedProgramMetadata.remove();
		if (metadata == null) {
			throw new IllegalStateException("No parsed GA program metadata is available.");
		}
		return metadata;
	}


	/** Makes a document state available while its AST is being constructed. */
	public void pushParsingDocumentState(DocumentState documentState) {
		this.parsingDocumentStates.get().addLast(documentState);
	}

	/** Removes the innermost document state after parsing, including failed parses. */
	public void popParsingDocumentState() {
		this.parsingDocumentStates.get().removeLast();
	}

	/**
	 * Returns the document currently being parsed on this thread.
	 *
	 * <p>A stack is required because an algebra library can be parsed while its
	 * importing document is still being constructed.</p>
	 */
	public DocumentState getCurrentParsingDocumentState() {
		DocumentState documentState = this.parsingDocumentStates.get().peekLast();
		if (documentState == null) {
			throw new IllegalStateException("No document is currently being parsed.");
		}
		return documentState;
	}

	@TruffleBoundary
	public boolean isDebuggerActive() {
		return this.env != null && Debugger.find(this.env).getSessionCount() > 0;
	}
}
