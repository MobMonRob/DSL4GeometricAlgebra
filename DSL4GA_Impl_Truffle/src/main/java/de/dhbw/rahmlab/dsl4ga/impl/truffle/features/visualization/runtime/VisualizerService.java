package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime;

import de.dhbw.rahmlab.dsl4ga.euclidview3d.utils.GAViewObject;
import de.dhbw.rahmlab.dsl4ga.euclidview3d.utils.GAViewer;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.util.GeometricObject;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisualizerService {
	private static final Logger LOG = Logger.getLogger(VisualizerService.class.getName());

	private final GAViewer viewer;

	private VisualizerService(GAViewer viewer) {
		this.viewer = viewer;
	}

	private static VisualizerService INSTANCE;

	public static VisualizerService instance() {
		if (INSTANCE == null) {
			Optional<GAViewer> viewerOptional = GAViewer.getInstance();
			if (viewerOptional.isEmpty()) {
				throw new ValidationException("Could get no CGAViewer instance.");
			}
			INSTANCE = new VisualizerService(viewerOptional.get());
		}
		return INSTANCE;
	}

	public static void closeIfOpen() {
		VisualizerService current = INSTANCE;
		if (current == null) {
			return;
		}
		// A later program run must obtain a fresh viewer, even if closing fails.
		INSTANCE = null;
		try {
			if (!current.viewer.close()) {
				LOG.warning("The visualization window could not be closed.");
			}
		} catch (RuntimeException ex) {
			LOG.log(Level.WARNING, "The visualization window could not be closed.", ex);
		}
	}

	public void add(MultivectorExpression mv, String name, VisualizerFunctionContext vizContext, boolean isExtrinsic) {
		MultivectorValue mvValue = GeomAlgeLangContext.get().getCurrentExternalArgs().evalToMV(List.of(mv)).get(0);
		GeometricObject geometricObject = mvValue.decompose(isExtrinsic);
		if (geometricObject != null) {
			try {
				GAViewObject gaViewObject = this.viewer.addGeometricObject(geometricObject, name/*, isIPNS*/);
				if (gaViewObject != null) {
					vizContext.addViewObject(gaViewObject);
				} else {
					throw new ValidationException("Visualization of \"" + name + "\" failed!");
				}
			} catch (RuntimeException ex) {
				throw new ValidationException(ex.getMessage(), ex);
			}
		} else {
			// FIXME mvValue.toString liefert keinen Namen
			// vielleicht hier auch keine Exception werfen, wenn nur der Multivektor nicht visualisierbar ist
			// nur eine Warnung ausgeben
			//TODO
			//throw new ValidationException(
			System.out.println(String.format("Variable \"%s\" is no k-vector:", name)+mv.toString());
		}
	}
}
