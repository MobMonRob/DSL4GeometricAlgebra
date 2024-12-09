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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.graalvm.tools.lsp.exceptions.DiagnosticsNotification;
import org.graalvm.tools.lsp.server.types.CompletionItem;
import org.graalvm.tools.lsp.server.types.CompletionItemKind;
import org.graalvm.tools.lsp.server.types.CompletionList;
import org.graalvm.tools.lsp.server.types.CompletionOptions;
import org.graalvm.tools.lsp.server.types.Position;
import org.graalvm.tools.lsp.server.types.PublishDiagnosticsParams;
import org.graalvm.tools.lsp.server.types.Range;
import org.graalvm.tools.lsp.server.types.ServerCapabilities;
import org.graalvm.tools.lsp.server.types.TextDocumentContentChangeEvent;
import org.junit.jupiter.api.Test;

import com.oracle.truffle.api.nodes.NodeInfo;

import static de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Program.LANGUAGE_ID;
//import com.oracle.truffle.sl.SLLanguage;
//import com.oracle.truffle.sl.builtins.SLHelloEqualsWorldBuiltin;

public class CompletionTest extends TruffleLSPTest {

    @Test
    public void globalsAndLocals() throws InterruptedException, ExecutionException {
        URI uri = createDummyFileUriForGA();
        String text = """
                fn abc(p1, p2) {
                  varA := p1 + p2
                  varB := p1 p2
                  varA
                }
                fn main() {
                    3+3
                }
                """;
     
        // FIXME empty blocks are not allowed
        // damit fliege ich hier raus
        Future<?> future = truffleAdapter.parse(text, LANGUAGE_ID, uri);
        future.get();

        int numberOfGlobalsItems = -1;

        numberOfGlobalsItems = checkGlobalsAndLocals(uri, 0, 0, numberOfGlobalsItems, "p1", false);
        checkGlobalsAndLocals(uri, 1, 12, numberOfGlobalsItems, "p1", false);
        checkGlobalsAndLocals(uri, 5, 0, numberOfGlobalsItems + 3, "p1", true, "p2", true, "varA", true, "varB", false);
        checkGlobalsAndLocals(uri, 7, 2, numberOfGlobalsItems + 4, "p1", true, "varA", true, "varB", true);
        checkGlobalsAndLocals(uri, 9, 0, numberOfGlobalsItems, "p1", false, "varA", false, "varB", false);
        // if line is out of range -> show nothing
        checkEmpty(uri, 100, 0);
        // if column is out of range -> show nothing
        checkEmpty(uri, 8, 5);
    }

    private int checkGlobalsAndLocals(URI uri, int line, int column, int numberOfGlobalsItems, Object... vars) throws InterruptedException, ExecutionException {
        Future<CompletionList> futureCompletions = 
                truffleAdapter.completion(uri, line, column, null);
        CompletionList completionList = futureCompletions.get();
        assertFalse(completionList.isIncomplete());

        List<CompletionItem> items = completionList.getItems();
        assertFalse(items.isEmpty());

        // testweise auskommentiert
        //NodeInfo nodeInfo = SLLanguage.lookupNodeInfo(GAHelloEqualsWorldBuiltin.class);
        //assertNotNull(nodeInfo);

        //String shortName = nodeInfo.shortName();
        //assertTrue("Built-in function " + shortName + " not found.", items.stream().anyMatch(item -> item.getLabel().startsWith(shortName)));
        for (int i = 0; i < vars.length; i += 2) {
            String var = (String) vars[i];
            boolean present = (boolean) vars[i + 1];
            if (present) {
		assertTrue(items.stream().anyMatch(item -> item.getLabel().startsWith(var)), var + " should be found in function scope");
            } else {
		assertTrue(items.stream().noneMatch(item -> item.getLabel().startsWith(var)), var + " should not be found in main-function scope");
            }
        }
        if (numberOfGlobalsItems != -1) {
            assertEquals(numberOfGlobalsItems, items.size());
        }
        return items.size();
    }

    private void checkEmpty(URI uri, int line, int column) throws InterruptedException, ExecutionException {
        Future<CompletionList> futureCompletions = truffleAdapter.completion(uri, line, column, null);
        CompletionList completionList = futureCompletions.get();
        assertFalse(completionList.isIncomplete());

        List<CompletionItem> items = completionList.getItems();
        assertTrue(items.isEmpty());
    }

    @Test
    public void objectPropertyCompletionLocalFile() throws InterruptedException, ExecutionException {
        URI uri = createDummyFileUriForGA();
        Future<?> future = truffleAdapter.parse(PROG_OBJ_NOT_CALLED, LANGUAGE_ID, uri);
        future.get();

        // Completing provider started bei einem "." - das macht bei unserer Sprache
        // aber keinen Sinn
        setTriggerCharacters();
        
        // Zeichen nach dem "x" durch einen Punkte ersetzen
        // FIXME hier fliege ich raus
        replace(uri, Range.create(Position.create(2, 12), Position.create(2, 12)), ".", "missing IDENTIFIER");
        Future<CompletionList> futureC = truffleAdapter.completion(uri, 2, 13, null);
        CompletionList completionList = futureC.get();
        assertEquals(1, completionList.getItems().size());
        CompletionItem item = completionList.getItems().get(0);
        assertEquals("p", item.getLabel());
        assertEquals("Number", item.getDetail());
        assertEquals(CompletionItemKind.Property, item.getKind());
        replace(uri, Range.create(Position.create(2, 12), Position.create(2, 13)), "", null);

        replace(uri, Range.create(Position.create(12, 7), Position.create(12, 7)), ".", "missing IDENTIFIER");
        futureC = truffleAdapter.completion(uri, 12, 8, null);
        try {
            futureC.get();
            fail();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof DiagnosticsNotification);
        }
    }

    /**
     * 
     * @param uri
     * @param range to replace
     * @param replacement replace with the value of this argument
     * @param diagMessage
     * @throws InterruptedException 
     */
    private void replace(URI uri, Range range, String replacement, String diagMessage) throws InterruptedException {
        TextDocumentContentChangeEvent event = TextDocumentContentChangeEvent.create(replacement).setRange(range).setRangeLength(replacement.length());
        Future<?> future = truffleAdapter.processChangesAndParse(Arrays.asList(event), uri);
        try {
            future.get();
            
            // sicherstellen, dass diagMessage != null and not empty
            assertNull(diagMessage);
		} catch (ExecutionException e) {
			assertFalse(diagMessage.isEmpty(), diagMessage);
                        
            Collection<PublishDiagnosticsParams> diagnosticParamsCollection = ((DiagnosticsNotification) e.getCause()).getDiagnosticParamsCollection();
            assertEquals(1, diagnosticParamsCollection.size());
            // hier fliege ich raus, da das Diagnostic object kein json object mit prop message hat
            // bei obectPropertyCompletionLocal()
            //FIXME
            String message = diagnosticParamsCollection.iterator().next().getDiagnostics().get(0).getMessage();
			assertTrue(message.contains(diagMessage), message);
        }
    }

    @Test
    public void objectPropertyCompletionViaCoverageData() throws InterruptedException, ExecutionException {
        //assumeFalse("Bytecode DSL interpreter cannot identify locals associated with a node (GR-59649)", useBytecode);
        URI uri = createDummyFileUriForGA();
        Future<?> future = truffleAdapter.parse(PROG_OBJ_NOT_CALLED, LANGUAGE_ID, uri);
        future.get();

        //FIXME
        // hier fliege ich raus
        Future<Boolean> futureCoverage = truffleAdapter.runCoverageAnalysis(uri);
        futureCoverage.get();

        // Completing provider started bei einem "." - das macht bei unserer Sprache
        // aber keinen Sinn
        setTriggerCharacters();
        
        replace(uri, Range.create(Position.create(8, 12), Position.create(8, 12)), ".", "missing IDENTIFIER");
        Future<CompletionList> futureC = truffleAdapter.completion(uri, 8, 13, null);
        CompletionList completionList = futureC.get();
        assertEquals(1, completionList.getItems().size());
        CompletionItem item = completionList.getItems().get(0);
        assertEquals("p", item.getLabel());
        assertEquals("Number", item.getDetail());
        assertEquals(CompletionItemKind.Property, item.getKind());
    }

    private void setTriggerCharacters() {
        ServerCapabilities capabilities = ServerCapabilities.create();
        CompletionOptions completionProvider = CompletionOptions.create();
        completionProvider.setTriggerCharacters(Collections.singletonList("."));
        capabilities.setCompletionProvider(completionProvider);
        truffleAdapter.setServerCapabilities(LANGUAGE_ID, capabilities);
    }
}
