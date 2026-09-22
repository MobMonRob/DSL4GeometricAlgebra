package de.dhbw.rahmlab.dsl4ga.test.truffle.api;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.GAFactoryService;
import de.orat.math.gacalc.api.GAFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GAFactoryServiceTest {

	@Test
	void resolvesFactoryDeclaredBySourceFile() throws URISyntaxException, IOException {
		var factory = getFactory("dk2.ga");

		assertEquals("cga", factory.getAlgebra());
	}

	@Test
	void resolvesPgaFactoryDeclaredBySourceFile() throws URISyntaxException, IOException {
		var factory = getFactory("pgatest.ga");

		assertEquals("pga", factory.getAlgebra());
	}

	@Test
	void reportsUnreadablePath() {
		Path missingPath = Path.of("does-not-exist.ga");

		assertThrows(IOException.class, () -> GAFactoryService.getFactory(missingPath));
	}

	private static GAFactory getFactory(String filename) throws URISyntaxException, IOException {
		var resource = GAFactoryServiceTest.class.getResource("/de/dhbw/rahmlab/dsl4ga/test/cga/gafiles/common/" + filename);
		assertNotNull(resource);
		return GAFactoryService.getFactory(Path.of(resource.toURI()));
	}
}
