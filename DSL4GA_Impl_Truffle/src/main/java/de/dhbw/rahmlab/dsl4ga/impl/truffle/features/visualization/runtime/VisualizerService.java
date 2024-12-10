package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.orat.math.cga.api.CGAKVector;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAViewObject;
import de.orat.math.cga.api.CGAViewer;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisualizerService {

	private final CGAViewer viewer;
	private final List<CGAViewObject> viewObjects = new ArrayList<>();

	private VisualizerService(CGAViewer viewer) {
		this.viewer = viewer;
	}

	private static VisualizerService INSTANCE;

	public static VisualizerService instance() throws InterpreterInternalException {
		if (INSTANCE == null) {
			Optional<CGAViewer> viewerOptional = CGAViewer.getInstance();
			if (viewerOptional.isEmpty()) {
				throw new InterpreterInternalException("Could get no CGAViewer instance.");
			}
			INSTANCE = new VisualizerService(viewerOptional.get());
		}
		return INSTANCE;
	}

	public void add(MultivectorNumeric mv, String name) throws InterpreterInternalException {
		var sparseDoubleMatrix = mv.elements();
		var sparseDoubleColumnVector = new SparseDoubleColumnVector(sparseDoubleMatrix);
		var doubleArray = sparseDoubleColumnVector.toArray();
		var cgaMultivector = new CGAMultivector(doubleArray);

		CGAKVector mv2 = CGAKVector.specialize(cgaMultivector, true);
		if (mv2 instanceof CGAKVector cgakVector) {
			CGAViewObject cgaViewObject = this.viewer.addCGAObject(cgakVector, name);
			this.viewObjects.add(cgaViewObject);
		} else {
			throw new InterpreterInternalException(
				String.format("Variable \"%s\" is no k-vector!", cgaMultivector.toString(name)));
		}
	}

	public void removeAll() {
		for (CGAViewObject viewObject : this.viewObjects) {
			viewObject.remove();
		}
		this.viewObjects.clear();
	}
}
