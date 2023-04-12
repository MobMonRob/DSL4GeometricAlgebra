package de.dhbw.rahmlab.geomalgelang.api;

import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.iCGAFlat;
import de.orat.math.cga.api.iCGAPointPair.PointPair;
import de.orat.math.cga.api.iCGATangentOrRound;
import java.util.List;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Quat4d;
import org.jogamp.vecmath.Vector3d;

public class Result {

	protected final List<CGAMultivector> inner;

	protected Result(List<CGAMultivector> inner) {
		this.inner = inner;
	}

	public Double firstDecomposeSquaredWeight() {
		return inner.get(0).squaredWeight();
	}

	public double firstDecomposeScalar() {
		return inner.get(0).decomposeScalar();
	}

	public double[][] decomposeDoubleArray() {
		return inner.stream().map(mv -> mv.extractCoordinates()).toArray(double[][]::new);
	}

	public double[] firstDecomposeDoubleArray() {
		return inner.get(0).extractCoordinates();
	}

	public iCGATangentOrRound.EuclideanParameters firstDecomposeTangentOrRound() {
		return inner.get(0).decomposeTangentOrRound();
	}

	public iCGAFlat.EuclideanParameters firstDecomposeFlat() {
		return inner.get(0).decomposeFlat();
	}

	public iCGAFlat.EuclideanParameters firstDecomposeFlat(Point3d probePoint) {
		return inner.get(0).decomposeFlat(probePoint);
	}

	public Vector3d firstDecomposeAttitude() {
		return inner.get(0).decomposeAttitude();
	}

	public Quat4d firstDecomposeRotor() {
		return inner.get(0).decomposeRotor();
	}

	public PointPair firstDecomposePointPair() {
		return inner.get(0).decomposePointPair();
	}
	
	/*public iCGATangentOrRound.EuclideanParameters decomposeTangentOrRound() {
		return inner.decomposeTangentOrRound();
	}*/
}