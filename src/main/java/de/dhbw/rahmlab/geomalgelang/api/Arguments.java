package de.dhbw.rahmlab.geomalgelang.api;

import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGARoundPointIPNS;
import de.orat.math.cga.api.CGAScalar;
import java.util.HashMap;
import java.util.Map;
import org.jogamp.vecmath.Tuple3d;

public class Arguments {

	protected final Map<String, CGAMultivector> argsMap = new HashMap<>();

	protected void put(String argName, CGAMultivector multivector) throws IllegalArgumentException {
		if (this.argsMap.containsKey(argName)) {
			throw new IllegalArgumentException("argName \"" + argName + "\" is already present.");
		}

		this.argsMap.put(argName, multivector);
	}

	public Arguments scalar(String argName, double scalar) {
		var mvec = new CGAScalar(scalar);
		this.put(argName, mvec);
		return this;
	}

	public Arguments point_ipns(String argName, Tuple3d point, double weight) {
		var mvec = new CGARoundPointIPNS(point, weight);
		this.put(argName, mvec);
		return this;
	}
}
