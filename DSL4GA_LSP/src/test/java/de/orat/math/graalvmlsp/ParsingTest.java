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
package de.orat.math.graalvmlsp;

import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang.LANGUAGE_ID;
import java.io.File;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.graalvm.tools.lsp.server.types.Diagnostic;
import org.graalvm.tools.lsp.server.types.Position;
import org.graalvm.tools.lsp.server.types.PublishDiagnosticsParams;
import org.graalvm.tools.lsp.server.types.Range;
import org.graalvm.tools.lsp.server.types.TextDocumentContentChangeEvent;
import org.graalvm.tools.lsp.exceptions.DiagnosticsNotification;
import org.graalvm.tools.lsp.exceptions.UnknownLanguageException;
import org.graalvm.tools.lsp.server.utils.TextDocumentSurrogate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.Rule;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
// import org.junit.rules.ExpectedException;

public class ParsingTest extends TruffleLSPTest {

    @Test
    public void didOpenClose() {
        URI uri = createDummyFileUriForGA();
        String text = "fn main(a) {a}";
        truffleAdapter.parse(text, LANGUAGE_ID, uri);
        truffleAdapter.didClose(uri);
    }

    
    // junit4 style
    // https://howtodoinjava.com/junit5/expected-exception-example/
    //@Rule
    //public ExpectedException expectedException = ExpectedException.none();


    // Wie kann ich mit Jupiter formulieren, dass eine bestimmte Expception erwartet wird?
    //TODO
    @Test(/*expected = UnknownLanguageException.class*/)
    public void unknownLanguage() throws Throwable {
        File testFile = File.createTempFile("truffle-lsp-test-file-unknown-lang-id", "");
        testFile.deleteOnExit();
        URI uri = testFile.toURI();

        try {
            Future<?> future = truffleAdapter.parse("", "unknown-lang-id", uri);
            future.get();
        } catch (InterruptedException | ExecutionException ex) {
            //Assertions.assertThrows(UnknownLanguageException.class, ex.getCause());
            
            // TODO Code unten durch sinnvolles Assertions ersetzen
            
            //ex.printStackTrace(System.err);
            // Execution exception 
            //ex.printStackTrace(System.err);
            // Ausgabe: Unknown language: unknown-lang-id. Known languages are: [ga]
            if (ex.getCause() instanceof org.graalvm.tools.lsp.exceptions.UnknownLanguageException){
                // Unknown language exception found!
                System.out.println("Unknown language exception found!");
            } else {
                throw ex.getCause();
            }
        }
    }

    @Test
    public void unknownlanguageIdButMIMETypeFound() throws InterruptedException, ExecutionException {
        URI uri = createDummyFileUriForGA();
        // dabei wird findLanguageInfo() vom TruffleAdapter aufgerufen mit der langId als Argument
        // dieses kann "ga" oder aber der Mimetype sein und wenn das nicht gefunden wird dann wird
        // die uri ausgewertet um die Sprache zu bestimmen.
        // das geht dann via Source.findLanguage
        // https://github.com/oracle/graal/blob/master/truffle/src/com.oracle.truffle.api/src/com/oracle/truffle/api/source/Source.java
        // das überschreibt unsere Sprache vermutlich nicht korrekt
        //FIXME
        Future<?> future = truffleAdapter.parse("fn main(a) {a}", "unknown-lang-id", uri);
        // Caused by: org.graalvm.tools.lsp.exceptions.UnknownLanguageException: Unknown language: unknown-lang-id. Known languages are: [ga]
        future.get();
    }

    @Test
    public void parseOK() throws InterruptedException, ExecutionException {
        URI uri = createDummyFileUriForGA();
        String text = "fn main(a) {a}"; // ga
        //String text = "function main() {return 3+3;}"; // simple language
        //FIXME hier fliege ich raus mit
        //source should only be set once
        // java.lang.AssertionError
	// at de.orat.math.graalvmlsp.ParsingTest.parseOK(ParsingTest.java:110)

        //try {
            Future<?> future = truffleAdapter.parse(text, LANGUAGE_ID, uri);
            // source should only be set once
            future.get();
        //} catch (/*Throwable ex*/ InterruptedException | ExecutionException ex) {
        //    System.err.println("ParseOK failed:");
        //    ex.printStackTrace(System.err);
        //    if (ex.getCause() != null){
        //        ex.getCause().printStackTrace(System.err);
        //    }
        //    throw ex; //ex.getCause();
        //} 
    }

    @Test
    public void parseEOF() throws InterruptedException {
        URI uri = createDummyFileUriForGA();
        String text = "fn main"; // ga
        //String text = "function main"; // simple language
        Future<?> future = truffleAdapter.parse(text, LANGUAGE_ID, uri);
        try {
            future.get();
			fail();
        } catch (ExecutionException ex) {
            Collection<PublishDiagnosticsParams> diagnosticParams = ((DiagnosticsNotification) ex.getCause()).getDiagnosticParamsCollection();
            assertEquals(1, diagnosticParams.size());
            PublishDiagnosticsParams param = diagnosticParams.iterator().next();
            assertEquals(uri.toString(), param.getUri());
            List<Diagnostic> diagnostics = param.getDiagnostics();
            //JSONObject["message"] not found.
            System.out.println("message="+diagnostics.get(0).getMessage());
            assertTrue(diagnostics.get(0).getMessage().contains("EOF"));
        }
    }

    @Test
    public void changeAndParse() throws InterruptedException, ExecutionException {
        TextDocumentSurrogate surrogate;
        URI uri = createDummyFileUriForGA();

        //String text = "function main() {return 3+3;}"; // simple language
        String text = "fn main(a) {a}"; // ga
        Future<?> futureParse = truffleAdapter.parse(text, LANGUAGE_ID, uri);
        futureParse.get();

        // Insert +4
        checkChange(uri, Range.create(Position.create(0, 13/*27*/), 
                                           Position.create(0, 13/*27*/)), "+a",
                        "fn main(a) {a+a}");

        // Delete
        // von 12 auf 13 geändert
		// Exception bereits hier.
        checkChange(uri, Range.create(Position.create(0, 13/*24*/), 
                                             Position.create(0, 14/*26*/)), "",
                        "fn main(a) {a}");

        // Replace
        checkChange(uri, Range.create(Position.create(0, 12), Position.create(0, 13)), "\n42\n}",
                        "fn main() {\n42\n}");

        // Insert at the end
        checkChange(uri, Range.create(Position.create(2, 1), Position.create(2, 1)), "\n",
                        "function main() {\n  return 42;\n}\n");
        checkChange(uri, Range.create(Position.create(3, 0), Position.create(3, 0)), " ",
                        "function main() {\n  return 42;\n}\n ");

        // Multiline replace
        checkChange(uri, Range.create(Position.create(0, 16), Position.create(3, 1)), "{return 1;}",
                        "function main() {return 1;}");

        // No change
        surrogate = checkChange(uri, Range.create(Position.create(0, 1), Position.create(0, 1)), "",
                        "function main() {return 1;}");
        // Replace to empty
        try {
            // java.util.concurrent.ExecutionException: org.graalvm.tools.lsp.exceptions.DiagnosticsNotification
            checkChange(uri, Range.create(Position.create(0, 0), 
                    Position.create(0, 14)), "", null);
            // hier fliege ich raus mit org.opentest4j.AssertionFailedError
            //FIXME
            fail();
        } catch (ExecutionException e) {
            // stacktrace beschaffen
            StringBuilder sb = new StringBuilder();
            var diag = ((DiagnosticsNotification) e.getCause()).getDiagnosticParamsCollection();
            diag.forEach(par -> par.getDiagnostics().forEach(d -> sb.append(d.getMessage())));
            String msg = sb.toString();
            System.out.println("Stack:"+msg);

            Collection<PublishDiagnosticsParams> diagnosticParamsCollection = 
                    ((DiagnosticsNotification) e.getCause()).getDiagnosticParamsCollection();
            assertEquals(1, diagnosticParamsCollection.size());
            PublishDiagnosticsParams diagnosticsParams = diagnosticParamsCollection.iterator().next();
            List<Diagnostic> diagnostics = diagnosticsParams.getDiagnostics();
            assertTrue(diagnostics.get(0).getMessage().contains("EOF"));
        } catch (Throwable f){
            System.out.println("failed:");
            f.printStackTrace(System.out);
        }
        assertEquals("", surrogate.getEditorText()); // surrogate==null
    }

    /**
     * 
     * @param uri
     * @param range
     * @param change
     * @param editorText target text
     * @return
     * @throws InterruptedException
     * @throws ExecutionException 
     */
    private TextDocumentSurrogate checkChange(URI uri, Range range, String change, String editorText) throws InterruptedException, ExecutionException {
        TextDocumentContentChangeEvent event = TextDocumentContentChangeEvent.create(change).setRange(range).setRangeLength(change.length());
        Future<TextDocumentSurrogate> future = truffleAdapter.processChangesAndParse(Arrays.asList(event), uri);
        try {
            TextDocumentSurrogate surrogate = future.get();
            // testweise Test: fn main(a) {a+a}=fn main(a) {a+a}
            System.out.println("Test: "+surrogate.getEditorText()+"="+editorText);
            assertEquals(editorText, surrogate.getEditorText());
            assertSame(surrogate.getEditorText(), surrogate.getEditorText());
			return surrogate;
		} catch (ExecutionException ex) {
			throw _Util.umwrapException(ex);
        } catch (Throwable t){
            // Caused by: org.graalvm.tools.lsp.exceptions.DiagnosticsNotification
            //TODO
            t.printStackTrace(System.out);
			throw t;
        }
		// return null;
    }

    @Test
    public void parseingWithSyntaxErrors() throws InterruptedException {
        URI uri = createDummyFileUriForGA();
        // String text = "function main() {return 3+;}"; // 26=";" 27="}
        String text = "fn main(a) {a+}";

        // in unserem bisherigen Code wurde immer nur context.parse() verwendet
        // d.h. truffleAdaper.parse() wurde bisher nicht getestet!
        // und genaus das funktioniert jetzt nicht!
        //FIXME
        Future<?> future = truffleAdapter.parse(text, LANGUAGE_ID, uri);
        try {
            future.get();
            fail();
        } catch (ExecutionException e) {
            DiagnosticsNotification diagnosticsNotification = getDiagnosticsNotification(e);
            Collection<PublishDiagnosticsParams> diagnosticParamsCollection = diagnosticsNotification.getDiagnosticParamsCollection();
            assertEquals(1, diagnosticParamsCollection.size());
            PublishDiagnosticsParams diagnosticsParams = diagnosticParamsCollection.iterator().next();
            assertEquals(1, diagnosticsParams.getDiagnostics().size());
            //FIXME hier fliege ich raus
            // Start==0 und End==0 
            //Warum?
            Range range = diagnosticsParams.getDiagnostics().get(0).getRange();
            System.out.println("Start = "+String.valueOf(range.getStart().getCharacter()));
            System.out.println("End = "+String.valueOf(range.getEnd().getCharacter()));
            assertTrue(rangeCheck(0, 14/*26*/, 
                                          0, 15/*27*/, 
                                          diagnosticsParams.getDiagnostics().get(0).getRange()));
        }
    }
}
