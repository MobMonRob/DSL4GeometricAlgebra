package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.orat.math.cga.api.CGAKVector;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAViewObject;
import de.orat.math.cga.api.CGAViewer;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.Optional;

public class VisualizerService {

	private final CGAViewer viewer;

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

	public void add(MultivectorNumeric mv, String name, VisualizerFunctionContext vizContext, boolean isIPNS) throws InterpreterInternalException {
		var sparseDoubleMatrix = mv.elements();
		var sparseDoubleColumnVector = new SparseDoubleColumnVector(sparseDoubleMatrix);
		var doubleArray = sparseDoubleColumnVector.toArray();
		//FIXME vermutlich erwartet der Konstruktor ein doubleArray Argument in einer anderen Representation
		var cgaMultivector = new CGAMultivector(doubleArray);

		CGAKVector mv2 = CGAKVector.specialize(cgaMultivector, isIPNS);
		if (mv2 instanceof CGAKVector cgakVector) {
			CGAViewObject cgaViewObject = this.viewer.addCGAObject(cgakVector, name);
			if (cgaViewObject != null){
				vizContext.addViewObject(cgaViewObject);
			} else {
				throw new InterpreterInternalException("Visualization of \""+name+"\" failed!");
			}
		} else {
			throw new InterpreterInternalException(
				String.format("Variable \"%s\" is no k-vector!", cgaMultivector.toString(name)));
		}
	}
}
