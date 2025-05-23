package de.orat.math.graalvmlsp;

import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang.LANGUAGE_ID;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.graalvm.tools.lsp.exceptions.DiagnosticsNotification;

import org.graalvm.tools.lsp.server.types.Hover;
import org.graalvm.tools.lsp.server.types.Position;
import org.graalvm.tools.lsp.server.types.Range;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class HoverTest extends TruffleLSPTest {

	@Test
	public void hoverExtreme() throws InterruptedException, ExecutionException {
		URI uri = createDummyFileUriForGA();

		Future<?> futureOpen = truffleAdapter.parse(PROG_OBJ, LANGUAGE_ID, uri);
		futureOpen.get();

		for (int column = 0; column < 11; ++column) {
			for (int line = 0; line < 13; ++line) {
				Future<Hover> future = truffleAdapter.hover(uri, line, column);
				Hover hover = future.get();
				var hoverRange = hover.getRange();
				if (hoverRange != null) {
					System.out.println("hoooover");
				}
			}
		}
	}

	@Test
	public void hoverNoCoverageDataAvailable() throws InterruptedException, ExecutionException {
		URI uri = createDummyFileUriForGA();

		Future<?> futureOpen = truffleAdapter.parse(PROG_OBJ, LANGUAGE_ID, uri);
		futureOpen.get();

		checkHover(uri, 2, 0, Range.create(Position.create(7, 8), Position.create(7, 11)));
		checkHover(uri, 7, 12, Range.create(Position.create(7, 8), Position.create(7, 13)));
		checkHover(uri, 0, 4, Range.create(Position.create(0, 3), Position.create(4, 0)));
	}

	@Test
	public void hoverWithCoverageDataAvailable() throws InterruptedException, ExecutionException, IOException {
		URI uri = createDummyFileUriForGA();
		// PredictionMode.SLL failed.
		// PredictionMode.SLL failed.
		// org.graalvm.tools.lsp.exceptions.DiagnosticsNotification
		try {
			Future<?> futureOpen = truffleAdapter.parse(PROG_OBJ, LANGUAGE_ID, uri);
			futureOpen.get();
		} catch (ExecutionException ex) {
			StringBuilder sb = new StringBuilder();
			var diag = ((DiagnosticsNotification) ex.getCause()).getDiagnosticParamsCollection();
			// Damit bekommt man die Message aus ValidationException und LanguageRuntimeException.
			diag.forEach(par -> par.getDiagnostics().forEach(d -> sb.append(d.getMessage())));
			String msg = sb.toString();
			throw new RuntimeException(msg, ex);
		}

		/*
		try {
			var url = uri.toURL();
			var lang = Source.findLanguage(url);
			String a = "debugPoint";
		} catch (MalformedURLException ex) {
			throw ex;
		}
		 */
		// Geht nicht. Source.findLanguage(url) funktioniert nicht.
		// Future<Boolean> futureCoverage = truffleAdapter.runCoverageAnalysis(uri);
		// assertTrue(futureCoverage.get());
		Hover hover = checkHover(uri, 8, 3, Range.create(Position.create(8, 3), Position.create(8, 4)));
		// hier scheint mir hover != null aber hover.getRange()==null zu sein
		if (hover == null) {
			System.out.println("hover==null");
		}
		assertTrue(hover.getContents() instanceof List);
		assertEquals(1, ((List<?>) hover.getContents()).size());
		assertTrue(((List<?>) hover.getContents()).get(0) instanceof org.graalvm.tools.lsp.server.types.MarkedString);
		assertEquals("x", ((org.graalvm.tools.lsp.server.types.MarkedString) ((List<?>) hover.getContents()).get(0)).getValue());
		//assertEquals("Object", ((List<?>) hover.getContents()).get(1));
		//assertEquals("meta-object: Object", ((List<?>) hover.getContents()).get(2));
	}

	private Hover checkHover(URI uri, int line, int column, Range range) throws InterruptedException, ExecutionException {
		Future<Hover> future = truffleAdapter.hover(uri, line, column);
		Hover hover = future.get();
		// hover.getRange()==null zu sein
		if (hover == null) {
			System.out.println("hover==null!");
		}
		var hoverRange = hover.getRange();
		assertTrue(rangeCheck(range, hoverRange));
		return hover;
	}
}
