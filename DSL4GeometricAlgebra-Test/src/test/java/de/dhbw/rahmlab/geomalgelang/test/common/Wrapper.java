package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Tuple3d;
import org.jogamp.vecmath.Vector3d;

public interface Wrapper {

    // For testing composition of CGA objects
    
    // ipns representation
    
        @CGA("x+0.5x²εᵢ+ε₀")
	double[] roundPointIPNS(Tuple3d x_euclidean_vector_opns);
        
        @CGA("x")
	double[] roundPointIPNS2(Tuple3d x_round_point_ipns);
        
        //FIXME Point3d ist hier nicht zulässig, sollte es aber
        //FIXME x²-r² liefert eine falsche Zahl, das "-" scheint hier nicht richtig zu funktionieren?
        @CGA("x+0.5(x²-r²)εᵢ+ε₀")
	double[] realSphereIPNS(Tuple3d x_euclidean_vector_opns, double r_scalar_opns);
        
        @CGA("x")
	double[] sphereIPNS2(Point3d x_sphere_ipns_1, double x_sphere_ipns_2); // korrekt
        
        //FIXME das "-" scheint hier nicht richtig zu funktionieren
        @CGA("p-0.5r²εᵢ")
	double[] realSphereIPNS3(Tuple3d p_round_point_ipns, double r_scalar_opns); 
        
        // FIXME Vector3d sollte hier möglich sein
        // "n" is not a known variable!
        // FIXME Die Variable in der Formel darf nicht "n" genannt werden, da n bereits
        // als Symbol definiert ist. Bessere Fehlermeldung!
        @CGA("nn+(x⋅nn)εᵢ")
	double[] planeIPNS(Tuple3d nn_euclidean_vector_opns, Tuple3d x_euclidean_vector_opns);
        
        @CGA("nn+dεᵢ")
        double[] planeIPNS1(Tuple3d nn_euclidean_vector_opns, double d_scalar_opns);
        
        @CGA("x")
        double[] planeIPNS2(Point3d x_plane_ipns_1, Vector3d x_plane_ipns_2);
        
        //FIXME
        // compile failed
        //@CGA("x")
        //double[] planeIPNS3(Vector3d x_plane_ipns_1, double x_plane_ipns_2);
        
        // ohne argument r gibts keinen Fehler
        //FIXME
        @CGA("ε₀∧n+(x⋅n)ε₀∧εᵢ+x∧n+(x⋅n)x-0.5((x²-r²)n)∧εᵢ")
        double[] circleIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d n_euclidean_vector_opns, double r_scalar_opns);
        
        @CGA("x")
        double[] circleIPNS2(Point3d x_circle_ipns_1, Vector3d x_circle_ipns_2, double x_circle_ipns_3);
        
        @CGA("n∧x+(0.5x²n-x(x⋅n))εᵢ+nε₀+(x⋅n)E₃")
        double[] orientedPointIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d n_euclidean_vector_opns);
        
        @CGA("x")
        double[] orientedPointIPNS2(Point3d x_oriented_point_ipns_1, Vector3d x_oriented_point_ipns_2);
        
        @CGA("(nn+(x∧nn)εᵢ)E₃")
        double[] lineIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d nn_euclidean_vector_opns);
        
        @CGA("x")
        double[] lineIPNS2(Point3d x_line_ipns_1, Vector3d x_line_ipns_2);
       
        @CGA("ε₀∧n+x∧n∧ε₀∧εᵢ-(x⋅n)-(x⋅n x)-0.5(x²+r²)n))E₃")
        double[] pointPairIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d n_euclidean_vector_opns, double r_scalar_opns);
        
        @CGA("x")
        double[] pointpairIPNS2(Point3d x_pointpair_ipns_1, Vector3d x_pointpair_ipns_2, double x_pointpair_ipns_3);
        
        @CGA("(1-x∧εᵢ)E₃")
        double[] flatPointIPNS(Tuple3d x_euclidean_vector_opns);
        
        @CGA("x")
        double[] flatPointIPNS2(Point3d x_flat_point_ipns);
        
        @CGA("s E")
        double[] scalarIPNS(double s_scalar_opns);
        
        @CGA("x")
        double[] scalarIPNS2(double x_scalar_ipns);
        
    // opns respresentation
       
}