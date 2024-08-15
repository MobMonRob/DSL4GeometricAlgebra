package de.dhbw.rahmlab.dsl4ga.launcher;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Arguments;
import java.io.File;
import java.io.IOException;

import org.graalvm.polyglot.Source;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Program;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Result;

import java.util.Arrays;

public final class DSL4GAMain {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			throw new IllegalArgumentException("No file has been provided.");
		}

		String file = args[0];

		Source source = Source.newBuilder(Program.LANGUAGE_ID, new File(file)).build();

		System.exit(executeSource(source));
	}

	private static int executeSource(Source source) {
		try (Program program = new Program(source)) {
			Arguments arguments = new Arguments();

			Result answer = program.invoke(arguments);
			double[][] answerScalar = answer.decomposeDoubleArray();

			System.out.println("answer: ");
			for (int i = 0; i < answerScalar.length; ++i) {
				String current = Arrays.toString(answerScalar[i]);
				System.out.println(current);
			}
			System.out.println();
		}

		return 0;
	}
}
