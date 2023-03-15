package de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses;

import com.oracle.truffle.api.nodes.Node;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;

public abstract class GeomAlgeLangBaseNode extends Node {

	protected final GeomAlgeLangContext currentLanguageContext() {
		return GeomAlgeLangContext.get(this);
	}
}
