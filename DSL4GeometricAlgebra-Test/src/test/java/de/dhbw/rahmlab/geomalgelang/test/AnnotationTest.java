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
    public static double eps = 1e-12;
    public static final String[] baseVectorNames = {"eo", "e1", "e2", "e3", "ei"};
    
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
    
    public static String toString(String name, double[] coordinates){
        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append("=(");
        for (int i=0;i<coordinates.length;i++){
            result.append(String.valueOf(coordinates[i]));
            result.append(",");
        }
        result.deleteCharAt(result.length()-1);
        result.append(")");
        return result.toString();
    }
    @Test
    void compositionOfRoundPointIPNS() {
        // rp1=(1.0,1.0,2.0,3.0,6.999999999999998,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)
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
        //FIXME
        // alle drei verschieden
        System.out.println(toString("s1",s1));
        System.out.println(toString("s2",s2));
        System.out.println(toString("s3",s3));
        assertTrue(equals(s1,s2,eps));
    }
    
    @Test
    void compositionOfPlaneIPNS(){
        Point3d p = new Point3d(1d,2d,3d);
        Vector3d n = new Vector3d(0d,0d,1d);
        double d = Math.abs(p.x*n.x+p.y*n.y+p.z*n.z)/Math.abs(n.length()); // vorzeichen kann falsch werden
        double[] p1 = WrapperGen.INSTANCE.planeIPNS(n, p);
        //System.out.println(toString("p1",p1));
        double[] p2 = WrapperGen.INSTANCE.planeIPNS1(n, d);
        //System.out.println(toString("p2",p2));
        double[] p3 = WrapperGen.INSTANCE.planeIPNS2(p, n);
        assertTrue(equals(p1,p3, eps));
        //System.out.println(toString("p3",p3));
        assertTrue(equals(p1,p2, eps));
    }
    
    @Test
    void compositionOfScalarIPNS(){
        double scalar = 2.5d;
        double[] s1 = WrapperGen.INSTANCE.scalarIPNS(scalar);
        //System.out.println(toString("scl1",s1));
        assertTrue(equals(s1[31], scalar, eps));
        double[] s2 = WrapperGen.INSTANCE.scalarIPNS2(scalar);
        assertTrue(equals(s1[31], scalar, eps));
        //System.out.println(toString("scl2",s2));
    }
}
