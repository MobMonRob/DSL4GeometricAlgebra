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

	protected static final ContextReference<GeomAlgeLangContext> contextRef = ContextReference.create(GeomAlgeLang.class);

	public static GeomAlgeLangContext get(Node node) {
		return GeomAlgeLangContext.contextRef.get(node);
	}

	public static GeomAlgeLangContext get() {
		return GeomAlgeLangContext.contextRef.get(null);
	}

	public final BuiltinRegistry builtinRegistry;
	public final GeomAlgeLang truffleLanguage;
	public final TruffleLanguage.Env env;
	private GAFactory gaFactory = null;
	private ArgsMapper currentExternalArgs = null;
	private final ThreadLocal<Deque<DocumentState>> parsingDocumentStates
		= ThreadLocal.withInitial(ArrayDeque::new);
	private final ThreadLocal<Deque<List<MultivectorVariable>>> functionSpecializationVariableScopes
		= ThreadLocal.withInitial(ArrayDeque::new);
	private int mainArity = -1;

	public int getMainArity() {
		return this.mainArity;
	}

	public void setMainArity(int mainArity) {
		this.mainArity = mainArity;
	}

	/**
	 * Caution! params can be not enough if FunctionCache is activated. use
	 * getVisibleSimplificationVariables() instead. ToDo: Make safer if FunctionCache stays.
	 */
	public ArgsMapper getCurrentExternalArgs() {
		return this.currentExternalArgs;
	}

	public void setCurrentExternalArgs(ArgsMapper currentExternalArgs) {
		this.currentExternalArgs = currentExternalArgs;
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
		if (currentExternalArgs != null) visible.addAll(currentExternalArgs.params);
		functionSpecializationVariableScopes.get().forEach(visible::addAll);
		return List.copyOf(visible);
	}

	public GeomAlgeLangContext() {
		this(null, null);
	}

	public GeomAlgeLangContext(GeomAlgeLang truffleLanguage, Env env) {
		this.builtinRegistry = new BuiltinRegistry(truffleLanguage);
		this.truffleLanguage = truffleLanguage;
		this.env = env;
	}

	public static final String FAC_SYMBOL = "__fac";

	public void setFac(GAFactory fac) {
		this.gaFactory = fac;
		// Alternative to GeomAlgeLangContext.get().getFac() (which needs context.enter()) from outside Truffle.
		this.env.exportSymbol(FAC_SYMBOL, this.env.asGuestValue(fac));
	}

	/**
	 * Can be null, if not set in parsing. Should never happen after parsing.
	 */
	public GAFactory getFac() {
		return this.gaFactory;
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
