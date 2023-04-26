package de.dhbw.rahmlab.geomalgelang.api;

import de.orat.math.cga.api.iCGAFlat;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class EuclideanParametersFromFlat {
	private final  Vector3d attitude;
	private final Point3d location;
	private final double squaredWeight;
	
	public EuclideanParametersFromFlat(iCGAFlat.EuclideanParameters parameters){
		this.attitude = parameters.attitude();
		this.location = parameters.location();
		this.squaredWeight = parameters.squaredWeight();
	}
	
	public Vector3d attitude(){
		return attitude;
	}
	public Point3d location(){
		return location;
	}
	public double squaredWeight(){
		return squaredWeight;
	}
}
