package de.dhbw.rahmlab.dsl4ga.euclidview3d.utils;

//import de.orat.math.cga.api.CGAScrew.MotorParameters;
import de.orat.math.gacalc.api.MultivectorValue; /*Numeric;*/
import de.orat.math.gacalc.util.GeometricObject;
import de.orat.math.gacalc.util.Tuple;
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
    /*public GAViewObject addCGAObject(GAViewObject parent, MultivectorValue mv, 
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
    }*/

	// das ist die Methode, die der VisualizerService von außen aufruft
	/**
	 * 
	 * @param geometricObject
	 * @param label
	 * @return 
	 * @throws IllegalArgumentException if multivector is no visualizable type (no k-vector) or the arguments 
	 * are illegal
	 */
	public GAViewObject addGeometricObject(GeometricObject geometricObject, String label) {
		return addGeometricObject(this, geometricObject, label);
    }
    
    /**
     * Add geometric object into the visualization.
     *
     * @param m cga multivector
     * @param label label
     * @return true if the given object can be visualized, false if it is outside 
     * the axis-aligned bounding box and this box is not extended for the given object
     * @throws IllegalArgumentException if multivector is no visualizable type (no k-vector) or the arguments 
	 * are illegal
     */
    GAViewObject addGeometricObject(GAViewObject parent, GeometricObject geometricObject, String label){
         return addGeometricObject(parent, geometricObject, label, null);
    }
    // returns null if the given GAKVector m has unknown type
	// brauchts vermutlich nicht mehr, da in diesem Fall GeometricObject bereits nicht bestimmt werden konnte
	/**
	 * 
	 * @param parent
	 * @param mv
	 * @param label
	 * @param color if == null, color is determined automatically for the geometric object type
	 * @return 
	 */
    GAViewObject addGeometricObject(GAViewObject parent, GeometricObject geometricObject, String label, 
		                        /*boolean isIPNS,*/ Color color){
        
        long id = -1;
        long[] ids;
            
		switch (geometricObject.geometricType){
			case GeometricObject.GeometricType.ROUND_POINT:
				if (color == null){
					id = addRoundPoint(geometricObject, label);
				} else {
					id = addRoundPoint(geometricObject, label, color);
				}
				return new GAViewObject(geometricObject, label, parent, id);
				
			case GeometricObject.GeometricType.LINE:
				if (color == null){
					id = addLine(geometricObject, label);
				} else {
					id = addLine(geometricObject, label, color);
				}
				return new GAViewObject(geometricObject, label, parent, id);
				
			case GeometricObject.GeometricType.DIPOLE:
				double r2 = geometricObject.getSignedSquaredSize(); 
				//double r2 = parameters.squaredSize();
				//double r2 = pointPairIPNS.squaredSize();
				// r2<0 imaginary dipole == circle
				// r2==0 tangent not yet supported
				// tangent vector
				
				Point3d loc = toPoint3d(geometricObject.location[0]);
			    System.out.println("pp \""+label+"\" loc=("+String.valueOf(loc.x)+", "+
					            String.valueOf(loc.y)+", "+String.valueOf(loc.z)+
							" r2="+String.valueOf(geometricObject.getSignedSquaredSize()));
				if (color == null){
					ids = addPointPair(geometricObject, label);
				} else {
					ids = addPointPair(geometricObject, label, color);
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
				return addComplexViewObject(geometricObject, label, parent, ids);
				
			case GeometricObject.GeometricType.SPHERE:
				if (color == null){
					id = addSphere(geometricObject, label);
				} else {
					id = addSphere(geometricObject, label, color, true);
				}
				return new GAViewObject(geometricObject, label, parent, id);
				
			case GeometricObject.GeometricType.PLANE:
				if (color == null){
					ids = addPlane(geometricObject, label,  true, true, false);
				} else {
					ids = addPlane(geometricObject, label, color, true, true, false);
				}
				return addComplexViewObject(geometricObject, label, parent, ids);
				
			case GeometricObject.GeometricType.ORIENTED_POINT:
				if (color == null){
					ids = addOrientedPoint(geometricObject, label);
				} else {
					ids = addOrientedPoint(geometricObject, label, color);
				}
				return addComplexViewObject(geometricObject, label, parent, ids);
				
			case GeometricObject.GeometricType.FLAT_POINT:
				if (color == null){
					id = addFlatPoint(geometricObject, label);
				} else {
					id = addFlatPoint(geometricObject, label, color);
				}      
				return new GAViewObject(geometricObject, label, parent, id);
				
			case GeometricObject.GeometricType.CIRCLE:
				if (color == null){
					id = addCircle(geometricObject, label);
				} else {
					id = addCircle(geometricObject, label, color);
				}
				return new GAViewObject(geometricObject, label, parent, id);
		}
		
		
        //TODO
        // attitude/free vector dashed/stripled arrow at origin
        // tangent vector solid arrow at origin?
        
        
        //TODO
        // flat-point als Würfel darstellen

        System.out.println("add view object failed: \""+label+"\" has unknown type or is not yet supported!");
        return null;
    }
    
	
	//----------
	
	
    // der parent ist momentan immer der viewer selbst also root, das könnte sich aber ändern
    private GAViewObject addComplexViewObject(GeometricObject geometricObject, String label, 
												GAViewObject parent, long[] ids){
        GAViewObject parent2 = new GAViewObject(geometricObject, label,  parent, -1);
        for (int i=0;i<ids.length;i++){
            GAViewObject p = new GAViewObject(null,label+"_"+String.valueOf(i+1),parent2, ids[i]);
            parent2.addChild(p);
        }
        return parent2;
    }
    
    /*void transform(GAViewObject obj, CGAScrew motor){
        impl.transform(obj.getId(), convert(motor));
    }*/
    
    // TODO kann ich eine Screw überhaupt in eine 4x4-Matrix überführen?
    /*private Matrix4d convert(CGAScrew motor){
        //TODO
        //MotorParameters motorParameters = motor.decomposeMotor();
        
        return null;
    }*/
    
    
	//-------------
	
	private static Point3d toPoint3d(Tuple tuple){
		return new Point3d(tuple.values[0], tuple.values[1], tuple.values[2]);  //parameters.location();
	}
	private static Vector3d toVector3d(Tuple tuple){
		return new Vector3d(tuple.values[0], tuple.values[1], tuple.values[3]);
	}
	
	
	
	//-------------
	
    /**
     * Add a point to the 3d view.
     *
     * @param parameters unit is [m]
     * @param isIPNS
     * @param label label or null if no label needed
     */
    long addRoundPoint(GeometricObject geometricObject, String label){
        //Color color = COLOR_GRADE_1;
        //if (geometricObject.isOPNS()) color = COLOR_GRADE_4;
        return addRoundPoint(geometricObject, label, getColor(geometricObject));
    }
    
    long addRoundPoint(GeometricObject geometricObject, String label, Color color){
        
        if (color == null) throw 
			new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
		Point3d location = toPoint3d(geometricObject.location[0]);
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
	 * @throws IllegalArgumentException
     */
    long addSphere(GeometricObject geometricObject, String label){
            //Color color = COLOR_GRADE_1;
            //if (geometricObject.isOPNS()) color = COLOR_GRADE_4;
            return addSphere(geometricObject, label, getColor(geometricObject), true);
    }
	/**
	 * Add sphere
	 * 
	 * @param geometricObject
	 * @param label
	 * @param color
	 * @param transparency
	 * @return 
	 * @throws IllegalArgumentException
	 */
    long addSphere(GeometricObject geometricObject, String label, Color color, boolean transparency){
        if (color == null) throw 
			new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        //Point3d location = parameters.location();
		Point3d location = toPoint3d(geometricObject.location[0]);
        location.scale(1000d);
        boolean imaginary = false;
        if (geometricObject.getSignedSquaredSize() < 0){
            imaginary = true;
        }
        //TODO
        // Farbe ändern für imaginäre Kugeln
        double radius = Math.sqrt(Math.abs(geometricObject.getSignedSquaredSize()));
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
    long[] addPlane(GeometricObject geometricObject, String label,
                     boolean showPolygon, boolean showNormal, boolean transparency){
        //Color color = COLOR_GRADE_1;
        //if (geometricObject.isOPNS()) color = COLOR_GRADE_4;
        return addPlane(geometricObject, label, getColor(geometricObject), showPolygon, showNormal, transparency);
    }
    long[] addPlane(GeometricObject geometricObject, String label,
                     Color color, boolean showPolygon, boolean showNormal, boolean transparency){
        
        if (color == null) throw 
			new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        //Point3d location = parameters.location();
		Point3d location = toPoint3d(geometricObject.location[0]);
        location.scale(1000d);
        //Vector3d a = parameters.attitude();
		Vector3d a = toVector3d(geometricObject.attitude);
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
    long addLine(GeometricObject geometricObject, String label){
        //Color color = COLOR_GRADE_2;
        //if (geometricObject.isOPNS()) color = COLOR_GRADE_3;
        return addLine(geometricObject, label, getColor(geometricObject));
    }
    /**
     * Add line based on euclidean parameters location and direction. 
     * 
     * @param parameters
     * @param label
     * @param color
     * @return null, if failed e.g. clipping failed
     */
    long addLine(GeometricObject geometricObject, String label, Color color){
        if (color == null) 
			throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d p1 = toPoint3d(geometricObject.location[0]);
        p1.scale(1000d);
		Vector3d a = toVector3d(geometricObject.attitude);
		
        //Vector3d a = parameters.attitude();
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
    long[] addOrientedPoint(GeometricObject geometricObject, String label){
       //Color color = COLOR_GRADE_2;
       //if (geometricObject.isOPNS()) color = COLOR_GRADE_3;
       return addOrientedPoint(geometricObject, label, getColor(geometricObject));
    }
    long[] addOrientedPoint(GeometricObject geometricObject, String label, Color color){
       if (color == null) 
		    throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
       
       // location
	   Point3d location = toPoint3d(geometricObject.location[0]);
	   location.scale(1000d);
       
       // orientation
       Vector3d direction = toVector3d(geometricObject.attitude);
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

    long addFlatPoint(GeometricObject geometricObject, String label){
        //Color color = COLOR_GRADE_2;
        //if (geometricObject.isOPNS()) color = COLOR_GRADE_3;
        return addFlatPoint(geometricObject, label, getColor(geometricObject));
    }
    long addFlatPoint(GeometricObject geometricObject, String label, Color color){
        if (color == null) 
			throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        Point3d location = toPoint3d(geometricObject.location[0]);
	    //location.scale(1000d);
        
        System.out.println("Add flat point \""+label+"\" at ("+String.valueOf(location.x)+","+
                        String.valueOf(location.y)+", "+String.valueOf(location.z)+")!");
      
		Vector3d direction = toVector3d(geometricObject.attitude);
       
        return impl.addCube(location, direction /*parameters.attitude()*/,
                POINT_RADIUS*2*1000, color, label, true);
    }
    
    /**
     * Add a circle to the 3d view.
     *
     * @param parameters units in [m]
     * @param label name of the circle shown in the visualisation
     * @param isIPNS true, if circle is given in inner-product-null-space representation
     */
    long addCircle(GeometricObject geometricObject, String label){
        //Color color = COLOR_GRADE_2;
        //if (geometricObject.isOPNS()) color = COLOR_GRADE_3;
        return addCircle(geometricObject, label, getColor(geometricObject));
    }
    long addCircle(GeometricObject geometricObject, String label, Color color){
        if (color == null) 
			throw new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        boolean isImaginary = false;
        double r2 = geometricObject.getSignedSquaredSize();
        if (r2 <0) {
            isImaginary = true;
            System.out.println("Circle \""+label+"\" is imaginary!");
            r2 = -r2;
        }
        double r = Math.sqrt(r2)*1000;
        //Point3d location = parameters.location();
		Point3d location = toPoint3d(geometricObject.location[0]);
        location.scale(1000d);
        //Vector3d direction = parameters.attitude();
		Vector3d direction = toVector3d(geometricObject.attitude);
        
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
    long[] addPointPair(GeometricObject geometricObject, String label){
        //Color color = COLOR_GRADE_3;
        //if (geometricObject.isOPNS()) color = COLOR_GRADE_2;
		
		Color color = getColor(geometricObject);
        Point3d[] points = new Point3d[]{toPoint3d(geometricObject.location[0]),
			                        toPoint3d(geometricObject.location[1])}; //new Point3d[]{pp.p1(), pp.p2()};
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
    /*long[] addPointPair(iCGATangentOrRound.EuclideanParameters parameters,
                                                     String label, boolean isIPNS){
        Color color = COLOR_GRADE_3;
        if (!isIPNS) color = COLOR_GRADE_2;
        return addPointPair(parameters, label, color);
    }*/
    long[] addPointPair(GeometricObject geometricObject, String label, Color color){    
        if (color == null) throw 
			new IllegalArgumentException("color==null not allowed, use method with argument ipns instead!");
        
        //Point3d location = parameters.location();
		Point3d location = toPoint3d(geometricObject.location[0]);
        //Vector3d att = parameters.attitude();
		Vector3d att = toVector3d(geometricObject.attitude);
        System.out.println("Add point pair \""+label+"\" loc=("+String.valueOf(location.x)+", "+String.valueOf(location.y)+", "+String.valueOf(location.z)+
                  ", att=("+String.valueOf(att.x)+", "+String.valueOf(att.y)+", "+String.valueOf(att.z)+
                "), r2="+String.valueOf(geometricObject.getSignedSquaredSize()));
        Point3d[] points = decomposePointPair(geometricObject);
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
     * @param parameters
     * @return the two decomposed points in Point3d[2]
     */
    private static Point3d[] decomposePointPair(GeometricObject geometricObject){
		Point3d c = toPoint3d(geometricObject.location[0]);
		double r = Math.sqrt(Math.abs(geometricObject.getSignedSquaredSize()));
        Vector3d v = toVector3d(geometricObject.attitude);
		v.normalize();
        v.scale(r); 
        Point3d[] result = new Point3d[2];
        result[0] = new Point3d(c);
        result[0].add(v);
        result[1] = new Point3d(c);
        result[1].sub(v);
        return result;
    }

	Color getColor(GeometricObject geometricObject){
		return getColor(geometricObject.grade());
	}
	/**
	 * @param geometricObject
	 * @return null for unknown grade
	 */
	Color getColor(int grade){
		Color result = null;
		switch (grade){
			case 1:
				result = COLOR_GRADE_1;
				break;
			case 2:
				result = COLOR_GRADE_2;
				break;
			case 3:
				result = COLOR_GRADE_3;
				break;
			case 4:
				result = COLOR_GRADE_4;
				break;
			default:
		}
		return result;
	}
    
    /**
     * Add a real tangent to the 3d view.
     *
	 * In the case of a tangent at a point on a round the length of the tangend is the radius of the round,
	 * 
	 * Keep in mind: Imaginary rounds do not have real points on its surface, therefor they have no real
	 * tangents.
	 * 
	 * TODO
	 * This is a visualization for tangents on lines and circles only, these tangents are bivectors and 
	 * visualized by a round point and an arrow.
	 * Tangents at points on planes and spheres are trivectors and show be visualized as a bivector, that means 
	 * as a point and a circle with arrows showing the direction of the circle. Maybe the circle should be
	 * filled.
	 * 
     * @param geometricObject with grade 2,3 or 4
     * @param label
     * @param isIPNS
     */
    long[] addTangent(GeometricObject geometricObject, String label){
		int grade = geometricObject.grade();
		if (grade < 2 || grade > 4) 
			throw new IllegalArgumentException("Tangents must have grades in {2,3,4}]allowed!");
		return addTangent(geometricObject, label,  getColor(grade));
	}
     
	long[] addTangent(GeometricObject geometricObject, String label, Color color){
        if (color == null) 
		    throw new IllegalArgumentException("color==null not allowed!");
    
		long[] result = new long[2];
		
		//TODO
		// unterscheide visualisations for grade 2,3,4
		
		// add arrow at localisation
		
		//Vector3d dir = new Vector3d(parameters.attitude());
		Vector3d dir = toVector3d(geometricObject.attitude);
		dir.normalize();
		dir.scale(TANGENT_LENGTH);
		
		Point3d location = toPoint3d(geometricObject.location[0]);
        
		
        // point
        result[0] = impl.addSphere(location, POINT_RADIUS*2*1000, color, label, false);
       
		// arrow
		result[1] = impl.addArrow(location, dir, LINE_RADIUS*1000, color, label);
		
		return result;
    }
    
	
	//---------
	
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