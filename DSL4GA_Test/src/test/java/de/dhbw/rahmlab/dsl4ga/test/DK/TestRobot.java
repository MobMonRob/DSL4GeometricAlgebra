package de.dhbw.rahmlab.dsl4ga.test.DK;

import static java.lang.Math.PI;
import org.jogamp.vecmath.Matrix4d;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class TestRobot {
    
    public record DHAxes(Vector3d[] z, Vector3d[] x, Point3d[] c){}
    
    
    public void init() {
       
		double[] d = new double[]{0d, 0.1625, 0d, 0d, 0.1333, 0.0997, 0.0996};
        // use the signs of the nominal values to define the directions of a --> theta, a, alpha
        double[] a = new double[]{0d, 0d,  -0.425, -0.3922, 0d, 0d, 0d};
        double[] alpha_rad = new double[]{0d, PI/2d, 0d, 0d, PI/2d, -PI/2d, 0d};
		double[] theta_rad = new double[6];
		
        DHAxes axes = determineDHAxesFromDH(theta_rad, alpha_rad, d, a);
		
        //float radius = (float) 0.01;
        for (int i=0;i<axes.z.length;i++){
            //Arrow arrow = new Arrow();
            //pos, dir, length
            double x = axes.c[i].x;
            double y = axes.c[i].y;
            double z = axes.c[i].z;
            double dx = axes.z[i].x;
            double dy = axes.z[i].y;
            double dz = axes.z[i].z;
            if (Math.abs(axes.c[i].y) > 1){
                System.out.println("Axis["+String.valueOf(i)+"] moved to o(y=0)!");
                // oder Ursprung so verschieben, dass y=0 wird
                double t = -y/dy;
                x +=t*dx;
                y = 0;
                z +=t*dz;
            }
            float length = 0.4f;
            // der richtungsvektor sollte die Length 4 haben
            Vector3d dir = new Vector3d(dx,dy,dz);
            dir.normalize();
            dir.scale(length);
			
            //arrow.setData(new Point3d(x,y,z), dir,radius,10,0, darkred, "label");
            //    arrow.setWireframeDisplayed(false);
            //chart.add(arrow);
            
        }
    }
    
    /**
     * Determine DH-axes from DH parameters.
     * 
     * @param theta with 0 as first value in [°] (7 values for UR5e)
     * @param alpha with 0 as first value in [°] (7 values for UR5e)
     * @param d with 0 as first value in [m]
     * @param r with 0 as first value in [m]
     * @return axes of all of the DH frames.
     */
    public static DHAxes determineDHAxesFromDH(double[] theta, double[] alpha, double[] d, 
                                       /*double[] dn,*/ double[] r/*, boolean[] signR*/){
        
        Vector3d[] z = new Vector3d[alpha.length];
        Vector3d[] x = new Vector3d[alpha.length]; // reale Ausrichtung der x-Achsen
        Point3d[]  c = new Point3d[alpha.length];
        
        System.out.println("Denavit Hartenberg:");
        System.out.println("------------------");
        
        //  Basis-Koordinatensystem
        
        z[0] = new Vector3d(0d,0d,1d); // Ausrichtung er ersten Drehachse
        // x[0] zeigt von der vorherigen Achse, also dem Basis-System auf die erste
        // Achse also Globe2Base - um keine unnötigen delta-thetas zu erzeugen, sollte
        // x[0] in Richtung von z[2] in der neutral-pose zeigen, die die nach z[1] folgenden zweite joint-axis
        // sollte ja in der neutral-pose gerade die Ausgangs-x-Richtung definieren
        x[0] = new Vector3d(1d,0d,0d);
        c[0] = new Point3d(0d,0d,0d);
        System.out.println("o0= ("+String.valueOf(c[0].x)+","+String.valueOf(c[0].y)+","+String.valueOf(c[0].z)+")");
        System.out.println("z0= ("+String.valueOf(z[0].x)+","+String.valueOf(z[0].y)+","+String.valueOf(z[0].z)+")");
        
        Matrix4d dhm = new Matrix4d();
        dhm.setIdentity();
        
        //TODO
        // unklar, wie ich das Vorzeichen im Modell von a2-a4 berücksichten muss, 
        // außerdem unklar, warum a5-a6 kein Vorzeichen definiert
        // vermutlich muss ich beim Erstellen der matrix die Vorzeichen gar nicht berücksichtigen!!!!
        
        // 
        for (int i=1;i<alpha.length;i++){
            System.out.println("Input("+String.valueOf(i)+"): alpha="+String.valueOf(alpha[i]*180d/PI)+
                    "°, theta="+String.valueOf(theta[i]*180d/PI)+
                    "°, d="+String.valueOf(d[i]*1000d)+"mm, r="+String.valueOf(r[i]*1000)+"mm");
            
            // origin in lokalen Koordinaten
            Point3d cc = new Point3d(0d,0d,0d);
            // z-Richtung in lokalen Koordinaten
            Vector3d zz = new Vector3d(0d,0d,1d);
            // x-Richtung in lokalen Koordinaten
            Vector3d xx = new Vector3d(1d,0d,0d);
        
            dhm.mul(new DH(theta[i], alpha[i], d[i], r[i]).toMatrix4d());
            
            dhm.transform(cc);
            System.out.println("o"+String.valueOf(i)+"= ("+String.valueOf(cc.x)+","+String.valueOf(cc.y)+","+String.valueOf(cc.z)+")");
            c[i] = new Point3d(cc.x,cc.y,cc.z);
            
            dhm.transform(zz);
            System.out.println("z"+String.valueOf(i)+"= ("+String.valueOf(zz.x)+","+String.valueOf(zz.y)+","+String.valueOf(zz.z)+")");
            z[i] = new Vector3d(zz);
            
            dhm.transform(xx);
            System.out.println("x"+String.valueOf(i)+"= ("+String.valueOf(xx.x)+","+String.valueOf(xx.y)+","+String.valueOf(xx.z)+")");
            x[i] = new Vector3d(xx);
        }
        
        return new DHAxes(z,x,c);
    }
}
