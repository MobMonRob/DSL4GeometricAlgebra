package de.dhbw.rahmlab.geomalgelang.api;

import de.orat.math.cga.api.CGAAttitudeIPNS;
import de.orat.math.cga.api.CGAKVector;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAOrientedPointIPNS;
import de.orat.math.cga.api.CGAPlaneIPNS;
import de.orat.math.cga.api.CGAPlaneOPNS;
import de.orat.math.cga.api.CGAPointPairOPNS;
import de.orat.math.cga.api.CGARotor;
import de.orat.math.cga.api.CGARoundPointIPNS;
import de.orat.math.cga.api.iCGAPointPair.PointPair;
import de.orat.math.cga.api.iCGATangentOrRound;
import de.orat.math.cga.api.iCGATangentOrRound.EuclideanParameters;
import java.util.List;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Quat4d;
import org.jogamp.vecmath.Vector3d;

public class Result {

	public record EuclideanParametersOfOrientedPointIPNS(Point3d location, Vector3d attitude) {

	}

	protected final List<CGAMultivector> inner;

	protected Result(List<CGAMultivector> inner) {
		this.inner = inner;
	}

	public Double firstDecomposeSquaredWeight() {
		return ((CGAKVector) inner.get(0)).squaredWeight();
	}

	public double firstDecomposeScalar() {
		return inner.get(0).decomposeScalar();
	}

	public Double[] decomposeScalarArray() {
		return inner.stream().map(mv -> (mv.decomposeScalar())).toArray(Double[]::new);
	}

	public double[][] decomposeDoubleArray() {
		return inner.stream().map(mv -> mv.extractCoordinates()).toArray(double[][]::new);
	}

	public double[] firstDecomposeDoubleArray() {
		return inner.get(0).extractCoordinates();
	}

	public iCGATangentOrRound.EuclideanParameters firstDecomposeTangentOrRound() {
		return (new CGAKVector(inner.get(0))).decomposeTangentOrRound();
	}

	public EuclideanParametersOfOrientedPointIPNS firstDecomposeOrientedPointIPNS() {
		//System.out.println(inner.get(0).toString("decomposeTangentOrRound"));
		EuclideanParameters result = (new CGAOrientedPointIPNS(inner.get(0))).decomposeTangentOrRound();
		return new EuclideanParametersOfOrientedPointIPNS(result.location(), result.attitude());
	}

	public EuclideanParametersFromPlaneIPNS firstDecomposePlaneIPNS() {
		return new EuclideanParametersFromPlaneIPNS((new CGAPlaneIPNS(inner.get(0))).decomposeFlat());
	}

	public EuclideanParametersFromPlaneOPNS firstDecomposePlaneOPNS() {
		return new EuclideanParametersFromPlaneOPNS((new CGAPlaneOPNS(inner.get(0))).decomposeFlat());
	}

	public Vector3d firstDecomposeAttitudeIPNS() {
		return (new CGAAttitudeIPNS(inner.get(0))).direction();
	}

	// Das geht so nicht, da result argument type identisch mit vorheriger Methode
	// vielleicht zwei verschiedenen Klassen bauen die von Vector3d erben ...
	//TODO
	/*public Vector3d firstDecomposeAttitudeOPNS() {
		return (new CGAAttitudeOPNS(inner.get(0))).direction();
	}*/
	public Quat4d firstDecomposeRotor() {
		return (new CGARotor(inner.get(0))).decompose();
	}

	/*public PointPair firstDecomposePointPairIPNS() {
		return (new CGAPointPairIPNS(inner.get(0))).decomposePoints();
	}*/
	public PointPair firstDecomposePointPairOPNS() {
		//TODO herausfinden ob der Multivector ein ipns oder ein opns point-pair ist und entsprechend casten
		return (new CGAPointPairOPNS(inner.get(0))).decomposePoints();
	}

	public Point3d firstDecomposeRoundPointIPNS() {
		return (new CGARoundPointIPNS(inner.get(0))).location();
	}

	public Point3d[] decomposePoints() {
		return inner.stream().map(mv -> (new CGARoundPointIPNS(mv)).location()).toArray(Point3d[]::new);
	}

	/*public iCGATangentOrRound.EuclideanParameters decomposeTangentOrRound() {
		return inner.decomposeTangentOrRound();
	}*/
}
