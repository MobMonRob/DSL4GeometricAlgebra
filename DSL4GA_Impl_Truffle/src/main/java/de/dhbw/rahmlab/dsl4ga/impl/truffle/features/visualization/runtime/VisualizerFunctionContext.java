package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime;

import de.orat.math.cga.api.CGAViewObject;
import java.util.ArrayList;
import java.util.List;

public class VisualizerFunctionContext {

	private final List<CGAViewObject> viewObjects = new ArrayList<>();

	protected void addViewObject(CGAViewObject viewObject) {
		this.viewObjects.add(viewObject);
	}

	public void removeAll() {
		for (CGAViewObject viewObject : this.viewObjects) {
			viewObject.remove();
		}
		this.viewObjects.clear();
	}
}
