package de.dhbw.rahmlab.dsl4ga.euclidview3d.utils;

//import de.orat.math.cga.api.CGAScrew.MotorParameters;
import de.orat.math.gacalc.api.MultivectorValue; /*Numeric;*/
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import de.orat.view3d.euclid3dviewapi.api.ViewerService;
import de.orat.view3d.euclid3dviewapi.spi.iAABB;
import de.orat.view3d.euclid3dviewapi.spi.iEuclidViewer3D;
import de.orat.view3d.euclid3dviewapi.util.Line;
import de.orat.view3d.euclid3dviewapi.util.Plane;
import java.awt.Color;
import java.util.Optional;
import org.jogamp.vecmath.Matrix4d;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Tuple3d;
import org.jogamp.vecmath.Vector3d;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GAViewer extends GAViewObject {
    
    public static Color COLOR_GRADE_1 = Color.RED;    // ipns: sphere, planes, round points
    public static Color COLOR_GRADE_2 = Color.GREEN;  // ipns: lines, ipns circle, oriented points; opns: flat-points, point-pairs
    public static Color COLOR_GRADE_3 = Color.BLUE;   // ipns: point-pairs, (tangent vector), flat-points; opns:circle, line, oriented-points
    public static Color COLOR_GRADE_4 = Color.YELLOW; // (ipns euclidean vector), opns: plane, round-point, spheres

    //TODO
    // nur als Faktoren verwenden und skalieren auf Basis des angezeigten Volumens
    public static float POINT_RADIUS = 0.01f; // in m
    public static float LINE_RADIUS = 0.005f; // in m
    public static float TANGENT_LENGTH = 0.1f*3f; // testweise *3 damit trotz Roboter sichtbar

    private final iEuclidViewer3D impl;
   
    public static Optional<GAViewer> getInstance(){
         Optional<iEuclidViewer3D> viewer = ViewerService.getInstance().getViewer();
         GAViewer cgaViewer = null;
         if (viewer.isPresent()){
             try {
                cgaViewer = new GAViewer(viewer.get());
             } catch (Exception ex){
                cgaViewer = null;
                ex.printStackTrace();
             }
         }
         return Optional.ofNullable(cgaViewer);
    }
    
    GAViewer(iEuclidViewer3D impl) throws Exception {
        super(null, null, null, -1l);
        this.impl = impl;
        impl.open();
    }
    
    /**
     * 
     * @param parent
     * @param m
     * @param label
     * @param isIPNS
     * @return null if the given multivector is no k-vector
     * 
     * TODO
     * was ist mit einer Schraubachse? Ist die auch ein k-vector? vermutlich nein!
     */
    public GAViewObject addCGAObject(GAViewObject parent, MultivectorValue mv, 
		                             String label, boolean isIPNS){
        
		var sparseDoubleMatrix = mv.elements();
		var sparseDoubleColumnVector = new SparseDoubleColumnVector(sparseDoubleMatrix);
		var doubleArray = sparseDoubleColumnVector.toArray();
		
		GAViewObject result = null;
        // wenn generischer Multivektor, diesen analysieren um herauszufinden welcher
        // Subtyp das sein sollte und dann diesen erzeugen
        GAKVector mm = GAKVector.specialize(m, true);
        if (mm != null){
            result = addCGAObject(parent, mv, label);
        } 
        return result;
    }

    @Override
    public GAViewObject addCGAObject(MultivectorValue mv, String label) {
        return addCGAObject(this, mv, label);
    }
    
    /**
     * Add cga object into the visualization.
     *
     * @param m cga multivector
     * @param label label
     * @return true if the given object can be visualized, false if it is outside 
     * the axis-aligned bounding box and this box is not extended for the given object
     * @throws IllegalArgumentException if multivector is no visualizable type
     */
    GAViewObject addCGAObject(GAViewObject parent, MultivectorValue mv, String label){
         return addCGAObject(parent, mv, label, null);
    }
    // returns null if the given GAKVector m has unknown type
    GAViewObject addCGAObject(GAViewObject parent, MultivectorValue mv, String label, Color color){
        
        long id = -1;
        long[] ids;
            
        // cga ipns objects
        if (m instanceof CGARoundPointIPNS roundPointIPNS){
            //TODO decompose bestimmt auch eine Attitude, was bedeutet das für einen round-point?
            // brauche ich das überhaupt?
            // decompose bestimmt zuerst auch einen round-point, aber den hab ich ja bereits
            //FIXME
            if (color == null){
               id = addRoundPoint(roundPointIPNS.decompose(), label, true);
            } else {
               id = addRoundPoint(roundPointIPNS.decompose(), label, color);
            }
            return new GAViewObject(m,label, parent, id);
        
        } else if (m instanceof CGALineIPNS lineIPNS){
            if (color == null){
                id = addLine(lineIPNS.decomposeFlat(), label, true);
            } else {
                id = addLine(lineIPNS.decomposeFlat(), label, color);
            }
            return new GAViewObject(m,label, parent, id);
            
        } else if (m instanceof CGAPointPairIPNS pointPairIPNS){
            //addPointPair(m.decomposeTangentOrRound(), label, true);
            
            iCGATangentOrRound.EuclideanParameters parameters = pointPairIPNS.decompose();
            double r2 = parameters.squaredSize();
            //double r2 = pointPairIPNS.squaredSize();
            if (r2 < 0){
                //FIXME
                // decomposition schlägt fehl
                // show imaginary point pairs as circles
                //CGACircleIPNS circle = new CGACircleIPNS(pointPairIPNS);
                //addCircle(pointPairIPNS.decomposeTangentOrRound(), label, true);
                //return true;
                System.out.println("Visualize imaginary point pair \""+label+"\" failed!");
                return null;
                
            // tangent vector
            } else if (r2 == 0){
                System.out.println("CGA-Object \""+label+"\" is a tangent vector - not yet supported!");
                return null;

            // real point pair only?
            //FIXME
            } else {
                //ddPointPair(cGAPointPairIPNS.decomposePoints(), label, true);
                //iCGATangentOrRound.EuclideanParameters parameters = pointPairIPNS.decompose();
                Point3d loc = parameters.location();
                System.out.println("pp \""+label+"\" loc=("+String.valueOf(loc.x)+", "+String.valueOf(loc.y)+", "+String.valueOf(loc.z)+
                        " r2="+String.valueOf(parameters.squaredSize()));
                
                if (color == null){
                    ids = addPointPair(parameters, label, true);
                } else {
                    ids = addPointPair(parameters, label, color);
                }  
                // scheint zum gleichen Ergebnis zu führen
                //iCGAPointPair.PointPair pp = pointPairIPNS.decomposePoints();
                //addPointPair(pp, label, true);
                //double r_ = pp.p1().distance(pp.p2())/2;
                //System.out.println("Visualize real point pair \""+label+"\"with r="+String.valueOf(r_));
                //TODO vgl. impl für plane, da wird eine Methode addComplexType verwendet
                // sollte ich das hier nicht auch so machen?
                //CGAViewObject parent2 = new GAViewObject(m, label, parent, -1);
                //CGAViewObject p1 = new GAViewObject(null,label+"_1",parent2, ids[0]);
                //parent.addChild(p1);
                //CGAViewObject p2 = new GAViewObject(null,label+"_2",parent2, ids[1]);
                //parent.addChild(p2);
                //return parent2;
                return addComplexViewObject(m, label, parent, ids);
            }
            
        } else if (m instanceof CGASphereIPNS sphereIPNS){
            if (color == null){
                id = addSphere(sphereIPNS.decompose(), label, true);
            } else {
                id = addSphere(sphereIPNS.decompose(), label, color, true);
            }
            return new GAViewObject(m,label, parent, id);
            
        } else if (m instanceof CGAPlaneIPNS planeIPNS){
            if (color == null){
                ids = addPlane(planeIPNS.decomposeFlat(), label, true, true, true, false);
            } else {
                ids = addPlane(planeIPNS.decomposeFlat(), label, color, true, true, false);
            }
            return addComplexViewObject(m, label, parent, ids);
            
        } else if (m instanceof CGAOrientedPointIPNS orientedPointIPNS){
            if (color == null){
                ids = addOrientedPoint(orientedPointIPNS.decompose(), label, true);
            } else {
                ids = addOrientedPoint(orientedPointIPNS.decompose(), label, color);
            }
            return addComplexViewObject(m, label, parent, ids);
            
        } else if (m instanceof CGAFlatPointIPNS flatPointIPNS){
            if (color == null){
                id = addFlatPoint(flatPointIPNS.decomposeFlat(), label, true);
            } else {
                id = addFlatPoint(flatPointIPNS.decomposeFlat(), label, color);
            }      
            return new GAViewObject(m,label, parent, id);
            
        } else if (m instanceof CGACircleIPNS circleIPNS){
            if (color == null){
                id = addCircle(circleIPNS.decompose(), label, true);
            } else {
                id = addCircle(circleIPNS.decompose(), label, color);
            }
            return new GAViewObject(m,label, parent, id);
            
        }
        //TODO
        // attitude/free vector dashed/stripled arrow at origin
        // tangent vector solid arrow at origin?
        
        
        // cga opns objects
        
        if (m instanceof CGARoundPointOPNS roundPointOPNS){
            if (color == null){
                id = addRoundPoint(roundPointOPNS.decompose(), label, false);
            } else {
                id = addRoundPoint(roundPointOPNS.decompose(), label, color);
            }
            return new GAViewObject(m,label, parent, id);
            
        } else if (m instanceof CGALineOPNS lineOPNS){
            if (color == null){
                id = addLine(lineOPNS.decomposeFlat(), label, false);
            } else {
                id = addLine(lineOPNS.decomposeFlat(), label, color);
            }
            return new GAViewObject(m,label, parent, id);
            
        } else if (m instanceof CGAPointPairOPNS pointPairOPNS){
            iCGATangentOrRound.EuclideanParameters parameters = pointPairOPNS.decompose();
            
            if (color == null){
                ids = addPointPair(parameters, label, false);
            } else {
                ids = addPointPair(parameters, label, color);
            }
            //iCGAPointPair.PointPair pp = pointPairOPNS.decomposePoints();
            //addPointPair(pp, label, false);
            GAViewObject parent2 = new GAViewObject(m, label,  parent, -1);
            GAViewObject p1 = new GAViewObject(null,label+"_1",parent2, ids[0]);
            parent.addChild(p1);
            GAViewObject p2 = new GAViewObject(null,label+"_2",parent2, ids[1]);
            parent.addChild(p2);
            return parent2;
            
        } else if (m instanceof CGASphereOPNS sphereOPNS){
            if (color == null){
                id = addSphere(sphereOPNS.decompose(), label, false);
            } else {
                id = addSphere(sphereOPNS.decompose(), label, color, false);
            }
            return new GAViewObject(m,label, parent, id);
            
        } else if (m instanceof CGAPlaneOPNS planeOPNS){
            if (color == null){
                ids = addPlane(planeOPNS.decomposeFlat(), label, false, true, true, false);
            } else {
                ids = addPlane(planeOPNS.decomposeFlat(), label, color, true, true, false);
            }
            return addComplexViewObject(m, label, parent, ids);
            
        } else if (m instanceof CGACircleOPNS circleOPNS){
            if (color == null){
                id = addCircle(circleOPNS.decompose(), label, false);
            } else {
                id = addCircle(circleOPNS.decompose(), label, color);
            }
            return new GAViewObject(m,label, parent, id);
            
        } else if (m instanceof CGAOrientedPointOPNS orientedPointOPNS){
            if (color == null){
                ids = addOrientedPoint(orientedPointOPNS.decompose(), label, false);
            } else {
                ids = addOrientedPoint(orientedPointOPNS.decompose(), label, color);
            }
            return addComplexViewObject(m, label, parent, ids);
        }
        //TODO
        // flat-point als Würfel darstellen

        System.out.println("add view object failed: \""+label+"\" has unknown type or is not yet supported!");
        return null;
    }
    
    // der parent ist momentan immer der viewer selbst also root, das könnte sich aber ändern
    private GAViewObject addComplexViewObject(CGAMultivector m, String label, GAViewObject parent, long[] ids){
        GAViewObject parent2 = new GAViewObject(m, label,  parent, -1);
        for (int i=0;i<ids.length;i++){
            GAViewObject p = new GAViewObject(null,label+"_"+String.valueOf(i+1),parent2, ids[i]);
            parent2.addChild(p);
        }
        return parent2;
    }
    
    void transform(GAViewObject obj, CGAScrew motor){
        impl.transform(obj.getId(), convert(motor));
    }
    
    // TODO kann ich eine Screw überhaupt in eine 4x4-Matrix überführen?
    private Matrix4d convert(CGAScrew motor){
        //TODO
        //MotorParameters motorParameters = motor.decomposeMotor();
        
        return null;
    }
    
    
    /**
     * Add a point to the 3d view.
     *
     * @param parameters unit is [m]
     * @param isIPNS
     * @param label label or null if no label needed
     */
    long addRoundPoint(iCGATangentOrRound.EuclideanParameters parameters, String label, boolean isIPNS){
        Color color = COLOR_GRADE_1;
        if (!isIPNS) color = COLOR_GRADE_4;
        return addRoundPoint(parameters, label, color);
    }
    
    long addRoundPoint(iCGATangentOrRound.EuclideanParameters parameters, String label, Color color){
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = parameters.location();
        System.out.println("Add point \""+label+"\" at ("+String.valueOf(location.x)+","+
                        String.valueOf(location.y)+", "+String.valueOf(location.z)+")!");
        location.scale(1000d);
        return impl.addSphere(location, POINT_RADIUS*2*1000, color, label, false);
    }
    
    
    /**
     * Add a sphere to the 3d view.
     *
     * @param parameters unit is [m]
     * @param label
     * @param isIPNS
     */
    long addSphere(iCGATangentOrRound.EuclideanParameters parameters,
                                              String label, boolean isIPNS){
            Color color = COLOR_GRADE_1;
            if (!isIPNS) color = COLOR_GRADE_4;
            return addSphere(parameters, label, color, true);
    }
    long addSphere(iCGATangentOrRound.EuclideanParameters parameters,
                                              String label, Color color, boolean transparency){
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = parameters.location();
        location.scale(1000d);
        boolean imaginary = false;
        if (parameters.squaredSize() < 0){
            imaginary = true;
        }
        //TODO
        // Farbe ändern für imaginäre Kugeln
        double radius = Math.sqrt(Math.abs(parameters.squaredSize()));
        radius *= 1000;
        System.out.println("Add sphere \""+label+"\" at ("+String.valueOf(location.x)+"mm,"+
                        String.valueOf(location.y)+"mm, "+String.valueOf(location.z)+"mm) with radius "+
                        String.valueOf(radius)+"mm!");

        return impl.addSphere(location, radius, color, label, true);
    }

    /**
     * Add a plane to the 3d view.
     *
     * @param parameters unit is [m]
     * @param label
     * @param isIPNS
     * @param showPolygon 
     * @param showNormal 
     * @return true, if the plane is visible in the current bounding box
     */
    long[] addPlane(iCGAFlat.EuclideanParameters parameters, String label,
                     boolean isIPNS, boolean showPolygon, boolean showNormal, boolean transparency){
        Color color = COLOR_GRADE_1;
        if (!isIPNS) color = COLOR_GRADE_4;
        return addPlane(parameters, label, color, showPolygon, showNormal, transparency);
    }
    long[] addPlane(iCGAFlat.EuclideanParameters parameters, String label,
                     Color color, boolean showPolygon, boolean showNormal, boolean transparency){
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = parameters.location();
        location.scale(1000d);
        Vector3d a = parameters.attitude();
        System.out.println("Add plane "+label+" a=("+String.valueOf(a.x)+", "+String.valueOf(a.y)+", "+String.valueOf(a.z)+
                "), o=("+String.valueOf(location.x)+", "+String.valueOf(location.y)+", "+String.valueOf(location.z)+")");
        long[] result = null;
        if (showPolygon){
            result = new long[1];
            result[0] = addPlane(location, a, color, label, showNormal, transparency);
        }
        // scheint zum Absturz zu führen
        /*if (showNormal){
            addArrow(p1, a, TANGENT_LENGTH, 
                         LINE_RADIUS*1000, color, label);
        }*/
        //TODO
        return result;
    }
    /**
     * Add a plane to the 3d view.
     * 
     * @param location first point of the plane, unit is [mm]
     * @param n normal vector
     * @param color color of the plane
     * @param label the text of the label of the plane
     * @return false if outside the bounding-box
     */
    long addPlane(Point3d location, Vector3d n, Color color, String label, 
            boolean showNormal, boolean transparency){
        
        long result = -1;
        
        if (!isValid(location) || !isValid(n)){
            throw new IllegalArgumentException("addPlane(): location or attitude with illegal values!");
        }
        
        // Clipping
        
        //WORKAROUND clippng based on vector-algebra in the util package of 
        // Euclid3dViewAPI
        
        Plane plane = new Plane(new Vector3d(location), n);

        // clipping
        iAABB aabb = impl.getAABB(); // createAxisAlignedBoundBox();
        
        // testweise die Ecken der bounding box visualisieren
        /*List<Point3d> points = aabb.getCorners();
        for (int i=0;i<points.size();i++){
            this.addPoint(points.get(i), Color.BLUE, 30, String.valueOf(i));
        }*/
        
        Point3d[] corners = aabb.clip(plane); // corners of a polygon in a plane
        
        // testweise
        for (int i=0;i<corners.length;i++){
            System.out.println("corner["+String.valueOf(i)+"]: x="+String.valueOf(corners[i].x+", y="+String.valueOf(corners[i].y)+
                    ", z="+String.valueOf(corners[i].z)));
        }
        if (corners.length > 2){
            result = impl.addPolygone(location, corners, color, label, showNormal, transparency);
            //System.out.println("addPlane \""+label+"\": "+String.valueOf(corners.length)+" corners found:");
            /*for (int i=0;i<corners.length;i++){
                System.out.println("Corner "+String.valueOf(i)+": ("+String.valueOf(corners[i].x)+", "+
                        String.valueOf(corners[i].y)+", "+String.valueOf(corners[i].z)+")");
            }*/
        } else {
            System.out.println("addPlane \""+label+"\" failed. Corners cauld not be determined!");
        }
        return result;
    }
    
    
    // grade 2

    /**
     * Add a line to the 3d view.
     *
     * @param parameters, unit is [m]
     * @param isIPNS
     * @param label
     * @return true if the line is inside the bounding box and therefore visible
     */
    long addLine(iCGAFlat.EuclideanParameters parameters, String label, boolean isIPNS){
        Color color = COLOR_GRADE_2;
        if (!isIPNS) color = COLOR_GRADE_3;
        return addLine(parameters, label, color);
    }
    /**
     * Add line based on euclidean parameters location and direction. 
     * 
     * @param parameters
     * @param label
     * @param color
     * @return null, if failed e.g. clipping failed
     */
    long addLine(iCGAFlat.EuclideanParameters parameters, String label, Color color){
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d p1 = parameters.location();
        p1.scale(1000d);
        Vector3d a = parameters.attitude();
        System.out.println("Add line \""+label+"\" at ("+String.valueOf(p1.x)+", "+String.valueOf(p1.y)+
                        ", "+String.valueOf(p1.z)+") with a=("+String.valueOf(a.x)+", "+String.valueOf(a.y)+", "+
                        String.valueOf(a.z)+")");
        
        iAABB aabb = impl.getAABB();
        
        // bis auf L_45 scheint das zu funktionieren
        //return aabb.clip4(line);
        
        // funktioniert
        Point3d[] points = aabb.clip(new Line(new Vector3d(p1), a));
        
        // funktioniert nicht, führt zum Absturz, out of memory
        //return aabb.clip3(line);
        
        long result = -1;
        if (points.length == 2){
            result = impl.addLine(points[0], points[1], color, LINE_RADIUS*1000,  label);
        } else {
            System.out.println("Clipping of line \""+label+"\" failed, because no intersection with the bounding box!");
        }
        return result;
    }
    
    /**
     * Add oriented-point visualized as point and arrow.
     * 
     * @param parameters, unit is [m]
     * @param label
     * @param isIPNS 
     */
    long[] addOrientedPoint(iCGATangentOrRound.EuclideanParameters parameters, 
                                                String label, boolean isIPNS){
       Color color = COLOR_GRADE_2;
       if (!isIPNS) color = COLOR_GRADE_3;
       return addOrientedPoint(parameters, label, color);
    }
    long[] addOrientedPoint(iCGATangentOrRound.EuclideanParameters parameters, 
                                                String label, Color color){
       if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
       
       // p1
       Point3d location = parameters.location();
       location.scale(1000d);
       
       // orientation
       Vector3d direction = parameters.attitude();
       //FIXME soll die length von direction der length der attitude, also dem weight
       // des cga-Objekts entsprechen?
       direction.normalize();
       direction.scale(TANGENT_LENGTH*1000/2d);
       
       System.out.println("Add oriented point \""+label+"\" at ("+String.valueOf(location.x)+","+
                        String.valueOf(location.y)+", "+String.valueOf(location.z)+")!");
      
       long[] result = new long[2];
       
       // point
       result[0] = impl.addSphere(location, POINT_RADIUS*2*1000, color, label, false);
       
       // arrow
       Point3d location2 = new Point3d(location);
       location2.sub(direction);
       direction.scale(2d);
       result[1] = impl.addArrow(location2, direction, 
                        LINE_RADIUS*1000, color, null);
       return result;
       
       //TODO oder besser als Kreis darstellen?
    }

    long addFlatPoint(iCGAFlat.EuclideanParameters parameters, String label, boolean isIPNS){
        Color color = COLOR_GRADE_2;
        if (!isIPNS) color = COLOR_GRADE_3;
        return addFlatPoint(parameters, label, color);
    }
    long addFlatPoint(iCGAFlat.EuclideanParameters parameters, String label, Color color){
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = parameters.location();
        
        System.out.println("Add flat point \""+label+"\" at ("+String.valueOf(location.x)+","+
                        String.valueOf(location.y)+", "+String.valueOf(location.z)+")!");
      
        return impl.addCube(location, parameters.attitude(),
                POINT_RADIUS*2*1000, color, label, true);
    }
    
    /**
     * Add a circle to the 3d view.
     *
     * @param parameters units in [m]
     * @param label name of the circle shown in the visualisation
     * @param isIPNS true, if circle is given in inner-product-null-space representation
     */
    long addCircle(iCGATangentOrRound.EuclideanParameters parameters,
                                              String label, boolean isIPNS){
        Color color = COLOR_GRADE_2;
        if (!isIPNS) color = COLOR_GRADE_3;
        return addCircle(parameters, label, color);
    }
    long addCircle(iCGATangentOrRound.EuclideanParameters parameters,
                                              String label, Color color){
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        boolean isImaginary = false;
        double r2 = parameters.squaredSize();
        if (r2 <0) {
            isImaginary = true;
            System.out.println("Circle \""+label+"\" is imaginary!");
            r2 = -r2;
        }
        double r = Math.sqrt(r2)*1000;
        Point3d location = parameters.location();
        location.scale(1000d);
        Vector3d direction = parameters.attitude();
        
        System.out.println("Add circle \""+label+"\" at ("+String.valueOf(location.x)+"mm,"+
                        String.valueOf(location.y)+"mm, "+String.valueOf(location.z)+"mm) with radius "+
                        String.valueOf(r)+"\"[mm] and n= ("+String.valueOf(direction.x)+","+
                        String.valueOf(direction.y)+", "+String.valueOf(direction.z)+") ");
        return impl.addCircle(location, direction, r, color, label, isImaginary, false);
    }


    // grade 3

    /**
     * Add a point-pair to the 3d view.
     *
     * No imaginary point-pairs, because these are ipns circles.
     *
     * @param pp unit in [m]
     * @param label
     * @param isIPNS true, if ipns representation
     */
    long[] addPointPair(iCGAPointPair.PointPair pp, String label, boolean isIPNS){
        Color color = COLOR_GRADE_3;
        if (!isIPNS) color = COLOR_GRADE_2;
        Point3d[] points = new Point3d[]{pp.p1(), pp.p2()};
        points[0].scale(1000d);
        points[1].scale(1000d);
        return new long[]{
            impl.addSphere(points[0],  POINT_RADIUS*2*1000, color, label, false),
            impl.addSphere(points[1],  POINT_RADIUS*2*1000, color, label, false)};
        //impl.addPointPair(points[0], points[1], label, color, color, LINE_RADIUS*1000, POINT_RADIUS*2*1000);
    }

    /**
     * Add a point-pair to the 3d view.
     *
     * Because parameters are decomposed the point-pair can not be imaginary.
     *
     * @param parameters unit in [m]
     * @param label
     * @param isIPNS true, if ipns represenation
     */
    long[] addPointPair(iCGATangentOrRound.EuclideanParameters parameters,
                                                     String label, boolean isIPNS){
        Color color = COLOR_GRADE_3;
        if (!isIPNS) color = COLOR_GRADE_2;
        return addPointPair(parameters, label, color);
    }
    long[] addPointPair(iCGATangentOrRound.EuclideanParameters parameters,
                                                     String label, Color color){    
        
        if (color == null) throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = parameters.location();
        Vector3d att = parameters.attitude();
        System.out.println("Add point pair \""+label+"\" loc=("+String.valueOf(location.x)+", "+String.valueOf(location.y)+", "+String.valueOf(location.z)+
                  ", att=("+String.valueOf(att.x)+", "+String.valueOf(att.y)+", "+String.valueOf(att.z)+
                "), r2="+String.valueOf(parameters.squaredSize()));
        Point3d[] points = decomposePointPair(parameters);
        System.out.println("p1=("+String.valueOf(points[0].x)+", "+String.valueOf(points[0].y)+", "+String.valueOf(points[0].z)+
                    ", p2=("+String.valueOf(points[1].x)+", "+String.valueOf(points[1].y)+", "+String.valueOf(points[1].z)+")");

        points[0].scale(1000d);
        points[1].scale(1000d);
        //impl.addPointPair(points[0], points[1], label, color, color, LINE_RADIUS*1000, POINT_RADIUS*2*1000);
        
        return new long[]{
            impl.addSphere(points[0],  POINT_RADIUS*2*1000, color, label, false),
            impl.addSphere(points[1],  POINT_RADIUS*2*1000, color, label, false)};
    }
    
    /**
     * Decompose euclidean parameters of a point-pair into two points.
     *
     * Implementation based on determination of location and squared-size.<p>
     *
     * TODO sollte das nicht in CGAPointPair... verschoeben werden?
     * Nein, ich brauche das ja ausserhalb der CGAAPI, aber wohin dann damit?<p>
     * 
     * @param parameters
     * @return the two decomposed points in Point3d[2]
     */
    private static Point3d[] decomposePointPair(iCGATangentOrRound.EuclideanParameters parameters){
        Point3d c = parameters.location();
        double r = Math.sqrt(Math.abs(parameters.squaredSize()));
        Vector3d v = parameters.attitude();
        v.normalize();
        v.scale(r); 
        Point3d[] result = new Point3d[2];
        result[0] = new Point3d(c);
        result[0].add(v);
        result[1] = new Point3d(c);
        result[1].sub(v);
        return result;
    }

    
    /**
     * Add a tangent to the 3d view.
     *
     * @param parameters
     * @param label
     * @param isIPNS
     */
    void addTangentVector(iCGATangentOrRound.EuclideanParameters parameters,
                                                             String label, boolean isIPNS){
            Color color = COLOR_GRADE_3;
            if (!isIPNS) color = COLOR_GRADE_2;
            Vector3d dir = new Vector3d(parameters.attitude());
            dir.normalize();
            dir.scale(TANGENT_LENGTH);
            impl.addArrow(parameters.location(), dir,
                            LINE_RADIUS*1000, color, label);
    }
    
    private static boolean isValid(Tuple3d tuple3d){
        if (!Double.isFinite(tuple3d.x)) return false;
        if (!Double.isFinite(tuple3d.y)) return false;
        return Double.isFinite(tuple3d.z);
    }

    /**
     * This viewer do not know about hierarchy of view elements. 
     * 
     * @param id 
     */
    public void remove(long id) {
       boolean result = impl.removeNode(id);
       if (!result){
           System.out.println("Try to remove node with id="+String.valueOf(id)+" but not found!");
       }
    }
}