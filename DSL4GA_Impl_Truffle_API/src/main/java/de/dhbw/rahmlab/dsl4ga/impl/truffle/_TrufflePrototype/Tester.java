package de.dhbw.rahmlab.dsl4ga.impl.truffle._TrufflePrototype;

public class Tester {

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; ++i) {
			{
				var fac = new TruffleProgramFactory();
				var prod = fac.parse("");
				prod.invoke(null);
			}
			System.gc();
			Thread.sleep(500);
			System.out.print("\n");
		}

		for (int i = 0; i < 10; ++i) {
			System.gc();
			Thread.sleep(500);
			System.out.print("\n");
		}
	}
}
