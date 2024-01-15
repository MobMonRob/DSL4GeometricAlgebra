package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api;

import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import de.orat.math.gacalc.api.MultivectorNumeric;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jogamp.vecmath.Tuple3d;
import util.cga.SparseCGAColumnVector;
import util.cga.SparseCGAColumnVectorFactory;

public class Arguments {

	private final ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.instance().getExprGraphFactory().orElseThrow();
	private final Map<String, MultivectorNumeric> argsMap = new LinkedHashMap<>();
	private final Map<String, MultivectorNumeric> argsMapView = Collections.unmodifiableMap(this.argsMap);

	protected Map<String, MultivectorNumeric> getArgsMapView() {
		return argsMapView;
	}

	protected void put(String argName, MultivectorNumeric multivector) throws IllegalArgumentException {
		if (this.argsMap.containsKey(argName)) {
			throw new IllegalArgumentException("argName \"" + argName + "\" is already present.");
		}

		this.argsMap.put(argName, multivector);
	}

	protected Arguments createPutReturn(String argName, SparseCGAColumnVector sparseVec) throws IllegalArgumentException {
		var mvec = this.exprGraphFactory.createMultivectorNumeric(sparseVec.nonzeros(), sparseVec);
		this.put(argName, mvec);
		return this;
	}

	public Arguments euclidean_vector(String argName, Tuple3d tuple3d) {
		var sparseVec = SparseCGAColumnVectorFactory.euclidean_vector(tuple3d);
		return createPutReturn(argName, sparseVec);
	}

	public Arguments scalar_ipns(String argName, double scalar) {
		var sparseVec = SparseCGAColumnVectorFactory.scalar_ipns(scalar);
		return createPutReturn(argName, sparseVec);
	}
}
