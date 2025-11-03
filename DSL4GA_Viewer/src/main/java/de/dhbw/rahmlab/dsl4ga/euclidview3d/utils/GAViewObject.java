package de.dhbw.rahmlab.dsl4ga.euclidview3d.utils;

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
    private CGAMultivector mv;
    
    private String label;
    private GAViewObject parent;
    private List<GAViewObject> children;
    
    GAViewObject(CGAMultivector mv, String label, GAViewObject parent, long id){
        this.children = new ArrayList<>();
        setParent(parent);
        this.label = label;
        this.mv = mv;
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
    GAViewObject addCGAObject(GAViewObject parent, GAKVector m, String label){
        throw new RuntimeException("Invocation only of the overwritten method in CGAAviewer allowed!");
    }
    public GAViewObject addCGAObject(GAKVector m, String label){
        if (parent != null){
            return parent.addCGAObject(this, m, label);
        } else {
            return addCGAObject(this, m, label);
        }
    }
    
    GAViewer getCGAViewer(){
        if (parent != null){
            return parent.getCGAViewer();
        } else return (GAViewer) this;
    }
    
    public GAViewObject addCGAObject(GAKVector m, String label, Color color){
        return getCGAViewer().addCGAObject(this, m, label, color);
    }

    public void transform(CGAScrew motor){
        GAViewer viewer = getCGAViewer();
        if (id != -1) viewer.transform(this, motor);
        for (GAViewObject obj: children){
            viewer.transform(obj, motor);
        }
    }
    
    public void remove() {
        System.out.println("remove \""+this.label+"\"!");
        GAViewer viewer = getCGAViewer();
        if (id != -1) viewer.remove(id);
        for (GAViewObject obj: children){
            viewer.remove(obj.getId());
        }
    }
}