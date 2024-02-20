package de.dhbw.rahmlab.geomalgelang.parsing;

import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractParsingTest {

	Context context;

	@BeforeAll
	void setup() {
		context = Context.create();
		context.enter();
	}

	@AfterAll
	void desetup() {
		context.leave();
		context.close();
	}
}
