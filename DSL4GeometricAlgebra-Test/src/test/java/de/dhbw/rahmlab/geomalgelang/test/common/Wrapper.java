package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import de.orat.math.cga.api.iCGAFlat;
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
        
		@CGA("atan2(y,x)")
		double atan2(double y_scalar_opns, double x_scalar_opns);
		
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
        
		@CGA("(ε₀∧P3∧P∧εᵢ)*")
		public double[] planePC1(Point3d P3_round_point_ipns, Point3d P_round_point_ipns);
		
		@CGA("(ε₀^ε₃^P^εᵢ)*")
		public iCGAFlat.EuclideanParameters planePC(Point3d P_round_point_ipns);
		
		
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
		
		// Kommentarzeilen gibts noch nicht, daher im folgenden zu entfernen
		//TODO
		@CGA(
			"""
			// dh parameters UR5e
   			a2 := -0.425
   			a3 := -0.3922
   			d1 := 0.1625
   			d4 := 0.1333
   			d5 := 0.0997
   			d6 := 0.0996
   
			// position of the end-effector and joint 5
   			Pe := p+0.5p²εᵢ+ε₀
   
            P5e := p-d6 ae
            P5 := P5e+0.5P5e²εᵢ+ε₀
			//P5 = createPoint(px - d6 * ae1, py - d6 * ae2, pz - d6 * ae3)

			// sphere around P5
   			Sc := P5-0.5d4²εᵢ
   
			// sphere around the origin
   			K0 := ε₀+(Sc⋅ε₀)εᵢ
			// intersection of Sc and K0
   			C5k := Sc^K0
			// intersection of C5k and the horizontal plane through P5
   			Qc := (C5k⋅(P5^ε₁^ε₂^εᵢ))*
			// point Pc with an offset d4 from P5
   			Pc := ExtractFirstPoint(Qc)
			// plane through joints 1, 2, 3 and 4
   			PIc := (ε₀^ε₃^Pc^εᵢ)*
			// finding P3 and P4
			// plane parallel to PIc that contains P4 and P5
   			PIc_parallel := PIc + (P5⋅PIc)εᵢ // eq. 47
   			PI56_orthogonal := ((P5^Pe)*^εᵢ)* // eq. 48 l.1
   			n56_orthogonal  := -((PI56_orthogonal*⋅ε₀)⋅εᵢ)/abs((PI56_orthogonal*⋅ε₀)⋅εᵢ) // eq. 48, l. 2
   			PIc_orthogonal := (P5^n56_orthogonal^εᵢ)*
   			L45 := PIc_parallel^PIc_orthogonal
   			S5  := P5-(0.5d5²εᵢ)
			Q4  := (L45⋅S5*)*
			P4  := ExtractFirstPoint(Q4)

			// point P3
   			S4  := P4 + (0.5d4²εᵢ)
   			L34 := (P4^PIc^εᵢ)*
   			Q3  := (S4⋅L34*)*
   			P3  := ExtractFirstPoint(Q3)

			// finding P1 and P2
   			P1 := createPoint(0, 0, d1)
   			S1 := P1-0.5a2²εᵢ
   			S3 := P3+0.5a3²εᵢ
   			C2 := S1^S3
   			Q2 := (C2*⋅PIc)*
   			P2 := ExtractFirstPoint(Q2)


			// finding the joint angles

   			L01 := (ε₀^ε₃^εᵢ)*
   			L12 := (P1^P2^εᵢ)*
   			L23 := (P2^P3^εᵢ)*

   			P0 := ε₀ //createPoint(0,0,0);

   			a1 := ε₂
   			b1 := -PIc
   			N1 := ε₁^ε₂
   			x1 := (a1^b1)/N1
   			y1 := a1⋅b1;

   			a2 := (L01*⋅ε₀)⋅εᵢ
   			b2 := (L12*⋅ε₀)⋅εᵢ
   			N2 := -(PIc*⋅ε₀)⋅εᵢ
   			x2 := (a2^b2)/N2
   			y2 := a2⋅b2

   			a3 := (L12*⋅ε₀)⋅εᵢ
   			b3 := (L23*⋅ε₀)⋅εᵢ
   			N3 := -(PIc*⋅ε₀)⋅εᵢ
   			x3 := (a3^b3)/N3
   			y3 := a3⋅b3

   			a4 := (L23*⋅ε₀)⋅εᵢ
   			b4 := (L45*⋅ε₀)⋅εᵢ
   			N4 := -(PIc*⋅ε₀)⋅εᵢ
   			x4 := (a4^b4)/N4
   			y4 := a4⋅b4

   			a5 := Pc;
   			b5 := -ae
   			N5 := (-L45^ε₀)⋅εᵢ
   			x5 := (a5∧b5)/N5
   			y5 := a5⋅b5

   			a6 := (L45*⋅ε₀)⋅εᵢ
   			b6 := -se
   			N6 := -ae (ε₃^ε₂^ε₁)
   			x6 := (a6∧b6)/N6
   			y6 := a6⋅b6
            atan2(y1,x1), atan2(y2,x2), atan2(y3,x3), atan2(y4, x4), atan2(y5, y5), atan2(y6,x6)
		""")
		public double[] ik(Point3d p_euclidean_vector, Vector3d ae_euclidean_vector, Vector3d se_euclidean_vector);
}
