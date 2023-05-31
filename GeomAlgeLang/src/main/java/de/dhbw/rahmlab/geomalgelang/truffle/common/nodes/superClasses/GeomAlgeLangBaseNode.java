package de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;

public abstract class GeomAlgeLangBaseNode extends Node {

	protected final GeomAlgeLangContext currentLanguageContext() {
		return GeomAlgeLangContext.get(this);
	}

	private static final int NO_SOURCE = -1;

	private int sourceCharIndex = NO_SOURCE;
	private int sourceLength;

	// invoked by the parser to set the source
	public void setSourceSection(int charIndex, int length) {
		assert sourceCharIndex == NO_SOURCE : "source should only be set once";
		this.sourceCharIndex = charIndex;
		this.sourceLength = length;
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
}
