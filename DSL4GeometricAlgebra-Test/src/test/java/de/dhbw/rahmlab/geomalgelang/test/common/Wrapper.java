package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import de.orat.math.cga.api.CGASphereIPNS;
import de.orat.math.cga.api.iCGATangentOrRound;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

public interface Wrapper {

    // For testing composition of CGA objects
    
    // ipns representation
    
        @CGA("x+0.5x²εᵢ+ε₀")
		double[] roundPointIPNS(Point3d x_euclidean_vector);
        
        @CGA("x")
		double[] roundPointIPNS2(Point3d x_round_point_ipns);
        
        @CGA("x+0.5(x² - r²)εᵢ+ε₀")
		double[] realSphereIPNS(Point3d x_euclidean_vector, double r_scalar_opns);
        
        @CGA("x")
		double[] sphereIPNS2(Point3d x_sphere_ipns_1, double x_sphere_ipns_2); // korrekt
        
        @CGA("p-0.5r²εᵢ")
		double[] realSphereIPNS3(Point3d p_round_point_ipns, double r_scalar_opns); 
        
        // FIXME Die Variable in der Formel darf nicht "n" genannt werden, da n bereits
        // als Symbol definiert ist. Bessere Fehlermeldung!
        @CGA("nn+(x⋅nn)εᵢ")
		double[] planeIPNS(Point3d x_euclidean_vector, Vector3d nn_euclidean_vector);
        
        @CGA("nn+dεᵢ")
        double[] planeIPNS1(Vector3d nn_euclidean_vector, double d_scalar_opns);
        
        @CGA("x")
        double[] planeIPNS2(Point3d x_plane_ipns_1, Vector3d x_plane_ipns_2);
        
        @CGA("x")
        double[] planeIPNS3(Vector3d x_plane_ipns_1, double x_plane_ipns_2);
        
        // Kugel mit Ebene geschnitten und in eine Summe umgeformt
        // passt nicht mit 2,3 zusammen
        @CGA("ε₀∧nn+(x⋅nn)E₀+x∧nn+((x⋅nn)x-0.5(x²-r²)nn)∧εᵢ")
        double[] circleIPNS(Point3d x_euclidean_vector, Vector3d nn_euclidean_vector, 
			double r_scalar_opns);
        
        //FIXME
        // Konstruktor: e23 ist ist doppelt so gross
        @CGA("x")
        double[] circleIPNS2(Point3d x_circle_ipns_1, Vector3d x_circle_ipns_2, double x_circle_ipns_3);
        
        // Kugel mit Ebene geschnitten, mit Formel
        @CGA("(ε₀+x+0.5(x²-r²)εᵢ)∧(nn+(x⋅nn)εᵢ)")
        double[] circleIPNS3(Point3d x_euclidean_vector, Vector3d nn_euclidean_vector, 
			double r_scalar_opns);
        
        
        @CGA("nn∧x+(0.5x²nn-x (x⋅nn))εᵢ+nnε₀-(x⋅nn)E₀")
        double[] orientedPointIPNS(Point3d x_euclidean_vector, Vector3d nn_euclidean_vector);
        
        @CGA("x")
        double[] orientedPointIPNS2(Point3d x_oriented_point_ipns_1, Vector3d x_oriented_point_ipns_2);
        
		
		// line
		
		// ipns
		
        @CGA("(nn+(x∧nn)εᵢ)E₃")
        double[] lineIPNS(Point3d x_euclidean_vector, Vector3d nn_euclidean_vector);
        
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
        double[] lineOPNS3(Point3d x1_euclidean_vector, Point3d x2_euclidean_vector);
		
		
		// point-pair
		
		// ipns
		
		// composition via formula from lua-based code
		// real point-pair only
		// scheint nicht normalisiert zu sein
        @CGA("(ε₀∧nn+x∧nn∧E₀+(x⋅nn)-((x⋅nn) x-0.5(x²+r²)nn)∧εᵢ)E₃")
        double[] pointPairIPNS(Point3d x_euclidean_vector, 
			                   Vector3d nn_euclidean_vector, double r_scalar_opns);
		
        // pointpair_ipns2 die 2 musste ich hinzufügen, da sonst nicht von pointPairINPNS4 unterscheidbar
        // und compile-Fehler entstanden. Fehler scheint behoben zu sein
		// composition via constructor
        @CGA("x")
        double[] pointpairIPNS2(Point3d x_pointpair_ipns_1, Vector3d x_pointpair_ipns_2,  
			double x_pointpair_ipns_3);
        
		// composition of a normalized point-pair via opns constructor and dual
        @CGA("x*")
        double[] pointPairIPNS3(Point3d x_pointpair_opns_1, Point3d x_pointpair_opns_2);
       
		// composition of a normalized point-pair via ipns construtor but intern dual of opns
        @CGA("x")
        double[] pointPairIPNS4(Point3d x_pointpair_ipns_1, Point3d x_pointpair_ipns_2);
       
		// following [Hitzer2004] (grade 2, also OPNS)
		// magnitude scheint verdoppelt
		@CGA("normalize((2r (nn∧x+0.5((x²+r²)nn-2(x⋅nn) x)εᵢ+nn ε₀+(x⋅nn) E₀))*)")
        double[] pointPairIPNS5(Point3d x_euclidean_vector, Vector3d nn_euclidean_vector, 
			double r_scalar_opns);
       
		// following [Dorst2009] 14.10
		// aber in Form der create()-Methode klappt es nicht - worin besteht der Unterschied?
		@CGA("(p-0.5r²εᵢ)∧(-p⌋((nn^/E₃)εᵢ))")
		double[] pointPairIPNS6(Point3d p_round_point_ipns, Vector3d nn_euclidean_vector, 
			double r_scalar_opns);
		
		
		// flat-point
		
        @CGA("(x∧εᵢ-1)E₃")
        double[] flatPointIPNS(Point3d x_euclidean_vector);
        
        @CGA("x")
        double[] flatPointIPNS2(Point3d x_flat_point_ipns);
        
		
		// scalar
		
        @CGA("s E")
        double[] scalarIPNS(double s_scalar_opns);
        
        @CGA("x")
        double[] scalarIPNS2(double x_scalar_ipns);
        
		@CGA("atan2(x,y)")
		double atan2(double x_scalar_ipns, double y_scalar_ipns);
		
        
    // opns respresentation
        
        @CGA("x")
		double[] roundPointOPNS(Point3d x_round_point_opns);
        
        @CGA("s1∧s2∧s3∧s4")
		double[] roundPointOPNS2(Point3d s1_sphere_ipns_1, double s1_sphere_ipns_2, 
                                 Point3d s2_sphere_ipns_1, double s2_sphere_ipns_2,
                                 Point3d s3_sphere_ipns_1, double s3_sphere_ipns_2,
                                 Point3d s4_sphere_ipns_1, double s4_sphere_ipns_2);
        

		
        @CGA("x")
        double[] pointPairOPNS(Point3d x_pointpair_opns_1, Point3d x_pointpair_opns_2);
		
		@CGA("p1∧p2")
        double[] pointPairOPNS2(Point3d p1_round_point_ipns, Point3d p2_round_point_ipns);
		
		@CGA("normalize(p1∧p2)")
        double[] normalizePointPairOPNS2(Point3d p1_round_point_ipns, Point3d p2_round_point_ipns);
		
		@CGA("(p+0.5r²εᵢ)∧(-p⌋((nn^)εᵢ))")
		double[] pointPairOPNS3(Point3d p_round_point_ipns, Vector3d nn_euclidean_vector, 
			double r_scalar_opns);
		
        @CGA("p1∧p2∧p3∧p4")
        double[] sphereOPNS(Point3d p1_round_point_ipns, Point3d p2_round_point_ipns, 
                Point3d p3_round_point_ipns, Point3d p4_round_point_ipns);
		
		@CGA("x")
		double[] sphereOPNS2(Point3d x_sphere_opns_1, double x_sphere_opns_2);
        
		
		@CGA("p1∧p2∧p3∧εᵢ")
        double[] planeOPNS(Point3d p1_round_point_ipns, Point3d p2_round_point_ipns, Point3d p3_round_point_ipns);
        
		@CGA("x")
        double[] planeOPNS2(Point3d x_plane_opns, Vector3d x_plane_opns_2);
        
		
		
		// complex tests
		
		// ipns real sphere wedge orthogonal ipns plane results in an imaginary point pair opns
		// test r2 < 0, Calculation following CGALua via center of point pair
		//FIXME Why is normalization needed with weight before c, r2 determination?
		@CGA(
		"""
		s:=x+0.5(x²-r²)εᵢ+ε₀
		p:=nn+(x⋅nn)εᵢ
		PP:=s∧p
        w:=E₀⋅(PP∧εᵢ)E₃
		PP:= PP/w
		c:=-nn (E₀⋅(PP∧(ε₀εᵢ)))E₃
		-c²+2((E₀⋅(ε₀∧PP))E₃+(c⋅nn)c)nn
		""")
		double testImaginaryPointPairRadius(Point3d x_euclidean_vector, 
			                          double r_scalar_opns, Vector3d nn_euclidean_vector);
		
		@CGA(
		"""
		s:=x+0.5(x²-r²)εᵢ+ε₀
		p:=nn+(x⋅nn)εᵢ
		PP:=s∧p
        w:=E₀⋅(PP∧εᵢ)E₃
		PP:= PP/w
		-nn (E₀⋅(PP∧(ε₀εᵢ)))E₃
		""")
		double[] testImaginaryPointPairCenter(Point3d x_euclidean_vector, 
			                          double r_scalar_opns, Vector3d nn_euclidean_vector);
		
		@CGA(
		"""
		s:=x+0.5(x²-r²)εᵢ+ε₀
		p:=nn+(x⋅nn)εᵢ
		s∧p
		""")
		iCGATangentOrRound.EuclideanParameters testImaginaryPointPair(Point3d x_euclidean_vector, 
			                          double r_scalar_opns, Vector3d nn_euclidean_vector);
		
		/*@CGA("""
			 pp:=(ε₀∧nn+x∧nn∧E₀-(x⋅nn)-((x⋅nn) x-0.5(x²+r²)nn)∧εᵢ)E₃
             w:=
		     blade:=pp/w
		     nn:=
			 c:=
		     r2:=""")
		double decomposePointPair(Point3d x_euclidean_vector, 
			                   Vector3d nn_euclidean_vector, double r_scalar_opns);
		*/
		
		// Kommentarzeilen gibts noch nicht, daher folgende entfernt:
		
		@CGA(
			"""
			// dh parameters UR5e
   			a2 := -0.425;
   			a3 := -0.3922;
   			d1 := 0.1625;
   			d4 := 0.1333;
   			d5 := 0.0997;
   			d6 := 0.0996;
   
			// position of the end-effector and joint 5
   			Pe := p+0.5p²εᵢ+ε₀
            P5 :=
			?P_5 = createPoint(p_x - d6 * ae_1, p_y - d6 * ae_2, p_z - d6 * ae_3);

			// sphere around P5
   			Sc := P5-0.5d4²εᵢ
   
			// sphere around the origin
   			K0 := ε₀+ (Sc . ε₀)εᵢ
			// intersection of S_c and K_0;
			?C_5k = S_c ^ K_0;
			// intersection of C_5k and the horizontal plane through P_5
			?Q_c = Dual(C_5k . (P_5 ^ e1 ^ e2 ^ einf));
			// point P_c with an offset d4 from P_5
			?P_c = ExtractFirstPoint(Q_c);
			// plane through joints 1, 2, 3 and 4
			?PI_c = Dual(e0 ^ e3 ^ P_c ^ einf);
			// finding P_3 and P_4
			// plane parallel to PI_c that contains P_4 and P_5
			?PI_c_parallel = PI_c + (P_5 . PI_c) * einf; // eq. 47
			?PI_56_orthogonal = Dual(Dual(P_5 ^ P_e) ^ einf); // eq. 48 l.1
			?n_56_orthogonal = -((Dual(PI_56_orthogonal) . e0) . einf) / abs((Dual(PI_56_orthogonal) . e0) . einf); // eq. 48, l. 2
			?PI_c_orthogonal = Dual(P_5 ^ n_56_orthogonal ^ einf);
			?L_45 = PI_c_parallel ^ PI_c_orthogonal;
			?S_5 = P_5 - (1 / 2 * d5 * d5 * einf);
			?Q_4 = Dual(L_45 . Dual(S_5));
			?P_4 = ExtractFirstPoint(Q_4);

			// point P3
			?S_4 = P_4 + (1 / 2 * d4 * d4 * einf);
			?L_34 = Dual(P_4 ^ PI_c ^ einf);
			?Q_3 = Dual(S_4 . Dual(L_34));
			?P_3 = ExtractFirstPoint(Q_3);

			// finding P1 and P2
			?P_1 = createPoint(0, 0, d1);
			?S_1 = P_1 - (1 / 2 * a2 * a2 * einf);
			?S_3 = P_3 + (1 / 2 * a3 * a3 * einf);
			?C_2 = S_1 ^ S_3;
			?Q_2 = Dual(Dual(C_2) . PI_c);
			?P_2 = ExtractFirstPoint(Q_2);


			// finding the joint angles

			?L_01 = Dual(e0 ^ e3 ^ einf);
			?L_12 = Dual(P_1 ^ P_2 ^ einf);
			?L_23 = Dual(P_2 ^ P_3 ^ einf);

			?P_0 = createPoint(0,0,0);

			?a_1 = e2;
			?b_1 = -(PI_c);
			?N_1 = e1 ^ e2;
			?x1 = (a_1 ^ b_1) / N_1;
			?y1 = a_1 . b_1;

			?a_2 = (Dual(L_01) . e0) . einf;
			?b_2 = (Dual(L_12) . e0) . einf;
			?N_2 = -(Dual(PI_c) . e0) . einf;
			?x2 = (a_2 ^ b_2) / N_2;
			?y2 = a_2 . b_2;

			?a_3 = (Dual(L_12) . e0) . einf;
			?b_3 = (Dual(L_23) . e0) . einf;
			?N_3 = -(Dual(PI_c) . e0) . einf;
			?x3 = (a_3 ^ b_3) / N_3;
			?y3 = a_3 . b_3;

			?a_4 = (Dual(L_23) . e0) . einf;
			?b_4 = (Dual(L_45) . e0) . einf;
			?N_4 = -(Dual(PI_c) . e0) . einf;
			?x4 = (a_4 ^ b_4) / N_4;
			?y4 = a_4 . b_4;

			?a_5 = P_c;
			?b_5 = -(ae_1 * e1 + ae_2 * e2 + ae_3 * e3);
			?N_5 = (-L_45 ^ e0) . einf;
			?x5 = (a_5 ^ b_5) / N_5;
			?y5 = a_5 . b_5;

			?a_6 = (Dual(L_45) . e0) . einf;
			?b_6 = -(se_1 * e1 + se_2 * e2 + se_3 * e3);
			?N_6 = -(ae_1 * e1 + ae_2 * e2 + ae_3 * e3) * (e3 ^ e2 ^ e1);
			?x6 = (a_6 ^ b_6) / N_6;
			?y6 = a_6 . b_6;
		""")
		public double[] ik(Point3d p_euclidean_vector, Vector3d a_euclidean_vector);
}
