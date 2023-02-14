package de.dhbw.rahmlab.annotation.user;

import de.dhbw.rahmlab.annotation.user.gen.WrapperGen;

public class AnnotationUser {

	public static void main(String[] args) {
		double out = WrapperGen.INSTANCE.targetMethod1(1, 10);
		System.out.println("targetMethod1 out: " + out);
	}
}
