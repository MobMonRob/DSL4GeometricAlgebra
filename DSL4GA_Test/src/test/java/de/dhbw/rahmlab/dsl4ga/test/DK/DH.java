package de.dhbw.rahmlab.dsl4ga.test.DK;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import org.jogamp.vecmath.Matrix3d;
import org.jogamp.vecmath.Matrix4d;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

/**
 * Denavit Hartenberg model.
 * 
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class DH {

    private static boolean debug = false;
    
    private final double theta;
    private final double alpha;
    private final double d;
    private final double r;

    // only != null if the object is defined by Achses/CSs
    private Point3d origin;

    public static void setDebug(boolean value){
        debug = value;
    }
    public DH(double theta, double alpha, double d, double r) {
        this.theta = theta;
        this.alpha = alpha;
        this.d = d;
        this.r = r;
    }

    public DH(double theta, double alpha, double d, double r, Point3d o) {
        this.theta = theta;
        this.alpha = alpha;
        this.d = d;
        this.r = r;
        this.origin = new Point3d(o);
    }

    public Point3d getOrigin() {
        return origin;
    }
    public double getAlpha() {
        return alpha;
    }
    public double getTheta() {
        return theta;
    }
    public double getD() {
        return d;
    }
    public double getR() {
        return r;
    }
    
    /**
     * Transformation from local coordinates in the current coordinate system into
     * the local coordinaten of the coordinate system before.
     * 
     * @return Denavit Hartenberg matrix
     */
    public Matrix4d toMatrix4d() {
        return dh2M(theta, alpha, d, r);
    }
    private static Matrix4d dh2M(double theta, double alpha, double d, double r) {
        Matrix4d result = new Matrix4d(cos(theta), -cos(alpha) * sin(theta), sin(alpha) * sin(theta), r * cos(theta),
                sin(theta), cos(alpha) * cos(theta), -sin(alpha) * cos(theta), sin(theta) * r,
                0, sin(alpha), cos(alpha), d,
                0d, 0d, 0d, 1d);
        return result;
    }
    public Matrix4d toMatrix4dInverse() {
        return dh2MInverse(theta, alpha, d, r);
    }
    private static Matrix4d dh2MInverse(double theta, double alpha, double d, double r) {
        Matrix4d result = new Matrix4d(cos(theta), sin(theta), 0.0d, -r,
                -cos(alpha) * sin(theta), cos(alpha) * cos(theta), sin(alpha), -sin(alpha) * d,
                sin(alpha) * sin(theta), -cos(theta)*sin(alpha), cos(alpha), -d *cos(alpha),
                0d, 0d, 0d, 1d);
        return result;
    }
    
    /**
     * Determine Denavit Hartenberg parameters from revolute joint axes based on
     * Plücker coordinates and dual vector algebra.
     *
     * @param z sequence of revolute joint axis directions, z[0] corresponds to
     * the base coordinate system (link/joint 0), z[0] zeigt beim UR nach oben
     * und c[0] ist der Ursprung o[0], z[1] corresponds to the seconds joint
     * axis, beim UR ist z[6] das CS des TCP, dessen z-Achse zeigt in die
     * gleiche Richtung wie z[5]
     * @param x0 Direction of the x-axis of the base coordinate system (because there
     * is no precending z-Achse which allows to define xStatic as upright to this)
     * @param c sequence of points laying on the corresonding revolute joint
     * axes, c[0] is the origin of the base system c[i] with i>0 are arbitray
     * points on the joint axes
     * @param xStatic Direction of the x-axes in neutral pose - used to determine
     * delta-thetas 
     * @param lastd last parameter d
     * @param signR if set to true, alpha, theta angeles are calculated based on a flipped x-axis
     *              at the moment not used
     * @param unit of the input coordinates
     * @return array of Denavit Hartenberg objects containing the corresponding
     * kinematic parameters. array length is the length of the given arrays + 1.
     * The unit of the angles is [deg], the distances units corresponding to the input dimensions.
     * @throws IllegalArgumentException if some components are NaN, null or
     * 0,0,0, or if array distance of z != array distance of c
     * 
     * TODO
     * - Wenn aufeinanderfolgende Achsen ungefähr rechtwinklich aufeinander stehen, ist
     *   a ungefähr 0. Wenn a exakt 0 ist, dann funktioniert die Berechnung derzeit nicht. 
     *   Es muss dann die x-Achse über das Kreuzprodukt bestimmt werden
     * - wenn aufeinanderfolgende Achse exakt parallel sind, dann sind O1,P1 nicht definiert
     *   über den kürzesten Abstand, dann sollten die Projektion von O1 der vorherigen Achse
     *   auf die nachfolgende parallele Achse verwendet werden
     * --> Test durch Verwendung der nominalen DH-Parameter des UR5e
     */
    /*public static DH[] determineDH(Vector3d[] z, Vector3d x0, Point3d[] c, 
            Vector3d[] xStatic, double lastd, boolean[] signR, String unit) {

        if (z.length != c.length) {
            throw new IllegalArgumentException("c.length must be z.length");
        }
        if (z.length != xStatic.length) {
            throw new IllegalArgumentException("xStatic.length must be z.length");
        }
        if (signR.length != z.length) {
            throw new IllegalArgumentException("signR.length must be z.length");
        }
        
        // hier bekomme ich bereits 7 z-Achsen, d.h. die letzte ist bereits gedoppelt
        //System.out.println("DH.determineDH length = "+String.valueOf(z.length));
        DH[] result = new DH[z.length];

        Point3d O0 = new Point3d(c[0]);
        
        // Basis Koordinatensystem
        // FIXME d=0 ist hier eigentlich falsch, kann es zu diesem Zeitpunkt 
        // aber auch gar nicht wissen
        result[0] = new DH(0d, 0d, 0d, 0d, O0);
           
        if (debug){
            System.out.println();
            System.out.println("Determine DH-parameters based on DualVectors:");
            System.out.println("--------------------------------------------");

            System.out.println("0: theta=0°, alpha=0°, r=0"+unit+", d=0"+unit+", ");
            System.out.println("O0=(" + String.valueOf(O0.x) + ","
                        + String.valueOf(O0.y) + "," + String.valueOf(O0.z) + ")");
        }
        
        if (signR[0]){
            x0.scale(-1d);
            if (debug){
                System.out.println("sign flipped for i="+String.valueOf(0)+"!");
            }
        }
        
        // - bis zur vorletzten Achse hier iterieren, denn innerhalb der for-Schleife wird [i+1] benötigt
        // - die letzte Achse auch weglassen, da diese in die gleiche Richtung zeigt wie die vorletzte
        //   und auch r==0, alpha==0 ist. Da gibts keinen dualAngle() zu bestimmen!
        for (int i = 0; i < z.length - 1 - 1; i++) {
            
            if (debug){
                System.out.println("x0=(" + String.valueOf(x0.x) + ","
                        + String.valueOf(x0.y) + "," + String.valueOf(x0.z) + ")");
            }
            
            // Wenn eine der beiden Achsen durch den Ursprung geht, werden beide im Raum verschoben
            // und später müssen bestimmte Schnittpunkte/Aufpunkte wieder zurück verschoben werden
            Vector3d t = new Vector3d();
            
            DualVector3d[] s12 = DualVector3d.createRays(t, c[i], z[i], c[i + 1], z[i + 1]); 
            
            //DualNumber dualAngle = s12[0].dualAngle(s12[1]);

            // testweise
            //TODO
            //Point3d[] p = s12[0].intersect2(s12[1]); // stürzt ab
            // O1=(-0.9998429046139186,8.273001345220623E-8,0.16242364179549842)
            // P0=(0.0,0.0,1.1624236417954983) --> P0.z ist um 1 zu gross
            //Point3d[] p = s12[0].intersect(s12[1]); 
            // O1=(-0.9998429046139187,8.273001345221183E-8,0.16242364179549842)
            //P0=(8.274298213398743E-8,8.274298213398743E-8,0.16242364179549842)
            // --> O0.x ist um 1 zu gross
            Point3d[] p = s12[0].intersect(s12[1], null, null); 
            Point3d P0 = p[0];
            P0.add(t);
            Point3d O1 = p[1];
            O1.add(t);
            
            if (debug){
                //System.out.println("t="+String.valueOf(t.x)+","+String.valueOf(t.y)+","+String.valueOf(t.z));
                System.out.println("O"+String.valueOf(i+1)+"=(" + String.valueOf(O1.x) + ","
                        + String.valueOf(O1.y) + "," + String.valueOf(O1.z) + ")");
                //System.out.println("O"+String.valueOf(i+1)+"=(" + String.valueOf(O2.x) + ","
                //        + String.valueOf(O2.y) + "," + String.valueOf(O2.z) + ")");
                System.out.println("P"+String.valueOf(i)+"=("
                        + String.valueOf(P0.x) + "," + String.valueOf(P0.y) + ","
                        + String.valueOf(P0.z) + ")");
            }
            
            // aktuelle x-Ausrichtung
            Vector3d x = new Vector3d(O1);
            x.sub(P0); // p vorher
            
            // dieses r ist jetzt korrekt für alle i
            double r =  Utils.ddist(P0, O1, xStatic[i]); // dualAngle.dual(); // kann falsches Vorzeichen haben wirkt sich dann auf alpha und theta aus
            if (r<0){
                if (debug){
                    System.out.println("x-axis flipped for i="+String.valueOf(i)+"!");
                }
                x.scale(-1d);
            }
            // signR verwenden um dann das Vorzeichen zu switchen für 1-2
            // damit funktioniert es auch
            
            double alpha = Utils.angle(s12[0].real(), s12[1].real(), x) *180d/Math.PI;
            //Utils.projAngle(s12[0].real(), s12[1].real(), xOrig, false);  //dualAngle.real() * 180d / Math.PI;
            
            // theta along the previous z-axis from the old to the new x
            // theta = Drehung um die erste z-Achse, x0
            // theta entspricht den delta-Thetas wenn ich die Herstellerkalibration reinstecke
            double theta = Utils.projAngle(x0, x, s12[0].real(), true); // bezogen auf vorherige Ausrichtung
            // sollte 0 sein für den statischen Kalibrations-Trial, !=0 für kirkdefault
            double thetaReal = Utils.projAngle(xStatic[i], x, s12[0].real(), true); // bezogen auf Neutral-Ausrichtung (static)
            double deltaTheta = theta - thetaReal; // Drehfehler bei kirkdefault
            
            //double theta, double alpha, double d, double r, Point3d o
            result[i+1] = new DH(theta, alpha, Utils.ddist(O0, P0, s12[0].real()), r, O1);

            if (debug){
                System.out.println(String.valueOf(i+1)+": theta="+String.valueOf(theta)+"°, alpha=" + 
                        String.valueOf(alpha)+"°, r=" + String.valueOf(r)+unit+
                        ", d="+String.valueOf(Utils.ddist(O0, P0, s12[0].real()))+unit+"!");
                
                // für Winkel ca. +x90grad ungefähr gleich
                // für kleine Winkel, fast parallele Geraden ist r2 falsch
                //double r1 = s12[0].ddist(s12[1]); // teilweise falsche Vorzeichen 
                //double r2 = s12[0].dist(s12[1]); // bei alpha=0, komplett falsch und falsche Vorzeichen bei den anderen
                //System.out.println("r1="+ String.valueOf(r1) + unit+", r2=" + String.valueOf(r2)+ " "+unit);
            }
            
            O0 = O1;
            x0 = x;
            
        }

        // TCP coordinate system
        
        O0 = new Point3d(O0);
        Vector3d lastZVec = z[z.length - 1];
        lastZVec.normalize();
        lastZVec.scale(lastd);
        O0.add(lastZVec);
        
        // double theta, double alpha, double d, double r, Point3d o
        result[result.length - 1] = new DH(0d, 0d, lastd, 0d, O0);

        if (debug){
            System.out.println(String.valueOf(result.length - 1) + ": theta="+String.valueOf(0)+"°, alpha=" + String.valueOf(0d)
                    + "°, r=" + String.valueOf(0d) + unit+", d="+String.valueOf(lastd)+unit+"!");
            System.out.println("O"+String.valueOf(result.length - 1)+"=(" + String.valueOf(O0.x) + ","
                        + String.valueOf(O0.y) + "," + String.valueOf(O0.z) + ")");
        }
        return result;
    }*/

    
    //--------------------------------
    
    /**
     * Build base coordinate system from z-direction and origin.
     *
     * @param z z-axis direction
     * @param x x-axis direction
     * @param o origin
     * @return coordinate system (if x=(NaN,NaN,NaN) than x,y of the result
     * coordinate system is (NaN, NaN
     * @Deprecated used only from the deprecated method revolute()
     */
    private static Matrix4d buildCoordinateSystem(Vector3d z, Vector3d x, Point3d o) {
        Matrix4d result = new Matrix4d();

        result.m03 = o.x;
        result.m13 = o.y;
        result.m23 = o.z;
        //result.setTranslation(new Vector3d(o));

        Matrix3d rot0 = new Matrix3d();
        //rot0.setColumn(0,new Vector3d(1d,0d,0d));
        rot0.setColumn(0, new Vector3d(x));
        Vector3d y = new Vector3d();
        y.cross(z, x);
        rot0.setColumn(1, y);
        rot0.setColumn(2, z);
        result.setRotationScale(rot0);
        return result;
    }

    /**
     * Determine a coordinate system corresponding to the Denavit Hartenberg
     * convention based on the two given straigt lines.
     *
     * This coordinate system is at the distal end of the link, the second axis
     * defines the z-axis of this CS and the origin of this CS is on this
     * axis.<p>
     *
     * @param s1 first straigt line
     * @param s2 seconds straigt line
     * @param c1 point on s1, only needed if s1 parallel or identical to s2
     * @param c2 point on s2, only needed if s1 parallel or identical to s2
     * @return Coordinate axes and origin in global coordinates. If s1==s2 then
     * x2=y2=(NaN,NaN,NaN) and origin
     *
     * @Deprecated die Matrix4d als Rückgabewert erscheint mir mittlerweile
     * nicht mehr als praktisch
     */
    /*private static Matrix4d revolute(DualVector3d s1, DualVector3d s2, Point3d c1, Point3d c2) {
        Point3d[] p = s1.intersect(s2, c1, c2);
        Point3d p2;
        Vector3d x2 = new Vector3d();
        // Gelenkachsen schneiden sich oder sind identisch
        if (p.length == 1) {
            // Gelenkachsen identisch z.B. TCP, letztes Koordinatensystem
            if (s1.real().x == s2.real().x && s1.real().y == s2.real().y && s1.real().z == s2.real().z) {
                x2.x = Double.NaN;
                x2.y = Double.NaN;
                x2.z = Double.NaN;
            } else {
                x2.cross(s1.real(), s2.real());
            }
            p2 = p[0];
            System.out.println("Intersection or identical");
            // Gelenkachsen parallel oder verschraubt
        } else {
            x2 = new Vector3d(p[1]);
            x2.sub(new Vector3d(p[0]));
            x2.normalize();
            p2 = p[1];
            System.out.println("Parallel oder verschraubt");
        }

        return buildCoordinateSystem(s2.real(), x2, p2);
    }*/
    
    /**
     * z,x sind die Richtungen der DH-Systeme, werden aus den Rotationsmatrizen
     * rausgeholt. Der Translationsanteil enspricht den jeweiligen absoluten
     * Koordinaten des Ursprungs DH-Koordinatensystemen auf den z-Achsen.<p>
     *
     * TODO unklar ob diese Def der Matrix4d-Objekte der üblichen Repräsentation
     * eines lokalen Koordinatensystems entspricht.<p>
     *
     * Die 3x3-Rotationsmatrix soll ja eine dir-cos-matrix sein, d.h. soll einen
     * Vektor im Koordinatensystem das die Matrix repräsentiert also z.B. die
     * z-Achse = (0,0,1) in das Basissystem transformieren. Damit das so ist mut
     * die 3. Spalte der Matrix gerade die z-Achse in Koordinaten des
     * Referenzsystems enthalten. Das ist also soweit korrekt wie ich das
     * verwende.<p>
     *
     * Unklar bleibt jetzt noch der translatorische Teil!!!<p>
     *
     * @param dh1
     * @param dh2
     * @param x directions in neutral-pose, used to determine delta-thetas
     * @Deprecated
     */
    public DH(Matrix4d dh1, Matrix4d dh2, Vector3d x) {

        // rotation 1
        Matrix3d rot1 = new Matrix3d();
        dh1.get(rot1);
        Vector3d x1 = new Vector3d();
        rot1.getColumn(0, x1);
        x1.normalize();
        Vector3d z1 = new Vector3d();
        rot1.getColumn(2, z1);
        z1.normalize();

        // translation 1
        Vector3d o1 = new Vector3d();
        dh1.get(o1);

        // rotation 2
        Matrix3d rot2 = new Matrix3d();
        dh2.getRotationScale(rot2);
        Vector3d x2 = new Vector3d();
        rot2.getColumn(0, x2);
        x2.normalize();
        Vector3d z2 = new Vector3d();
        rot2.getColumn(2, z2);
        z2.normalize();

        // translation 2
        Vector3d o2 = new Vector3d();
        dh2.get(o2);
        Vector3d o1o2 = new Vector3d(o2);
        o1o2.sub(o1);

        // ist das richtig? oder umgekehrt?
        //FIXME
        origin = new Point3d(o2);
        
        //scheint gar nicht zu stimmen
        //this.theta = angle(x1,x2,z1);
        Vector3d temp = new Vector3d();
        temp.cross(x1, x2);
        this.theta = Math.atan2(temp.dot(z1), x1.dot(x2));

        //TODO
        // irgendwie muss man doch die x-Richtungen die durch die Impulsgeber des robots
        // bestimmt werden und in der Neutralstellung zu 0deg führen als Referenz
        // verwenden um dazu theta zu bestimmen bzw. diese delta-thetas noch aufzuaddieren?
        //FIXME stimmt nur so ungefähr
        //this.alpha = angle(z1,z2,x2);
        temp.cross(z1, z2);
        this.alpha = Math.atan2(temp.dot(x2), z1.dot(z2));

        // scheint zu stimmen
        //System.out.println("z1=("+String.valueOf(z1.x)+","+String.valueOf(z1.y)+","+String.valueOf(z1.z)+")");
        //System.out.println("z2=("+String.valueOf(z2.x)+","+String.valueOf(z2.y)+","+String.valueOf(z2.z)+")");
        System.out.println("x2=(" + String.valueOf(x2.x) + "," + String.valueOf(x2.y) + "," + String.valueOf(x2.z) + ")");

        this.d = o1o2.dot(z1);
        this.r = o1o2.dot(x1);
    }
}
