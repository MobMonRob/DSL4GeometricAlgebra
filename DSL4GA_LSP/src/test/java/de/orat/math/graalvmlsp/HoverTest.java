/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
//package org.graalvm.tools.lsp.test.server;
package de.orat.math.graalvmlsp;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;

import static de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Program.LANGUAGE_ID;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graalvm.polyglot.Source;
import org.graalvm.tools.lsp.exceptions.DiagnosticsNotification;

import org.graalvm.tools.lsp.server.types.Hover;
import org.graalvm.tools.lsp.server.types.Position;
import org.graalvm.tools.lsp.server.types.Range;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
//import org.junit.Test;
import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("deprecation")
public class HoverTest extends TruffleLSPTest {

    @Test
    public void hoverNoCoverageDataAvailable() throws InterruptedException, ExecutionException {
        URI uri = createDummyFileUriForGA();
        // HoverTest.hoverNoCoverageDataAvailable:50 » Execution java.lang.RuntimeException: java.lang.NullPointerException: Cannot invoke "de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.iParsingService.parse(de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier, de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext)" 
        //because the return value of "de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingServiceProvider.
        // getParsingService()" is null
        // der obere Fehler scheint behoben mit dem ParserProvider workaround
        // java.util.concurrent.ExecutionException: org.graalvm.tools.lsp.exceptions.DiagnosticsNotification
        Future<?> futureOpen = truffleAdapter.parse(PROG_OBJ, LANGUAGE_ID, uri);
        futureOpen.get();

        checkHover(uri, 1, 5, Range.create(Position.create(1, 4), Position.create(1, 7)));
        checkHover(uri, 1, 8, Range.create(Position.create(1, 4), Position.create(1, 9)));
        checkHover(uri, 0, 10, Range.create(Position.create(0, 9), Position.create(3, 1)));
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

        Hover hover = checkHover(uri, 8, 10, Range.create(Position.create(8, 9), Position.create(8, 12)));
        assertTrue(hover.getContents() instanceof List);
        assertEquals(3, ((List<?>) hover.getContents()).size());
        assertTrue(((List<?>) hover.getContents()).get(0) instanceof org.graalvm.tools.lsp.server.types.MarkedString);
        assertEquals("obj", ((org.graalvm.tools.lsp.server.types.MarkedString) ((List<?>) hover.getContents()).get(0)).getValue());
        assertEquals("Object", ((List<?>) hover.getContents()).get(1));
        assertEquals("meta-object: Object", ((List<?>) hover.getContents()).get(2));
    }

    private Hover checkHover(URI uri, int line, int column, Range range) throws InterruptedException, ExecutionException {
        Future<Hover> future = truffleAdapter.hover(uri, line, column);
        Hover hover = future.get();
        assertTrue(rangeCheck(range, hover.getRange()));
        return hover;
    }
}
