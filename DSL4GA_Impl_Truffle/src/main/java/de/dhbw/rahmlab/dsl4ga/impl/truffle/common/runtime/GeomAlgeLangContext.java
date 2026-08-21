package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.ArgsMapper;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.runtime.BuiltinRegistry;
import de.orat.math.gacalc.api.GAFactory;

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
	private Source source = null;
	private ArgsMapper currentExternalArgs = null;
	private int mainArity = -1;

	public int getMainArity() {
		return this.mainArity;
	}

	public void setMainArity(int mainArity) {
		this.mainArity = mainArity;
	}

	public ArgsMapper getCurrentExternalArgs() {
		return this.currentExternalArgs;
	}

	public void setCurrentExternalArgs(ArgsMapper currentExternalArgs) {
		this.currentExternalArgs = currentExternalArgs;
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

	public void setSource(Source source) {
		this.source = source;
	}

	public Source getSource() {
		return this.source;
	}
}
