/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing;

import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

/**
 *
 * @author fabian
 */
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
