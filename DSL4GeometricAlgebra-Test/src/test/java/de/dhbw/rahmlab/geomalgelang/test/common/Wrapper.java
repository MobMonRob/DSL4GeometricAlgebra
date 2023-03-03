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
        @CGA("x+0.5(x² - r²)εᵢ+ε₀")
	double[] realSphereIPNS(Tuple3d x_euclidean_vector_opns, double r_scalar_opns);
        
        @CGA("x")
	double[] sphereIPNS2(Point3d x_sphere_ipns_1, double x_sphere_ipns_2); // korrekt
        
        @CGA("p-0.5r²εᵢ")
	double[] realSphereIPNS3(Tuple3d p_round_point_ipns, double r_scalar_opns); 
        
        // FIXME Vector3d sollte hier möglich sein
        // "n" is not a known variable!
        // FIXME Die Variable in der Formel darf nicht "n" genannt werden, da n bereits
        // als Symbol definiert ist. Bessere Fehlermeldung!
        @CGA("nn+(x⋅nn)εᵢ")
	double[] planeIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d nn_euclidean_vector_opns);
        
        @CGA("nn+dεᵢ")
        double[] planeIPNS1(Tuple3d nn_euclidean_vector_opns, double d_scalar_opns);
        
        @CGA("x")
        double[] planeIPNS2(Point3d x_plane_ipns_1, Vector3d x_plane_ipns_2);
        
        //  Available overloads of method "plane_ipns" do not match the parameter types given for the variable "x"     
        //FIXME build failure obwohl der passende Konstruktor vorhanden ist
        //@CGA("x")
        //double[] planeIPNS3(Vector3d x_plane_ipns_1, double x_plane_ipns_2);
        
        // Kugel mit Ebene geschnitten und in eine Summe umgeformt
        // pass nicht mit 2,3 zusammen
        @CGA("ε₀∧nn+(x⋅nn)E₀+x∧nn+((x⋅nn)x-0.5(x²-r²)nn)∧εᵢ")
        double[] circleIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d nn_euclidean_vector_opns, double r_scalar_opns);
        
        @CGA("x")
        double[] circleIPNS2(Point3d x_circle_ipns_1, Vector3d x_circle_ipns_2, double x_circle_ipns_3);
        
        // Kugel mit Ebene geschnitten
        @CGA("(ε₀+x+0.5(x²-r²)εᵢ)∧(nn+(x⋅nn)εᵢ)")
        double[] circleIPNS3(Tuple3d x_euclidean_vector_opns, Tuple3d nn_euclidean_vector_opns, double r_scalar_opns);
        
        
        @CGA("nn∧x+(0.5x²nn-x (x⋅nn))εᵢ+nnε₀-(x⋅nn)E₀")
        double[] orientedPointIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d nn_euclidean_vector_opns);
        
        @CGA("x")
        double[] orientedPointIPNS2(Point3d x_oriented_point_ipns_1, Vector3d x_oriented_point_ipns_2);
        
        @CGA("(nn+(x∧nn)εᵢ)E₃")
        double[] lineIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d nn_euclidean_vector_opns);
        
        @CGA("x")
        double[] lineIPNS2(Point3d x_line_ipns_1, Vector3d x_line_ipns_2);
       
        @CGA("(ε₀∧nn+x∧nn∧ε₀∧εᵢ-(x⋅nn)-((x⋅nn) x-0.5(x²+r²)nn)∧εᵢ)E₃")
        double[] pointPairIPNS(Tuple3d x_euclidean_vector_opns, Tuple3d nn_euclidean_vector_opns, double r_scalar_opns);
        
        @CGA("x")
        double[] pointpairIPNS2(Point3d x_pointpair_ipns_1, Vector3d x_pointpair_ipns_2, double x_pointpair_ipns_3);
        
        @CGA("x*")
        double[] pointPairIPNS3(Point3d x_pointpair_opns_1, Point3d x_pointpair_opns_2);
       
        @CGA("x")
        double[] pointPairIPNS4(Point3d x_pointpair_ipns_1, Point3d x_pointpair_ipns_2);
       
       
        @CGA("(x∧εᵢ-1)E₃")
        double[] flatPointIPNS(Tuple3d x_euclidean_vector_opns);
        
        @CGA("x")
        double[] flatPointIPNS2(Point3d x_flat_point_ipns);
        
        @CGA("s E")
        double[] scalarIPNS(double s_scalar_opns);
        
        @CGA("x")
        double[] scalarIPNS2(double x_scalar_ipns);
        
        
    // opns respresentation
        
        @CGA("p1∧p2∧p3∧p4")
        double[] sphereOPNS(Tuple3d p1_round_point_ipns, Tuple3d p2_round_point_ipns, 
                Tuple3d p3_round_point_ipns, Tuple3d p4_round_point_ipns);
        
        @CGA("x")
        double[] pointPairOPNS(Point3d x_pointpair_opns_1, Point3d x_pointpair_opns_2);
        
}
