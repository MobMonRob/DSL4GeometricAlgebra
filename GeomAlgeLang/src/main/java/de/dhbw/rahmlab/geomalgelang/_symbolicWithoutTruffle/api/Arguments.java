package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api;

import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.spi.iEuclideanTypeConverter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jogamp.vecmath.Tuple3d;
import util.cga.SparseCGAColumnVector;

public class Arguments {

	private final ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.getExprGraphFactoryThrowing();
	private final iEuclideanTypeConverter euclideanTypeConverter = exprGraphFactory.getEuclideanTypeConverter();
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
		var mvec = this.exprGraphFactory.createMultivectorNumeric(sparseVec);
		this.put(argName, mvec);
		return this;
	}

	public Arguments random(String argName) {
		var mvec = this.exprGraphFactory.createRandomMultivectorNumeric();
		this.put(argName, mvec);
		return this;
	}

	public Arguments euclidean_vector(String argName, Tuple3d tuple3d) {
		var sparseVec = euclideanTypeConverter.euclidean_vector(tuple3d);
		return createPutReturn(argName, sparseVec);
	}

	public Arguments scalar_ipns(String argName, double scalar) {
		var sparseVec = euclideanTypeConverter.scalar_ipns(scalar);
		return createPutReturn(argName, sparseVec);
	}
}
