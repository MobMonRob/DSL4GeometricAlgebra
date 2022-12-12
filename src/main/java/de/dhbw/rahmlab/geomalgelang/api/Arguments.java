package de.dhbw.rahmlab.geomalgelang.api;

import de.orat.math.cga.api.CGALineOPNS;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAOrientedCircleOPNS;
import de.orat.math.cga.api.CGAOrientedPointPairOPNS;
import de.orat.math.cga.api.CGARoundPointIPNS;
import de.orat.math.cga.api.CGAScalar;
import de.orat.math.cga.api.CGASphereIPNS;
import de.orat.math.cga.api.CGATranslator;
import java.util.HashMap;
import java.util.Map;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Tuple3d;
import org.jogamp.vecmath.Vector3d;

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

	public Arguments point_ipns(String argName, Tuple3d point) {
		var mvec = new CGARoundPointIPNS(point);
		this.put(argName, mvec);
		return this;
	}

	public Arguments pointpair_opns(String argName, Tuple3d point1, double weight1, Tuple3d point2, double weight2) {
		var mvec = new CGAOrientedPointPairOPNS(new Point3d(point1), weight1, new Point3d(point2), weight2);
		this.put(argName, mvec);
		return this;
	}

	public Arguments pointpair_opns(String argName, Tuple3d point1, Tuple3d point2) {
		var mvec = new CGAOrientedPointPairOPNS(new CGARoundPointIPNS(point1), new CGARoundPointIPNS(point2));
		this.put(argName, mvec);
		return this;
	}

	public Arguments line_opns(String argName, Tuple3d point1, double weight1, Tuple3d point2, double weight2) {
		var mvec = new CGALineOPNS(new Point3d(point1), weight1, new Point3d(point2), weight2);
		this.put(argName, mvec);
		return this;
	}

	public Arguments line_opns(String argName, Tuple3d point1, Tuple3d point2) {
		var mvec = new CGALineOPNS(new Point3d(point1), new Point3d(point2));
		this.put(argName, mvec);
		return this;
	}

	public Arguments sphere_ipns(String argName, Tuple3d center, double radius, double weight) {
		var mvec = new CGASphereIPNS(new Point3d(center), radius, weight);
		this.put(argName, mvec);
		return this;
	}

	public Arguments sphere_ipns(String argName, Tuple3d center, double radius) {
		var mvec = new CGASphereIPNS(new Point3d(center), radius);
		this.put(argName, mvec);
		return this;
	}

	public Arguments plane_ipns(String argName, Tuple3d normal, double dist, double weight) {
		throw new UnsupportedOperationException();
	}

	public Arguments plane_ipns(String argName, Tuple3d normal, double dist) {
		throw new UnsupportedOperationException();
	}

	public Arguments circle_opns(String argName, Tuple3d point1, double weight1, Tuple3d point2, double weight2, Tuple3d point3, double weight3) {
		var mvec = new CGAOrientedCircleOPNS(new Point3d(point1), weight1, new Point3d(point2), weight2, new Point3d(point3), weight3);
		this.put(argName, mvec);
		return this;
	}

	public Arguments circle_opns(String argName, Tuple3d point1, Tuple3d point2, Tuple3d point3) {
		var mvec = new CGAOrientedCircleOPNS(new Point3d(point1), new Point3d(point2), new Point3d(point3));
		this.put(argName, mvec);
		return this;
	}

	public Arguments tangent_opns(String argName, Tuple3d point, double weight, Tuple3d direction, double weight2) {
		throw new UnsupportedOperationException();
	}

	public Arguments translator(String argName, Tuple3d point) {
		var mvec = new CGATranslator(new Vector3d(point));
		this.put(argName, mvec);
		return this;
	}

	public Arguments rotator(String argName, Tuple3d point, double theta) {
		throw new UnsupportedOperationException();
	}
}
