package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import de.orat.math.cga.api.CGASphereIPNS;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Tuple3d;
import org.jogamp.vecmath.Vector3d;

public interface Wrapper {

    // For testing composition of CGA objects
    
    // ipns representation
    
        @CGA("x+0.5x²εᵢ+ε₀")
		double[] roundPointIPNS(Point3d x_euclidean_vector_opns);
        
        @CGA("x")
		double[] roundPointIPNS2(Point3d x_round_point_ipns);
        
        @CGA("x+0.5(x² - r²)εᵢ+ε₀")
		double[] realSphereIPNS(Point3d x_euclidean_vector_opns, double r_scalar_opns);
        
        @CGA("x")
		double[] sphereIPNS2(Point3d x_sphere_ipns_1, double x_sphere_ipns_2); // korrekt
        
        @CGA("p-0.5r²εᵢ")
		double[] realSphereIPNS3(Point3d p_round_point_ipns, double r_scalar_opns); 
        
        // FIXME Die Variable in der Formel darf nicht "n" genannt werden, da n bereits
        // als Symbol definiert ist. Bessere Fehlermeldung!
        @CGA("nn+(x⋅nn)εᵢ")
		double[] planeIPNS(Point3d x_euclidean_vector_opns, Vector3d nn_euclidean_vector_opns);
        
        @CGA("nn+dεᵢ")
        double[] planeIPNS1(Vector3d nn_euclidean_vector_opns, double d_scalar_opns);
        
        @CGA("x")
        double[] planeIPNS2(Point3d x_plane_ipns_1, Vector3d x_plane_ipns_2);
        
        @CGA("x")
        double[] planeIPNS3(Vector3d x_plane_ipns_1, double x_plane_ipns_2);
        
        // Kugel mit Ebene geschnitten und in eine Summe umgeformt
        // passt nicht mit 2,3 zusammen
        @CGA("ε₀∧nn+(x⋅nn)E₀+x∧nn+((x⋅nn)x-0.5(x²-r²)nn)∧εᵢ")
        double[] circleIPNS(Point3d x_euclidean_vector_opns, Vector3d nn_euclidean_vector_opns, 
			double r_scalar_opns);
        
        //FIXME
        // Konstruktor: e23 ist ist doppelt so gross
        @CGA("x")
        double[] circleIPNS2(Point3d x_circle_ipns_1, Vector3d x_circle_ipns_2, double x_circle_ipns_3);
        
        // Kugel mit Ebene geschnitten, mit Formel
        @CGA("(ε₀+x+0.5(x²-r²)εᵢ)∧(nn+(x⋅nn)εᵢ)")
        double[] circleIPNS3(Point3d x_euclidean_vector_opns, Vector3d nn_euclidean_vector_opns, 
			double r_scalar_opns);
        
        
        @CGA("nn∧x+(0.5x²nn-x (x⋅nn))εᵢ+nnε₀-(x⋅nn)E₀")
        double[] orientedPointIPNS(Point3d x_euclidean_vector_opns, Vector3d nn_euclidean_vector_opns);
        
        @CGA("x")
        double[] orientedPointIPNS2(Point3d x_oriented_point_ipns_1, Vector3d x_oriented_point_ipns_2);
        
		
		// line
		
		// ipns
		
        @CGA("(nn+(x∧nn)εᵢ)E₃")
        double[] lineIPNS(Point3d x_euclidean_vector_opns, Vector3d nn_euclidean_vector_opns);
        
        @CGA("x")
        double[] lineIPNS2(Point3d x_line_ipns_1, Vector3d x_line_ipns_2);
       
		// composition via opns representation via dual
		// vermutlich fehlt hier die Normalisierung
		@CGA("(p1∧p2∧εᵢ)*")
        double[] lineIPNS3(Point3d p1_round_point_ipns, Point3d p2_round_point_ipns);
		
		@CGA("x*")
        double[] lineIPNS4(Point3d x_line_opns_1, Point3d x_line_opns_2);
		
		
		// opns
		
		@CGA("p1∧p2∧εᵢ")
        double[] lineOPNS(Point3d p1_round_point_ipns, Point3d p2_round_point_ipns);
		
		@CGA("x")
        double[] lineOPNS2(Point3d x_line_opns_1, Point3d x_line_opns_2);
		
		@CGA("x1∧x2∧εᵢ+(x1-x2)E₀")
        double[] lineOPNS3(Point3d x1_euclidean_vector_opns, Point3d x2_euclidean_vector_opns);
		
		
		// point-pair
		
		// ipns
		
		// composition via formula
        @CGA("(ε₀∧nn+x∧nn∧E₀-(x⋅nn)-((x⋅nn) x-0.5(x²+r²)nn)∧εᵢ)E₃")
        double[] pointPairIPNS(Point3d x_euclidean_vector_opns, 
			                   Vector3d nn_euclidean_vector_opns, double r_scalar_opns);
        //FIXME
        // pointpair_ipns2 die 2 musste ich hinzufügen, da sonst nicht von pointPairINPNS4 unterscheidbar
        // und compile-Fehler entstanden.
		// composition via constructor
        @CGA("x")
        double[] pointpairIPNS2(Point3d x_pointpair_ipns2_1, Vector3d x_pointpair_ipns2_2,  
			double x_pointpair_ipns2_3);
        
		// composition of a normalized point-pair via opns constructor and dual
        @CGA("x*")
        double[] pointPairIPNS3(Point3d x_pointpair_opns_1, Point3d x_pointpair_opns_2);
       
		// composition of a normalized point-pair via ipns construtor but intern dual of opns
        @CGA("x")
        double[] pointPairIPNS4(Point3d x_pointpair_ipns_1, Point3d x_pointpair_ipns_2);
       
		// following [Hitzer2004] (grade 2, also OPNS)
		@CGA("(2r (nn∧x+0.5((x²+r²)nn-2(x⋅nn) x)εᵢ+nn ε₀+(x⋅nn) E₀))*")
        double[] pointPairIPNS5(Point3d x_euclidean_vector_opns, Vector3d nn_euclidean_vector_opns, 
			double r_scalar_opns);
       
        @CGA("(x∧εᵢ-1)E₃")
        double[] flatPointIPNS(Point3d x_euclidean_vector_opns);
        
        @CGA("x")
        double[] flatPointIPNS2(Point3d x_flat_point_ipns);
        
        @CGA("s E")
        double[] scalarIPNS(double s_scalar_opns);
        
        @CGA("x")
        double[] scalarIPNS2(double x_scalar_ipns);
        
        
    // opns respresentation
        
        @CGA("x")
		double[] roundPointOPNS(Point3d x_round_point_opns);
        
        @CGA("s1∧s2∧s3∧s4")
		double[] roundPointOPNS2(Point3d s1_sphere_ipns_1, double s1_sphere_ipns_2, 
                                 Point3d s2_sphere_ipns_1, double s2_sphere_ipns_2,
                                 Point3d s3_sphere_ipns_1, double s3_sphere_ipns_2,
                                 Point3d s4_sphere_ipns_1, double s4_sphere_ipns_2);
        

        @CGA("p1∧p2∧p3∧p4")
        double[] sphereOPNS(Point3d p1_round_point_ipns, Point3d p2_round_point_ipns, 
                Point3d p3_round_point_ipns, Point3d p4_round_point_ipns);
		
		@CGA("x")
		double[] sphereOPNS2(Point3d x_sphere_opns_1, double x_sphere_opns_2);
        
		
		@CGA("p1∧p2∧p3∧εᵢ")
        double[] planeOPNS(Point3d p1_round_point_ipns, Point3d p2_round_point_ipns, Point3d p3_round_point_ipns);
        
		@CGA("x")
        double[] planeOPNS2(Point3d x_plane_opns, Vector3d x_plane_opns_2);
        
		
		@CGA("p1∧p2")
        double[] pointPairOPNS2(Point3d p1_round_point_ipns, Point3d p2_round_point_ipns);
		
        @CGA("x")
        double[] pointPairOPNS(Point3d x_pointpair_opns_1, Point3d x_pointpair_opns_2);
		
		
		// complex tests
		// ipns real sphere wedge orthogonal ipns plane ergibt ein imaginary point pair opns
		// r2 < 0 testen, Berechung nach CGALua via center of point pair
		//FIXME Normierung mit weight bevor c, r2 bestimmt werden
		@CGA(
		"""
		s:=x+0.5(x² - r²)εᵢ+ε₀
		p:=nn+(x⋅nn)εᵢ
		PP:=s∧p
        w:=E₀⋅(PP∧εᵢ)E₃
		PP:= PP/w
		c:=-nn(E₀⋅(PP∧(ε₀εᵢ)))E₃
		r2:=-c²+2((E₀⋅(ε₀∧PP))E₃+(c⋅nn)c)nn
		""")
		double testImaginaryPointPair(Point3d x_euclidean_vector_opns, 
			                          double r_scalar_opns, Vector3d nn_euclidean_vector_opns);
		
		/*@CGA("""
			 pp:=(ε₀∧nn+x∧nn∧E₀-(x⋅nn)-((x⋅nn) x-0.5(x²+r²)nn)∧εᵢ)E₃
             w:=
		     blade:=pp/w
		     nn:=
			 c:=
		     r2:=""")
		double decomposePointPair(Point3d x_euclidean_vector_opns, 
			                   Vector3d nn_euclidean_vector_opns, double r_scalar_opns);
		*/
}
