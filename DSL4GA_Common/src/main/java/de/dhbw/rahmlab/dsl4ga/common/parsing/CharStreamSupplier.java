package de.dhbw.rahmlab.dsl4ga.common.parsing;

// import com.oracle.truffle.api.source.Source;
import java.io.IOException;
import java.io.Reader;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

public class CharStreamSupplier {

	private final CharStream charStream;

	private CharStreamSupplier(CharStream charStream) {
		this.charStream = charStream;
	}

	public static CharStreamSupplier from(CharStream charStream) {
		return new CharStreamSupplier(charStream);
	}

	public static CharStreamSupplier from(String input) {
		return new CharStreamSupplier(CharStreams.fromString(input));
	}

	/*
	public static CharStreamSupplier from(Source input) throws IOException {
		return new CharStreamSupplier(CharStreams.fromReader(input.getReader()));
	}
	 */
	public static CharStreamSupplier from(Reader reader) throws IOException {
		return new CharStreamSupplier(CharStreams.fromReader(reader));
	}

	public CharStream get() {
		return this.charStream;
	}
}
