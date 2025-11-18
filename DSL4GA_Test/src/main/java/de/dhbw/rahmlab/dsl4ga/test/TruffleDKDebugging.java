package de.dhbw.rahmlab.dsl4ga.test;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jogamp.vecmath.Matrix4d;
import util.cga.SparseCGAColumnVector;

public class TruffleDKDebugging {

	// dh parameters UR5e
	private static double[] a = new double[]{0.0, -0.425, -0.3922, 0.0, 0.0, 0.0};
	private static double[] d = new double[]{0.1625, 0.0, 0.0, 0.1333, 0.0997, 0.0996};
	private static double[] alpha = new double[]{}; //TODO

	public static void main(String[] args) throws Exception {
		invocationTest();
	}

	/**
	 * <pre>
	 * netbeans-ocga Plugin muss installiert sein.
	 * dk.ocga Breakpoint setzen per IDE.
	 * Rechtsklick auf die Datei TruffleDKDebugging.java "Debug file".
	 * Breakpoint und durchsteppen sollte funktionieren. Ebenso Visualisierung.
	 * Es braucht ein paar Sekunden bis sich das Visualisierungsfenster öffnet beim    ersten Doppelpunkt.
	 * </pre>
	 * 
	 * TODO
	 * Umstellung auf annotation API
	 * Matrix-Impl zum Vergleich mit der GA impl
	 * Wie Pose definieren?
	 */
	private static void invocationTest() throws Exception {
		String path = "./gafiles/common/dk2.ocga";
		var uri = TruffleDKDebugging.class.getResource(path);
		if (uri == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}

		var fac = new TruffleProgramFactory();
		var prog = fac.parse(uri);
		
		List<SparseDoubleMatrix> args = new ArrayList<>();
		SparseCGAColumnVector p = SparseCGAColumnVector.createEuclid(new double[]{0.5, 0.5, 0d});
		args.add(p);
		var res = prog.invoke(args);

		System.out.println("answer: ");
		res.forEach(System.out::println);
		System.out.println();
	}
	
	private static List<SparseDoubleMatrix> createArguments(double[] thetas){
		List<SparseDoubleMatrix> args = new ArrayList<>();
		for (int i=0;i<thetas.length;i++){
			SparseCGAColumnVector p = SparseCGAColumnVector.createScalar(thetas[i]);
			args.add(p);
		}
		return args;
	}
	
	private static Pose dk(double[] theta){
		Matrix4d m = new Matrix4d();
		m.setIdentity();
		for (int i=0;i<6;i++){
			m.mul(createRz(theta[i]));
			m.mul(createTz(d[i]));
			m.mul(createTx(a[i]));
			m.mul(createRx(alpha[i]));
		}
		return new Pose(m);
	}
	
	private static Matrix4d createRz(double theta){
		Matrix4d m = new Matrix4d();
		m.m00 = Math.cos(theta);
		m.m01 = - Math.sin(theta);
		m.m10 = Math.sin(theta);
		m.m11 = Math.cos(theta);
		return m;
	}
	private static Matrix4d createTz(double d){
		Matrix4d m = new Matrix4d();
		m.m00 = 1d;
		m.m11 = 1d;
		m.m22 = 1d;
		m.m33 = 1d;
		m.m23 = d;
		return m;
	}
	private static Matrix4d createTx(double a){
		Matrix4d m = new Matrix4d();
		m.m00 = 1d;
		m.m11 = 1d;
		m.m22 = 1d;
		m.m33 = 1d;
		m.m03 = a;
		return m;
	}
	private static Matrix4d createRx(double alpha){
		Matrix4d m = new Matrix4d();
		m.m00 = 1d;
		m.m11 = Math.cos(alpha);
		m.m12 = -Math.sin(alpha);
		m.m21 = Math.sin(alpha);
		m.m22 = Math.cos(alpha);
		m.m33 = 1d;
		return m;
	}
	private static Matrix4d createDH(double theta, double d, double alpha, double a){
		Matrix4d m = new Matrix4d();
		m.m00 = Math.cos(theta);
		m.m01 = -Math.sin(theta)*Math.cos(alpha);
		m.m02 = Math.sin(theta)*Math.sin(alpha);
		m.m03 = a*Math.cos(theta);
		m.m10 = Math.sin(theta);
		m.m11 = Math.cos(theta)*Math.cos(alpha);
		m.m12 = -Math.cos(theta)*Math.sin(alpha);
		m.m13 = a*Math.sin(theta);
		m.m21 = Math.sin(alpha);
		m.m22 = Math.cos(alpha);
		m.m23 = d;
		m.m33 = 1d;
		return m;
	}
	private static class Pose {
		private Matrix4d m;
		public Pose(Matrix4d m){
			this.m = m;
		}
	}
}
