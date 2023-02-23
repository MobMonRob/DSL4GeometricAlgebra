package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Tuple3d;
import org.jogamp.vecmath.Vector3d;

public interface Wrapper {

    // For testing composition of CGA objects
    
        @CGA(source = "0.5x²εᵢ+ε₀")
	double[] roundPointIPNS(Tuple3d x_euclidean_vector_opns);
        
        @CGA(source = "x")
	double[] roundPointIPNS2(Tuple3d x_round_point_ipns);
        
        //FIXME Point3d ist hier nicht zulässig, sollte es aber
        @CGA(source = "x+0.5(x²-r²)εᵢ+ε₀")
	double[] sphereIPNS(Tuple3d x_euclidean_vector_opns, double r_scalar);
        
        @CGA(source = "x")
	double[] sphereIPNS2(Point3d x_sphere_ipns_1, double x_sphere_ipns_2);
        
        // FIXME Vector3d sollte hier möglich sein
        @CGA(source = "n+(x⋅n)εᵢ")
	double[] planeIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d n_euclidean_vector_opns);
        
        @CGA(source = "n+dεᵢ")
        double[] planeIPNS1(Tuple3d n_euclidean_vector_opns, double d_scalar);
        
        @CGA(source = "x")
        double[] planeIPNS2(Point3d x_plane_ipns_1, Vector3d x_plane_ipns_2);
        
        //FIXME
        // compile failed
        //@CGA(source = "x")
        //double[] planeIPNS3(Vector3d x_plane_ipns_1, double x_plane_ipns_2);
        
        // ohne argument r gibts keinen Fehler
        //FIXME
        @CGA(source = "ε₀∧n+(x⋅n)ε₀∧εᵢ+x∧n+(x⋅n)x-0.5((x²-r²)n)∧εᵢ")
        double[] circleIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d n_euclidean_vector_opns, double r_scalar);
        
        @CGA(source = "x")
        double[] circleIPNS2(Point3d x_circle_ipns_1, Vector3d x_circle_ipns_2, double x_circle_ipns_3);
        
        @CGA(source = "n∧x+(0.5x²n-x(x⋅n))εᵢ+nε₀+(x⋅n)E₃")
        double[] orientedPointIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d n_euclidean_vector_opns);
        
        @CGA(source = "x")
        double[] orientedPointIPNS2(Point3d x_oriented_point_ipns_1, Vector3d x_oriented_point_ipns_2);
        
        @CGA(source = "(n+(x∧n)εᵢ)E₃")
        double[] lineIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d n_euclidean_vector_opns);
        
        @CGA(source = "x")
        double[] lineIPNS2(Point3d x_line_ipns_1, Vector3d x_line_ipns_2);
       
        @CGA(source = "ε₀∧n+x∧n∧ε₀∧εᵢ-(x⋅n)-(x⋅n x)-0.5(x²+r²)n))E₃")
        double[] pointPairIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d n_euclidean_vector_opns, double r_scalar);
        
        @CGA(source = "x")
        double[] pointpairIPNS2(Point3d x_pointpair_ipns_1, Vector3d x_pointpair_ipns_2, double x_pointpair_ipns_3);
        
        @CGA(source = "(1-x∧εᵢ)E₃")
        double[] flatPointIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d n_euclidean_vector_opns);
        
        @CGA(source = "x")
        double[] flatPointIPNS2(Point3d x_flat_point_ipns);
        
        @CGA(source = "s E")
        double[] scalarIPNS(double s_scalar);
        
        @CGA(source = "x")
        double[] scalarIPNS2(double x_scalar_ipns);
       
}
