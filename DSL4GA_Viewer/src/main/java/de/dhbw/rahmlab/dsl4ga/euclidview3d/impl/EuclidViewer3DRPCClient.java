package de.dhbw.rahmlab.dsl4ga.euclidview3d.impl;

import de.orat.view3d.euclid3dviewapi.spi.iAABB;
import de.orat.view3d.euclid3dviewapi.spi.iEuclidViewer3D;
import java.awt.Color;
import org.apache.dubbo.config.ReferenceConfig;
import org.jogamp.vecmath.Matrix4d;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class EuclidViewer3DRPCClient implements iEuclidViewer3D {

	private iEuclidViewer3D viewerRPCService;
	private static final int port = 4444;
    
	public EuclidViewer3DRPCClient(){
        ReferenceConfig<iEuclidViewer3D> reference = new ReferenceConfig<>();
            reference.setInterface(iEuclidViewer3D.class);
            reference.setUrl("dubbo://localhost:"+String.valueOf(port));
        viewerRPCService = reference.get();
    }
	
	// SEVERE: null
    // org.apache.dubbo.rpc.RpcException: Failed to invoke the method open in the service de.orat.view3d.   
	// euclid3dviewapi.spi.iEuclidViewer3D. Tried 3 times of the providers [localhost:50054] (1/1) from the 
	// registry localhost:50054 on the consumer 10.172.28.211 using the dubbo version 3.3.0. Last error is: 
	// Invoke remote method timeout. method: open, provider: dubbo://localhost:50054/de.orat.view3d.
	// euclid3dviewapi.spi.iEuclidViewer3D?application=DEFAULT_DUBBO_APP&background=false&executor-management-
	// mode=isolation&file-cache=true&interface=de.orat.view3d.euclid3dviewapi.spi.
	// iEuclidViewer3D&pid=915910&register.ip=10.172.28.211&revision=1.0-
	// SNAPSHOT&side=consumer&sticky=false&unloadClusterRelated=false, cause: Timeout after 1000ms waiting 
    // for result.

	@Override
	public void open() throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		// TODO soll eine Ex feuern, wenn der Server nicht verfügbar ist. Dann soll
		// die Default-Impl des Viewers verwendet werden die ein eigenes Fenster 
		// aufmacht
	}

	@Override
	public boolean close() {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public iAABB getAABB() {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public long addSphere(Point3d location, double radius, Color color, String label, boolean transparency) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public long addLine(Point3d p1, Point3d p2, Color color, double radius, String label) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public long addArrow(Point3d location, Vector3d direction, double radius, Color color, String label) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public long addCircle(Point3d location, Vector3d normal, double radius, Color color, String label, boolean isDashed, boolean isFilled) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public long addPolygone(Point3d location, Point3d[] corners, Color color, String label, boolean showNormal, boolean tranparency) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public long addCube(Point3d location, Vector3d dir, double width, Color color, String label, boolean tranparency) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public long addMesh(String path, Matrix4d transform) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public boolean removeNode(long handle) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public void transform(long handle, Matrix4d transform) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}
	
}
