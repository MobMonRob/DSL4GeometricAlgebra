package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime;

import de.dhbw.rahmlab.dsl4ga.euclidview3d.utils.GAViewObject;
import java.util.ArrayList;
import java.util.List;

public class VisualizerFunctionContext {

	private final List<GAViewObject> viewObjects = new ArrayList<>();

	protected void addViewObject(GAViewObject viewObject) {
		this.viewObjects.add(viewObject);
	}

	public void removeAll() {
		for (GAViewObject viewObject : this.viewObjects) {
			viewObject.remove();
		}
		this.viewObjects.clear();
	}
}
