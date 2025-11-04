package de.dhbw.rahmlab.dsl4ga.euclidview3d.utils;

import de.orat.math.cga.spi.iCGAMultivector;
import de.orat.math.gacalc.api.MultivectorValue; /*Numeric*/
import org.jogamp.vecmath.Point3d;

/**
 * A k-Vector is a multivector which is a linear combination of the 32 basis blades 
 * (in form of a linear combination) of the same grade k (0..5). 
 * 
 * k-blades are k-vectors but not all k-vectors are k-blades (sometimes shorter 
 * called blades).<p>
 * 
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GAKVector extends CGAMultivector implements iCGAkVector {
    
    public GAKVector(CGAMultivector m){
        super(m.compress().impl);
        try {
            testGrade();
        } catch (IllegalArgumentException e){
            System.out.println("Composition of the k-vector is failed: "+m.toString("mv"));
            throw(e);
        }
    }
    
    /**
     * Create a specialzed GAKVector from a given multivector.
     * 
     * @param m k-vector
     * @param isIPNS true, an ipns multivector is created
     * @return null, if the given Multivector is not recognized as a specialization
 of GAKVector
     */
    public static GAKVector specialize(/*CGAMultivector m*/ MultivectorValue mv, boolean isIPNS){
        
        //TODO
        // wie kann ich herausfinden ob m eine subclass von GAKVector ist und damit
        // gar kein neues objekt erzeugt werden muss?
        
        //TODO
        // alle Klassen durchtesten
        
        switch (m.grade()){
            case 0:
                // opns scalar
                return new CGAScalarOPNS(m);
                
            case 1:
                // ipns plane? (flat)
                if (CGAFlatIPNS.is(m)){
                    return new CGAPlaneIPNS(m);
                } 
                if (CGARoundIPNS.is(m)){
                    //TODO besser statische Methode CGARound.squaredSize(CGAMultivector) verwenden
                    CGASphereIPNS sphere = new CGASphereIPNS(m);
                    // ipns round point (round)
                    if (Math.abs(sphere.squaredSize()) < eps){
                        return new CGARoundPointIPNS(m);
                    // ipns sphere (round)
                    } else {
                        return sphere;
                    }
                }
                // opns attitude scalar (attitude?)
                if (isIPNS && CGAAttitudeIPNS.is(m)){
                   //FIXME
                   // mit ganja lande ich vermutlich hier wenn ich ein sphereIPNS als
                   // m übergebe. Warum?
                   return new CGAAttitudeIPNS(m);
                // ipns attitude trivector? (attitude)
                } else if (CGAAttitudeOPNS.is(m)){
                   return new CGAAttitudeTrivectorOPNS(m);
                }
                System.out.println("Illegal grade 1 object found: "+m.toString());
                break;
                
            case 2:
                if (isIPNS){
                    if (CGAFlatIPNS.is(m)){
                        // ipns screw axis? (flat)
                        CGAScrewAxisIPNS screwAxis = new CGAScrewAxisIPNS(m);
                        // ipns line (flat)
                        //TODO getPitch() muss noch implementiert werden
                        if (screwAxis.getPitch() < eps){
                            return new CGALineIPNS(m);
                        } else {
                            return screwAxis;
                        }
                    }
                    if (CGARoundIPNS.is(m)){
                        CGACircleIPNS circle = new CGACircleIPNS(m);
                        // ipns oriented point (round)
                        if (circle.squaredSize() < eps){
                            return new CGAOrientedPointIPNS(m);
                        } else {
                            // ipns circle (round)
                            return circle;
                        }
                    }
                    // ipns attitude bivector (attitude)
                    if (CGAAttitudeIPNS.is(m)){
                        return new CGAAttitudeBivectorIPNS(m);
                    // ipns tangent bivector? (tangent)
                    } else if (CGATangentIPNS.is(m)){
                        return new CGATangentBivectorIPNS(m);
                    }
                    System.out.println("Illegal ipns object of grade 2 found: "+m.toString());
                    
                // opns
                } else {
                    // opns flat point (flat)
                    if (CGAFlatOPNS.is(m)){
                        return new CGAFlatPointOPNS(m);
                    // opns point pair (round)
                    } else if (CGARoundOPNS.is(m)){
                        return new CGAPointPairOPNS(m);
                    }
                    System.out.println("Illegal opns object of grade 2 found: "+m.toString());
                }
                break;
                
            case 3:
                // opns
                if (!isIPNS){
                    if (CGAFlatOPNS.is(m)){
                        // opns line (flat)
                        // opns screw axis? (flat)
                        //TODO CGAScrewAxisOPNS muss noch implementiert werden
                        //CGAScrewAxisOPNS screwAxis = new CGAScrewAxisOPNS(m);
                        //if screwAxis.getPitch(9 < eps){}
                        return new CGALineOPNS(m);
                    }
                    if (CGARoundOPNS.is(m)){
                        CGACircleOPNS circle = new CGACircleOPNS(m);
                        // opns oriented point (round)
                        if (circle.squaredSize() < eps){
                            return new CGAOrientedPointOPNS(m);
                        // opns circle (round)
                        } else {
                            return circle;
                        }
                    }
                    
                    // opns attitude bivector (attitude)
                    if (CGAAttitudeOPNS.is(m)){
                        return new CGAAttitudeBivectorOPNS(m);
                    // opns tangent bivector (identisch mit ipns flat point) (tangent)
                    } else if (CGATangentOPNS.is(m)){
                        return new CGATangentBivectorOPNS(m);
                    }
                    System.out.println("Illegal opns object of grade 3 found:"+m.toString());
                    
                // ipns
                } else {
                    // ipns flat point (flat)
                    if (CGAFlatIPNS.is(m)){
                        return new CGAFlatPointIPNS(m);
                    // ipns point pair (round)
                    } else if (CGARoundIPNS.is(m)){
                        return new CGAPointPairIPNS(m);
                    }
                    System.out.println("Illegal ipns object of grade 3 found:"+m.toString());
                }
                break;
                
            case 4:
                if (isIPNS){
                    // ipns attitude scalar (attitude)
                    if (CGAAttitudeIPNS.is(m)){
                        return new CGAAttitudeIPNS(m);
                    }
                    System.out.println("Illegal ipns object of grade 4 found:"+m.toString());
                    
                // opns
                } else {
                    // opns plane (flat)
                    if (CGAFlatOPNS.is(m)){
                        return new CGAPlaneOPNS(m);
                    // opns round point (round)
                    } else if (CGARoundOPNS.is(m)){
                        return new CGARoundPointOPNS(m);
                    // opns attitude trivector? (attitude)
                    } else if (CGAAttitudeOPNS.is(m)){
                        return new CGAAttitudeTrivectorOPNS(m);
                    }
                    System.out.println("Illegal opns object of grade 4 found:"+m.toString());
                }
                break;
                
            case 5:
                // ipns scalar
                return new CGAScalarIPNS(m);
            default:
                System.out.println("Illegal object of unknown grade found:"+m.toString());
        }
        return null;
    }
    
    /**
     * @param impl 
     * @throws IllegalArgumentException if the grade of the given argument is not correct
     */
    GAKVector(iCGAMultivector impl){
        super(impl);
        try {
            testGrade();
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
            throw(e);
        }
    }
    public GAKVector(double[] values){
        super(values);
        try {
            testGrade();
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage()+" "+impl.toString());
            throw(e);
        }
    }
    
    public static GAKVector create(double[] values, boolean isIPNS){
        if (values.length != 32) throw new IllegalArgumentException("double[] has not the length 32 but \""+
                String.valueOf(values.length+"\"!"));
        GAKVector m = new GAKVector(values);
        if (isIPNS){
            if (CGARoundIPNS.typeof(m)){
                //TODO
                // test ob circle, point, ...
                return new CGAOrientedPointIPNS(m);
            } 
        } else {
             if (CGARoundIPNS.typeof(m)){
                //TODO
                // test ob circle, point, ...
                return new CGAOrientedPointOPNS(m);
            } 
        }
        System.out.println("Subtype of \""+m.toString("")+"\" not detected!");
        return m;

	// specialize beseitigt, code kommt in die Implementierung von mv
    
    /**
     * Determine direction/attitude from tangent or round objects in OPNS 
     * representation.
     * 
     * following [Dorst2009] p.407<p>
     * 
     * @return attitude
     */
    CGAAttitudeOPNS attitudeFromTangentAndRoundIPNS(){
        System.out.println(toString("CGAkVector attitudeFromTangentAndRoundIPNS"));
        CGAAttitudeOPNS result = new CGAAttitudeOPNS(inf.negate().lc(undual()).op(inf).compress());
        System.out.println(result.toString("CGAkVector attitudeIPNS (round/tangent)"));
        return result;
    }
    CGAAttitudeOPNS attitudeFromTangentAndRoundOPNS(){
        CGAAttitudeOPNS result = new CGAAttitudeOPNS(inf.lc(this).negate().op(inf).compress());
        System.out.println(result.toString("CGAkVector attitudeOPNS (round/tangent)"));
        return result;
    }
    /**
     * Determine direction/attitude from tangent or round objects.
     * 
     * [Hildenbrand2004]
     * 
     * @return direction/attitude
     */
    /*protected CGAMultivector attitudeFromTangentAndRound(){
        // see errata, dual tangend/round formula Dorst2007
        CGAMultivector einf = CGAMultivector.createInf(1d);
        CGAMultivector einfM = CGAMultivector.createInf(-1d);
        CGAMultivector result = einfM.ip(dual()).op(einf);
        System.out.println("attitude(round/attitude)="+result.toString());
        return result;
    }*/
    /*protected CGAMultivector attitudeFromDualTangentAndDualRound(){
        // see errata, dual tangend/round formula Dorst2007
        CGAMultivector einf = CGAMultivector.createInf(1d);
        CGAMultivector result = (einf.ip(this)).gp(-1d).op(einf);
        System.out.println("attitude(round/attitude)="+result.toString());
        return result;
    }*/
    
    
    /**
     * Determine the location of the geometric object, which is represented by
     * the k-Vector.For a flat object this is defined by the perpendicular 
     * distance vector of the origin to the carrier plane. 
     * 
     *
     * @param p 
     * @return location
     */
    /*@Override
    public Point3d location(){
        CGAMultivector result = carrierFlat().inverse().gp(this.op((new CGAScalar(1d).add(I0)))).rc(I0);
        return result.extractE3ToPoint3d();
    }*/
    
    // plane_through_point_tangent_to_x  = (point,x)=>point^ni<<x*point^ni;
    public CGARoundPointIPNS reject(CGARoundPointIPNS p){
        return new CGARoundPointIPNS(p.op(inf).lc(this).gp(p.op(inf)));
    }
    
    @Override
    public GAKVector undual(){
        return new GAKVector(super.undual().compress()); // or impl.dual().gp(-1));
    }
    @Override
    public GAKVector dual(){
        return new GAKVector(impl.dual().getCompressed());
    }
    
    /**
     * Create a unit k-vector respectively a unit orientation of the k-vector space.
     * 
     * If a unit orientation Ik has been given for the k-D vectorspace of Ek, 
     * and Ek = α Ik, then α is the weight of Ek, and the orientation of X is 
     * sign(α) Ik.
     * If k=0, the standard orientation is 1, and if k=n, the standard orientation 
     * is In. These are invariant under rigid body motions; for the other k-values 
     * the orientations are still invariant under translations. 
     * 
     * TODO
     * The unit orientations for k=1,...4 hängen vom konkreten k-vector ab?
     * Wie kann ich das dann implementieren?
     * Habe ich hier nicht einen Freiheitsgrad wie ich die Standard-Orientierung
     * eines k-vectors für k=1,...4 festlegen kann?
     * 
     */
    /*private GAKVector createUnitOrientation(){
        switch (grade()){
            case 0:
                return new CGAScalarOPNS(1);
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return createI();
        }
    }
    
    Note also that unit weight and unit norm are different concepts; for the 
    point representation e0+p has weight 1, but norm √(e02+p2) 
    private double weight(){
    
    }*/
    
    
    /**
     * Determines the squared weight (without sign) based on the attitude and 
     * the origin as probe point.
     * 
     * @return squared weight is always >0
     */
    public double squaredWeight(){
        return squaredWeight(new Point3d(0d,0d,0d));
    }
    /**
     * Determines the squared weight (without sign) based on the attitude.
     * 
     * @param probePoint
     * @return squared weight > 0
     */
    public double squaredWeight(Point3d probePoint){
        // probePoint(0,0,0)=1.0*eo
        // System.out.println("probePoint(0,0,0)="+probePointCGA.toString());
        return squaredWeight(attitudeIntern(), new CGARoundPointIPNS(probePoint));
    } 
    
    /**
     * Determine the squared weight (without sign) of any CGA object.
     * 
     * @param attitude direction specific for the object form the multivector is representing
     * @param probePoint If not specified use e0.
     * @return squared weight >0
     */
    protected static double squaredWeight(CGAMultivector attitude, CGARoundPointIPNS probePoint){
        //FIXME abs() scheint mir unnötig zu sein
        return Math.abs(probePoint.lc(attitude).sqr().decomposeScalar());
        // liefert gleiches Ergebnis
        // CGAMultivector A = probePoint.ip(attitude);
        //return A.reverse().gp(A).decomposeScalar();
    }
    
    /**
     * Determine the attitude/direction as (I inf). 
     * 
     * TODO
     * kann ich das nicht abstract machen?
     * 
     * @return 
     */
    protected CGAAttitudeOPNS /*CGAMultivector*/ attitudeIntern(){
        throw new RuntimeException("Not implemented. Available for derivative classes only!");
    }
    
    /**
     * Determination of the location of the corresponding geometric object, if 
     * available.
     * 
     * TODO
     * kann ich das nicht abstract machen?
     * 
     * @param probe
     * @return location
     * @throws RuntimeException if location is not available
     */
    public Point3d location(Point3d probe){
        throw new RuntimeException("Available for most of the derivative classes only!");
    }
    /**
     * Determination of location of the corresponding geometric object, based 
     * on default euclidiean probe point at (0,0,0).
     * 
     * @return location based on probe point = (0,0,0)
     * @throws java.lang.ArithmeticException if the k-vector is not invertible.
     */
    public Point3d location(){
        return location(new Point3d(0d,0d,0d));
        //throw new RuntimeException("location in Multivector should not be invoked!");
    }
    public CGARoundPointIPNS locationIntern(){
        return new CGARoundPointIPNS(location());
    }
    
    
    
    
    /**
     * Determines location from tangent (direct/dual) and round (direct/dual) 
     * objects.
     * 
     * scheint für CGAOrientedFiniteRoundOPNS um einen faktor 2 falsch zu sein in allen Koordinaten
     * scheint für CGARound zu stimmen
     * scheint mit CGATangent nicht zu stimmen??? mittlerweile korrigiert?
     * scheint mit CGAOrientedPointPair zu stimmen
     * vielleicht muss das object vorher normalisiert werden
     * TODO
     * 
     * @return location represented by a normalized sphere/finite point (dual sphere corresponding to Dorst2007)
     */
    protected CGARoundPointIPNS locationFromTangentAndRoundAsNormalizedSphere(){
        // corresponds to the errata of the book [Dorst2007]
        // and also Fernandes2009 supplementary material B
        // location as finite point/dual sphere corresponding to [Dorst2007]
        // createInf(-1d).ip(this) ist die Wichtung, es wird also durch die Wichtung geteilt,
        // d.h. der Punkt wird normiert?
        System.out.println(this.toString("locationFromTangentOrRoundAsNormalizedSphere(CGAKVector)"));
        // circleipns:
        // tangentOrRound = (-0.24011911*eo^e1 + 0.39999987*eo^e2 + 0.3904*eo^e3 - 
        // 0.35230035*eo^ei - 0.04229702*e1^ei + 0.07046005*e2^ei + 0.06876903*e3^ei)

        // CGARoundPointIPNS schlägt fehl mit:
        // The given multivector is no blade: 1.0000000000000002*eo + 0.2285897790690278*e1 
        // - 0.3807938564778948*e2 - 0.371654924710276*e3 + 0.159234453488245*ei - 5.391337279392339E-9*e1^e2^ei 
        // - 4.515812467020819E-9*e1^e3^ei - 1.24294008418957E-9*e2^e3^ei

        // FIXME unklar, ob Normierung notwendig ist
        CGAMultivector mn = this; //this.normalize();
        
        // The given multivector is no k-vector: 1.0000000000000002*eo + 0.220516482273777*e1 + 
        // 0.36734504072736274*e2 + 0.1835667072335959*e3 - 
        // 0.3445510478954946*e1^e2^e3 + 0.18137731261233903*ei - 0.28195912872783596*e1^e2^ei + 0.253138237443652*e1^e3^ei - 0.15195837009131624*e2^e3^ei
        // schlägt fehlt bei imaginary point-pair == ipns circle. da rountpointipns keine 3-er-Komponenten enthalten darf
        CGAMultivector m = mn.negate().div(inf.lc(mn));
        try {
            CGARoundPointIPNS result = new CGARoundPointIPNS(m.compress());
            // z.B. locationFromTangentAndRound=eo + 0.02*e1 + 0.02*e2 + e3 + 0.5*ei
            // bei input von p=(0.02,0.02,1.0), funktioniert, aber vermutlich nur,
            // da hier die Wichtung bei e0==1 ist.
            // locationFromTangentAndRound=2.0*eo + 0.5000000000000001*e2 - 0.5*ei
            // hiermit funktioniert es nicht mehr
            System.out.println("locationFromTangentAndRound="+result.toString());

            // center of this round, as a null vector
            // https://github.com/pygae/clifford/blob/master/clifford/cga.py Zeile 284:
            // self.mv * self.cga.einf * self.mv // * bedeutet geometrisches Produkt
            //TODO ausprobieren?

            // euclidean part rausziehen, scheint zu funktionieren
            //CGAMultivector o = CGAMultivector.createOrigin(1d);
            CGAMultivector resultEuclidean = o.op(inf).ip(o.op(inf).op(result));
            // location (decomposed) euclidean only = (0.4999999999999998*e2)
            // FIXME nur halb so gross wie ursprünglich
            // bei circleipns ==0 FIXME
            System.out.println(resultEuclidean.toString("location (decomposed) euclidean only"));
            return result;
        } catch (IllegalArgumentException e){
            System.out.println("grade error :"+m.toString("roundPointIPNS"));
            throw e;
        }
    }
    /**
     * Determines location from tangend and round objects and also from its dual.
     * 
     * @return location in the euclidian part directly.
     */
    protected CGAMultivector locationFromTangendAndRound(){
        // corresponds to the errata of the book Dorst2007
        CGAMultivector result = (this.gp(inf).gp(this)).div((inf.ip(this)).sqr()).gp(-0.5d);
        System.out.println("locationFromTangentAndRound2="+result.toString());
        return result;
    }
 
    
    // coordinates extraction
   
    // um die Attitude zu bestimmen, kann die Methode in CGAMultivector verwendet werden
    // um aus Eeinf die attitude rauszuholen
    
    /**
     * Extract attitude/direction from Bivector^einf multivector representation.
     * 
     * example: -1.9999999999999991*e1^e2^ei + 1.9999999999999991*e1^e3^ei + 1.9999999999999991*e2^e3^ei
     *
     * @return direction/attitude
     */
    /*protected Vector3d extractAttitudeFromBivectorEinfRepresentation(){
        
        //indizes hängen von der impl ab
        //double[] coordinates = impl.extractCoordinates(3);
        //Vector3d v = new Vector3d(coordinates[9], coordinates[8], coordinates[7]);
              
        CGAMultivector m = extractGrade(3).rc(o).negate().lc(I3i); //euclideanDual(); //extractE3ToVector3d();
        //System.out.println("###"+m.toString("extractAttFromBiVecEinf")+" "+toString("orig")+
        //        " vec=("+String.valueOf(v.x)+","+String.valueOf(v.y)+","+String.valueOf(v.z)+")");
        return m.extractE3ToVector3d();
    }*/
    
    
    
    public iCGATangentOrRound.EuclideanParameters decomposeTangentOrRound(){
        if (this instanceof iCGATangentOrRound tangentOrRound){
            return tangentOrRound.decompose();
        }
        throw new RuntimeException("CGA Multivector is not of type iCGATangentOrRound");
    }
    
}
