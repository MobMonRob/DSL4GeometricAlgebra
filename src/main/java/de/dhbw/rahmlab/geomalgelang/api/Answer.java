package de.dhbw.rahmlab.geomalgelang.api;

import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.iCGAFlat;
import de.orat.math.cga.api.iCGAPointPair.PointPair;
import de.orat.math.cga.api.iCGATangentOrRound;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Quat4d;
import org.jogamp.vecmath.Vector3d;

public class Answer {

	protected final CGAMultivector inner;

	protected Answer(CGAMultivector inner) {
		this.inner = inner;
	}

	public double decomposeScalar() {
		return inner.decomposeScalar();
	}

	public iCGATangentOrRound.EuclideanParameters decomposeTangentOrRound() {
		return inner.decomposeTangentOrRound();
	}

	public iCGAFlat.EuclideanParameters decomposeFlat() {
		return inner.decomposeFlat();
	}

	public iCGAFlat.EuclideanParameters decomposeFlat(Point3d probePoint) {
		return inner.decomposeFlat(probePoint);
	}

	public Vector3d decomposeAttitude() {
		return inner.decomposeAttitude();
	}

	public Quat4d decomposeRotor() {
		return inner.decomposeRotor();
	}

	public PointPair decomposePointPair() {
		return inner.decomposePointPair();
	}
}
