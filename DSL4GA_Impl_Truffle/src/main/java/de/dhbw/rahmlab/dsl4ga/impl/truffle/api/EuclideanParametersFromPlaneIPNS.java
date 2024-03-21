package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.orat.math.cga.api.iCGAFlat;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class EuclideanParametersFromPlaneIPNS extends EuclideanParametersFromFlat{
	public EuclideanParametersFromPlaneIPNS(iCGAFlat.EuclideanParameters parameters){
		super(parameters);
	}
}
