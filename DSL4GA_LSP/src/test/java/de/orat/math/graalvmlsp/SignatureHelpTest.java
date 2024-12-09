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

//import static org.junit.Assert.assertEquals;

import static de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Program.LANGUAGE_ID;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.graalvm.tools.lsp.server.types.SignatureHelp;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import org.junit.Test;
import org.junit.jupiter.api.Test;

public class SignatureHelpTest extends TruffleLSPTest {

    @Test
    public void gotoDefinitionForFunctions() throws InterruptedException, ExecutionException {
        URI uri = createDummyFileUriForGA();
        Future<?> futureOpen = truffleAdapter.parse(PROG_OBJ, LANGUAGE_ID, uri);
        futureOpen.get();

        // usage of function abc() is given and the functiondef should be found
        Future<SignatureHelp> futureSignatureHelp = truffleAdapter.signatureHelp(uri, 7, 11);
        SignatureHelp signatureHelp = futureSignatureHelp.get();
        // TODO: GA is not supporting GET_SIGNATURE message yet
        //how to implement this feature?
        //TODO
        assertEquals(0, signatureHelp.getSignatures().size());
    }
}
