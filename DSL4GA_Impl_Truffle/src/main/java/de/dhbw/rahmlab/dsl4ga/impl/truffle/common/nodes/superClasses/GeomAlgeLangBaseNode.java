package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses;

import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import java.util.Objects;

public abstract class GeomAlgeLangBaseNode extends Node implements InstrumentableNode {

	protected final GeomAlgeLangContext currentLanguageContext() {
		return GeomAlgeLangContext.get(this);
	}

	private static final int NO_SOURCE = -1;

	private int sourceCharIndex = NO_SOURCE;
	private int sourceLength;

	// invoked by the parser to set the source
	// parse() und damit diese Methode wird unter Umständen mehrfach aufgerufen
	public void setSourceSection(int fromIndex, int toIndexInclusive) throws IllegalArgumentException {
		//assert sourceCharIndex == NO_SOURCE : "source should only be set once";
		// testweise
		// ich lande hier zwei mal mit gleichem fromIndex
		// GeomAlgeLangBaseNode sourceCharIndex=-1
		//GeomAlgeLangBaseNode fromIndex=8
		//GeomAlgeLangBaseNode sourceCharIndex=-1
		//GeomAlgeLangBaseNode fromIndex=8
		//(new Exception()).printStackTrace(System.out);
		//System.out.println("GeomAlgeLangBaseNode sourceCharIndex="+String.valueOf(sourceCharIndex));
		//System.out.println("GeomAlgeLangBaseNode fromIndex="+String.valueOf(fromIndex)+
		//	" toIndexInclude="+String.valueOf(toIndexInclusive));
		// commented out because multiple invocation of parse() is possible 
		//assert sourceCharIndex != NO_SOURCE : "source should only be set once";
		if (fromIndex > toIndexInclusive) {
			throw new IllegalArgumentException("from Index "+
				String.valueOf(fromIndex)+" > "+String.valueOf(toIndexInclusive));
		}
		this.sourceCharIndex = fromIndex;
		this.sourceLength = toIndexInclusive - fromIndex + 1;
	}

	@Override
	public final SourceSection getSourceSection() {
		if (sourceCharIndex == NO_SOURCE) {
			// AST node without source
			return null;
		}
		GeomAlgeLangContext context = currentLanguageContext();
		Source source = context.getSource();
		return source.createSection(sourceCharIndex, sourceLength);
	}

	// Needed for Debugger.
	@Override
	public boolean isInstrumentable() {
		boolean is = Objects.nonNull(this.getSourceSection());
		return is;
	}
}
