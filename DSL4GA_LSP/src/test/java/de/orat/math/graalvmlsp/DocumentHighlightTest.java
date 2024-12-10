/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
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
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.graalvm.tools.lsp.server.types.DocumentHighlight;
import org.graalvm.tools.lsp.server.types.DocumentHighlightKind;
import org.graalvm.tools.lsp.server.types.Position;
import org.graalvm.tools.lsp.server.types.Range;

public class DocumentHighlightTest extends TruffleLSPTest {

    @Test
    public void variablesHighlightTest() throws InterruptedException, ExecutionException {
        // testweise auskommentiert
        //assumeFalse("Bytecode DSL interpreter cannot identify locals associated with a node (GR-59649)", useBytecode);
        URI uri = createDummyFileUriForGA();
        Future<?> futureOpen = truffleAdapter.parse(PROG_OBJ_NOT_CALLED, LANGUAGE_ID, uri);
        futureOpen.get();

        // in main() das erste x und dann eine Zeile tiefer das x das als return value da steht
        // zwei highlighted Stellen mit x sind zu erwarten, es werden aber keine gefunden
        // das erste X ist WRITE da wird geschrieben, das zweite vom Type READ da wirds nur ausgelesen als Rückgabewert
        //FIXME es werden 0 highlighted Stellen gefunden
        checkHighlight(uri, 11, 4, DocumentHighlight.create(Range.create(Position.create(11, 4), Position.create(11, 5)), DocumentHighlightKind.Write),
                        DocumentHighlight.create(Range.create(Position.create(12, 14), Position.create(12, 5)), DocumentHighlightKind.Read));
        
        //for (int column = 2; column <= 4; column++) {
        //    checkHighlight(uri, 6, column, DocumentHighlight.create(Range.create(Position.create(6, 2), Position.create(6, 5)), DocumentHighlightKind.Write),
        //                    DocumentHighlight.create(Range.create(Position.create(7, 2), Position.create(7, 5)), DocumentHighlightKind.Read),
        //                    DocumentHighlight.create(Range.create(Position.create(8, 9), Position.create(8, 12)), DocumentHighlightKind.Read));
        //}
    }

    private void checkHighlight(URI uri, int line, int column, DocumentHighlight... verifiedHighlights) throws InterruptedException, ExecutionException {
        Future<List<? extends DocumentHighlight>> future = truffleAdapter.documentHighlight(uri, line, column);
        List<? extends DocumentHighlight> highlights = future.get();
        // FIXME 0 but expected 2
        assertEquals(verifiedHighlights.length, highlights.size());
        for (int i = 0; i < verifiedHighlights.length; i++) {
            DocumentHighlight vh = verifiedHighlights[i];
            DocumentHighlight h = highlights.get(i);
			assertEquals(vh.getKind(), h.getKind(), Integer.toString(i));
			assertTrue(rangeCheck(vh.getRange(), h.getRange()), Integer.toString(i));
        }
    }
}
