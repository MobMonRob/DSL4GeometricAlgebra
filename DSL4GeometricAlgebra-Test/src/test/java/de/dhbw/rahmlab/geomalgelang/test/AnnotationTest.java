package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.test.common.gen.WrapperGen;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class AnnotationTest {

    /**
     * Algebra's precision.
     */
    private static double eps = 1e-12;
    
    private static final String[] baseVectorNames = new String[]{"","e0", "e1","e2","e3","einf","e01","e02",
		"e03","e0i","e12","e13","e1i","e23","e2i","e3i","e012","e013",
        "e01i","e023","e02i","e03i","e123","e12i","e13i","e23i","e0123","e012i","e013i","e023i","e123i","e0123i"};
    
    /**
     * Comparison of two multivectors.
     * 
     * @param A
     * @param B
     * @param precision
     * @return false in at minimum one of the 32 compontents differs more than the 
     * precision (defined as eps) between the two multivectors
     */
    public static boolean equals(double[] A, double[] B, double precision){
        for (int i=0;i<32;i++){
            if (Math.abs(A[i]-B[i]) > precision){
                return false;
            }
        }
        return true;
    }
    private static boolean equals(double a, double b, double precision){
        boolean result = true;
        if (Math.abs(a-b) > precision){
            result = false;
        }
        return result;
    }
    
    public static String toString(String name, double[] coordinates, double precision){
        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append("= ");
        boolean isFirstVectorWritten = false;
        if (Math.abs(coordinates[0]) > precision){
            result.append(String.valueOf(coordinates[0]));
            isFirstVectorWritten = true;
        }
        for (int i=1;i<coordinates.length;i++){
            if (Math.abs(coordinates[i]) > precision){
                if (coordinates[i] < 0){
                    result.append("-");
                } else if (isFirstVectorWritten){
                    result.append("+");
                }
                result.append(String.valueOf(Math.abs(coordinates[i])));
                result.append(baseVectorNames[i]);
                isFirstVectorWritten = true;
            }
        }
        result.deleteCharAt(result.length()-1);
        //result.append(")");
        return result.toString();
    }
    
    
    // test ipns compositions 
    
    @Test
    void compositionOfRoundPointIPNS() {
        double[] rp1 = WrapperGen.INSTANCE.roundPointIPNS(new Point3d(1d,2d,3d)); 
        double[] rp2 = WrapperGen.INSTANCE.roundPointIPNS2(new Point3d(1d,2d,3d)); 
        assertTrue(equals(rp1,rp2,eps));
        //System.out.println(toString("rp1",rp1));
        //System.out.println(toString("rp2",rp2));
    }
    
    @Test
    void compositionOfSphereIPNS(){
        Point3d p = new Point3d(1d,2d,3d);
        double r = 2d;
        double[] s1 = WrapperGen.INSTANCE.realSphereIPNS(p, r);
        double[] s2 = WrapperGen.INSTANCE.sphereIPNS2(p, r);
        double[] s3 = WrapperGen.INSTANCE.realSphereIPNS3(p, r);
        //System.out.println(toString("s1",s1, eps)); // falsch
        //System.out.println(toString("s2",s2, eps)); // korrekt
        //System.out.println(toString("s3",s3, eps)); // falsch
        assertTrue(equals(s1,s2,eps));
        assertTrue(equals(s1,s3,eps));
    }
    
    @Test
    void compositionOfPlaneIPNS(){
        Point3d p = new Point3d(1d,2d,3d);
        Vector3d n = new Vector3d(0d,0d,1d);
        double d = Math.abs(p.x*n.x+p.y*n.y+p.z*n.z)/Math.abs(n.length()); // vorzeichen kann falsch werden
        double[] p1 = WrapperGen.INSTANCE.planeIPNS(p,n);
        //System.out.println(toString("p1",p1));
        double[] p2 = WrapperGen.INSTANCE.planeIPNS1(n, d);
        //System.out.println(toString("p2",p2));
        double[] p3 = WrapperGen.INSTANCE.planeIPNS2(p, n);
        assertTrue(equals(p1,p3, eps));
        //System.out.println(toString("p3",p3));
        assertTrue(equals(p1,p2, eps));
        //double[] p4 = WrapperGen.INSTANCE.planeIPNS3(n, d);
        //System.out.println(toString("p4",p4));
        //assertTrue(equals(p1,p4, eps));
    }
    
    @Test
    void compositionOfOrientedPoint(){
        Point3d p = new Point3d(1d,2d,3d);
        Vector3d n = new Vector3d(0d,0d,1d);
        double[] p1 = WrapperGen.INSTANCE.orientedPointIPNS(p,n);
        //System.out.println(toString("p1",p1,eps));
        double[] p2 = WrapperGen.INSTANCE.orientedPointIPNS2(p,n);
        //System.out.println(toString("p2",p2,eps));
        assertTrue(equals(p1,p2, eps));
    }
    
    @Test
    void compositionOfLineIPNS(){
        Point3d p = new Point3d(1d,2d,3d);
        Vector3d n = new Vector3d(0d,0d,1d);
        double[] l1 = WrapperGen.INSTANCE.lineIPNS(p,n);
        //System.out.println(toString("l1",l1));
        double[] l2 = WrapperGen.INSTANCE.lineIPNS2(p,n);
        //System.out.println(toString("l2",l2));
        assertTrue(equals(l1,l2, eps));
    }
    
    @Test
    void compositionOfPointPairIPNS(){
        Point3d p1 = new Point3d(1d,2d,3d);
        Point3d p2 = new Point3d(2d,1d, 4d);
        Point3d p = new Point3d(p1);
        p.add(p2);
        p.scale(0.5d);
        double r = p1.distance(p2)/2d;
		// n from p1 to p2
        Vector3d n = new Vector3d(p2);
        n.sub(p1);
		
		// composition via formula
		double[] pp1 = WrapperGen.INSTANCE.pointPairIPNS(p, n, r);
        System.out.println(toString("pp1",pp1, eps));
        // composition via constructor
        double[] pp2 = WrapperGen.INSTANCE.pointpairIPNS2(p, n, r);
        //System.out.println(toString("pp2",pp2, eps));
        // test if composition via formula is equal to composition via constructor
        assertTrue(equals(pp1,pp2, eps));
        
		//FIXME
        // warum stimmt das nicht mit pp1 überein? Funktioniert dual nicht? oder ist die ipns Formel falsch
        // composition of a normalized point pair via opns constructor and dual
        double[] pp3 = WrapperGen.INSTANCE.pointPairIPNS3(p1,p2);
        System.out.println(toString("pp3",pp3, eps));
        double[] pp4 = WrapperGen.INSTANCE.pointPairIPNS4(p1,p2);
        //System.out.println(toString("pp4",pp4, eps));
        // test ob dual in der Formel mit dem programmatischen dual identisch ist
        assertTrue(equals(pp3,pp4, eps));
		
		double[] pp5 = WrapperGen.INSTANCE.pointPairIPNS5(p, n, r);
        System.out.println(toString("pp5",pp5, eps));
    }
   
    @Test
    void compositionOfFlatPointIPNS(){
        Point3d p = new Point3d(1d,2d,3d);
        double[] f1 = WrapperGen.INSTANCE.flatPointIPNS(p);
        //System.out.println(toString("f1",f1, eps));
        double[] f2 = WrapperGen.INSTANCE.flatPointIPNS2(p);
        //System.out.println(toString("f2",f2, eps));
        assertTrue(equals(f1,f2, eps));
    }
    
    @Test
    void compositionOfCircleIPNS(){
        Point3d p = new Point3d(1d,2d,3d);
        Vector3d n = new Vector3d(0d,0d,1d);
        double r = 2d;
        // Komplexe Formel basierend auf lua code
        double[] c1 = WrapperGen.INSTANCE.circleIPNS(p, n, r);
        
        // c2 = -c3 = -c1
        // Konstruktor basierende auf komplexer Formel lua code
        double[] c2 = WrapperGen.INSTANCE.circleIPNS2(p,n,r);
        // Formel Kugel mit Ebene geschnitten
        double[] c3 = WrapperGen.INSTANCE.circleIPNS3(p, n, r);
        System.out.println(toString("c1",c1,eps));
        System.out.println(toString("c2",c2,eps));
        System.out.println(toString("c3",c3,eps));
        assertTrue(equals(c1, c2, eps));
        assertTrue(equals(c1, c3, eps));
    }
    
    @Test
    void compositionOfScalarIPNS(){
        double scalar = 2.5d;
        double[] s1 = WrapperGen.INSTANCE.scalarIPNS(scalar);
        //System.out.println(toString("scl1",s1));
        assertTrue(equals(s1[31], scalar, eps));
        double[] s2 = WrapperGen.INSTANCE.scalarIPNS2(scalar);
        assertTrue(equals(s2[31], scalar, eps));
        //System.out.println(toString("scl2",s2));
    }
    
    // test opns compositions
    
    @Test
    void compositionOfRoundPointOPNS(){
        Point3d p = new Point3d(1d,0d,0d);
        // mit Konstruktor via ipns dual
        double[] cgaP1 = WrapperGen.INSTANCE.roundPointOPNS(p);
		
		// mit Formel aus 4 Kugeln
        //double[] cgaP2 = WrapperGen.INSTANCE.roundPointOPNS(p1);
    }
    
    @Test
    void compositionOfSphereOPNS(){
        Point3d p1 = new Point3d(1d,0d,0d);
        Point3d p2 = new Point3d(0d,1d,0d);
        Point3d p3 = new Point3d(0d,0d,-1d);
        Point3d p4 = new Point3d(0d,-1d,-0d);
        // s=2.0*eo^e1^e2^e3 - 1.0*e1^e2^e3^ei (korrekt)
        double[] s = WrapperGen.INSTANCE.sphereOPNS(p1,p2,p3,p4);
		System.out.println(toString("s",s,eps));
    }
	
	@Test
	void compositionOfPlaneOPNS(){
		Point3d p1 = new Point3d(1d,0d,0d);
        Point3d p2 = new Point3d(0d,1d,0d);
        Point3d p3 = new Point3d(0d,0d,-1d);
		Vector3d n1 = new Vector3d(p2);
		n1.sub(p1);
		Vector3d n2 = new Vector3d(p3);
		n2.sub(p1);
		Vector3d n = new Vector3d();
		n.cross(n1,n2);
		//TODO
		// Punkte in wecher Reihenfolge um n zu bestimmen, damit das Vorzeichen stimmt?
		double[] pl1 = WrapperGen.INSTANCE.planeOPNS(p1,p2,p3);
		System.out.println(toString("plane_1",pl1, eps));
		double[] pl2 = WrapperGen.INSTANCE.planeOPNS2(p1,n);
		System.out.println(toString("plane_2",pl2, eps));
		
	}
    
    @Test
    void compositionOfPointPairOPNS(){
        Point3d p1 = new Point3d(1d,2d,3d);
        Point3d p2 = new Point3d(2d,1d, 4d);
        Point3d p = new Point3d(p1);
        p.add(p2);
        p.scale(0.5d);
        double r = p1.distance(p2)/2d;
        Vector3d n = new Vector3d(p2);
        n.sub(p1);
        n.normalize();
        
		// via constructor
		double[] pp1 = WrapperGen.INSTANCE.pointPairOPNS(p1,p2);
		//System.out.println(toString("pp1_", pp1, eps));
		// via formula
		double[] pp2 = WrapperGen.INSTANCE.pointPairOPNS2(p1,p2);
		//System.out.println(toString("pp2_", pp2, eps));
		// test if composition via constructor is equals to composition via formula
        assertTrue(equals(pp1,pp2,eps));
    }
}
