package de.dhbw.rahmlab.dsl4ga.impl.truffle._TrufflePrototype;

public class Context implements AutoCloseable {

	private int[] arr = new int[1000000000];

	public Context() {
		arr[99999] = 99;
		System.out.print("C");
	}

	@Override
	public void close() throws Exception {
		arr = null;
		System.out.print("-");
	}

	public void finalize() {
		System.out.print("PPPPPPP");
	}
}
