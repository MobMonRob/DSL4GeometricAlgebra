package de.dhbw.rahmlab.dsl4ga.euclidview3d.utils;

import de.orat.math.gacalc.util.GeometricObject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GAViewObject {
    
    private long id; // id != -1, wenn das Objekt im viewer visualisiert werden kann, else wenn es nur der parent von children-objekten ist
    
    //TODO soll verwendet werden, wenn später einmal animationen ermöglicht werden
    // sollen bei denen der mv in der DSL geändert wird
    // unklar ob sich das so überhaupt implementieren läßt
	// Es gibt CGAMvValue und CGAMvExpr, beides auf einmal ist nicht zu haben, daher besser
	//GeometricObject Object übergeben? das ist dann auch unabhängig von der Algebra
    private GeometricObject geometricObject;
    
    private String label;
    private GAViewObject parent;
    private List<GAViewObject> children;
    
    GAViewObject(GeometricObject geometricObject, String label, GAViewObject parent, long id){
        this.children = new ArrayList<>();
        setParent(parent);
        this.label = label;
        this.geometricObject = geometricObject;
        this.id = id;
    }
    final void setParent(GAViewObject parent){
        this.parent = parent;
    }
    void addChild(GAViewObject child){
        children.add(child);
    }
    long getId(){
        return id;
    }
    GAViewObject addGAObject(GAViewObject parent, GeometricObject geometricObject/*GAKVector m,*/, String label){
        throw new RuntimeException("Invocation only by the overwritten method in GAAviewer allowed!");
    }
    public GAViewObject addGAObject(GeometricObject geometricObject, String label){
        if (parent != null){
            return parent.addGAObject(this, geometricObject, label);
        } else {
            return addGAObject(this, geometricObject, label);
        }
    }
    
    /*public GAViewObject addGAObject(GeometricObject geometricObject, String label, Color color){
        return getGAViewer().addGAObject(this, geometricObject, label, color);
    }*/

	
    GAViewer getGAViewer(){
        if (parent != null){
            return parent.getGAViewer();
        } else return (GAViewer) this;
    }
    
    /*public void transform(CGAScrew motor){
        GAViewer viewer = getCGAViewer();
        if (id != -1) viewer.transform(this, motor);
        for (GAViewObject obj: children){
            viewer.transform(obj, motor);
        }
    }*/
    
    public void remove() {
        System.out.println("remove \""+this.label+"\"!");
        GAViewer viewer = getGAViewer();
        if (id != -1) viewer.remove(id);
        for (GAViewObject obj: children){
            viewer.remove(obj.getId());
        }
    }
}