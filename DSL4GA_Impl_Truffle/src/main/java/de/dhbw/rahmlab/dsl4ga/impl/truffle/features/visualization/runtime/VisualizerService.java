package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime;

import de.dhbw.rahmlab.dsl4ga.euclidview3d.utils.GAViewObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.dsl4ga.euclidview3d.utils.GAViewer;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.util.GeometricObject;
import java.util.List;
import java.util.Optional;

public class VisualizerService {

	private final GAViewer viewer;

	private VisualizerService(GAViewer viewer) {
		this.viewer = viewer;
	}

	private static VisualizerService INSTANCE;

	public static VisualizerService instance() throws InterpreterInternalException {
		if (INSTANCE == null) {
			Optional<GAViewer> viewerOptional = GAViewer.getInstance();
			if (viewerOptional.isEmpty()) {
				throw new InterpreterInternalException("Could get no CGAViewer instance.");
			}
			INSTANCE = new VisualizerService(viewerOptional.get());
		}
		return INSTANCE;
	}

	public void add(MultivectorExpression mv, String name, VisualizerFunctionContext vizContext, 
		                 boolean isIPNS) throws InterpreterInternalException {
		
		MultivectorValue mvValue = GeomAlgeLangContext.currentExternalArgs.evalToMV(List.of(mv)).get(0);
		GeometricObject geometricObject = mvValue.decompose(isIPNS);
        if (geometricObject != null){
			try {
				GAViewObject gaViewObject = this.viewer.addGeometricObject(geometricObject, name/*, isIPNS*/);
				if (gaViewObject != null) {
					vizContext.addViewObject(gaViewObject);
				} else {
					throw new InterpreterInternalException("Visualization of \"" + name + "\" failed!");
				}
			} catch (RuntimeException ex) {
				throw new InterpreterInternalException(ex.getMessage(), ex);
			}
		} else {
			throw new InterpreterInternalException(
				String.format("Variable \"%s\" is no k-vector!", mvValue.toString(/*name*/)));
		}
	}
}
