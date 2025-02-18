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

import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang.LANGUAGE_ID;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
// import junit.framework.TestCase;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.io.IOAccess;
import org.graalvm.tools.lsp.server.ContextAwareExecutor;
import org.graalvm.tools.lsp.exceptions.DiagnosticsNotification;
import org.graalvm.tools.lsp.instrument.EnvironmentProvider;
import org.graalvm.tools.lsp.server.LSPFileSystem;
import org.graalvm.tools.lsp.server.TruffleAdapter;
import org.graalvm.tools.lsp.server.types.Range;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Instrument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.jupiter.api.Test;

// https://github.com/oracle/graal/blob/master/tools/src/org.graalvm.tools.lsp.test/src/org/graalvm/tools/lsp/test/server/HoverTest.java
//@RunWith(Parameterized.class)
public abstract class TruffleLSPTest /*extends TestCase*/ {

    
    /*@Parameters(name = "useBytecode={0}")
    public static List<Boolean> getParameters() {
        return List.of(false, true);
    }

    @Parameter(0) public Boolean useBytecode;*/

	protected static final String PROG_OBJ = """
                                                fn abc() {
                                                test := 2
                                                x := 1
                                                x
                                                }

                                                fn main() {
                                                   x := abc()
                                                   x
                                                }""";

    protected static final String PROG_OBJ_NOT_CALLED = """
                                                        fn abc() {
                                                          obj := 1
                                                          obj
                                                        }
                                                        
                                                        fn notCalled() {
                                                          obj := abc()
                                                          obj
                                                        }
                                                        
                                                        fn main() {
                                                            x := abc()
                                                            x
                                                        }
                                                        """;
    
    protected Engine engine;
    protected TruffleAdapter truffleAdapter;
    protected Context context;

    //@Before
    @BeforeEach
    public void setUp() {
        engine = Engine.newBuilder(LANGUAGE_ID).allowExperimentalOptions(true).build();
        Instrument instrument = engine.getInstruments().get("lsp");
        EnvironmentProvider envProvider = instrument.lookup(EnvironmentProvider.class);

        // This class delegates LSP requests of LanguageServerImpl to specific 
        // implementations of AbstractRequestHandler.
        // It is responsible for wrapping requests into tasks for an instance of 
        // ContextAwareExecutor, so that these tasks are executed by a Thread which 
        // has entered a org.graalvm.polyglot.Context. 
        // https://github.com/oracle/graal/blob/master/tools/src/org.graalvm.tools.lsp/src/org/graalvm/tools/lsp/server/TruffleAdapter.java
        // mit dem Aufruf des Konstruktors werden nur die übergebenen Objekte gespeichert
        truffleAdapter = new TruffleAdapter(envProvider.getEnvironment(), true);

        Builder contextBuilder = Context.newBuilder();
        contextBuilder.allowAllAccess(true);
		// contextBuilder.allowIO(IOAccess.newBuilder().fileSystem(LSPFileSystem.
		//        newReadOnlyFileSystem(truffleAdapter)).build());
        contextBuilder.engine(engine);
        //contextBuilder.option("gl.UseBytecode", useBytecode.toString());
        context = contextBuilder.build();
        
        //context.enter();
        //WORKAROUND
        // context.initialize(LANGUAGE_ID);

        context.enter();

        //  Context context = Context.newBuilder().allowAllAccess(true)
        // .allowExperimentalOptions(true).option("lsp", "true").build();//) {
               
        
        ContextAwareExecutor executorWrapper = new ContextAwareExecutor() {

            @Override
            public <T> Future<T> executeWithDefaultContext(Callable<T> taskWithResult) {
                try {
                    return CompletableFuture.completedFuture(taskWithResult.call());
                } catch (Exception e) {
                    CompletableFuture<T> cf = new CompletableFuture<>();
                    cf.completeExceptionally(e);
                    return cf;
                }
            }

            @Override
            public <T> Future<T> executeWithNestedContext(Callable<T> taskWithResult, boolean cached) {
                try (Context newContext = contextBuilder.build()) {
                    newContext.enter();
                    newContext.initialize(LANGUAGE_ID);
                    try {
                        return CompletableFuture.completedFuture(taskWithResult.call());
                    } catch (Exception e) {
                        CompletableFuture<T> cf = new CompletableFuture<>();
                        cf.completeExceptionally(e);
                        return cf;
                    } finally {
                        newContext.leave();
                    }
                }
            }

            @Override
            public <T> Future<T> executeWithNestedContext(Callable<T> taskWithResult, int timeoutMillis, Callable<T> onTimeoutTask) {
                if (timeoutMillis <= 0) {
                    return executeWithNestedContext(taskWithResult, false);
                } else {
                    CompletableFuture<Future<T>> future = CompletableFuture.supplyAsync(() -> executeWithNestedContext(taskWithResult, false));
                    try {
                        return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException e) {
                        future.cancel(true);
                        try {
                            return CompletableFuture.completedFuture(onTimeoutTask.call());
                        } catch (Exception timeoutTaskException) {
                            CompletableFuture<T> cf = new CompletableFuture<>();
                            cf.completeExceptionally(timeoutTaskException);
                            return cf;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        CompletableFuture<T> cf = new CompletableFuture<>();
                        cf.completeExceptionally(e);
                        return cf;
                    }
                }
            }

            @Override
            public void shutdown() {
            }

            @Override
            public void resetContextCache() {
            }
        };

        truffleAdapter.register(envProvider.getEnvironment(), executorWrapper);
    }

    //@After@
    @AfterEach
    public void tearDown() {
        context.leave();
        context.close();
    }

    public URI createDummyFileUriForGA() {
        try {
            File dummy = File.createTempFile("truffle-lsp-test-file-", "."+LANGUAGE_ID);
            dummy.deleteOnExit();
            return dummy.getCanonicalFile().toPath().toUri();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected DiagnosticsNotification getDiagnosticsNotification(ExecutionException e) {
        if (e.getCause() instanceof DiagnosticsNotification) {
            return (DiagnosticsNotification) e.getCause();
        } else {
            throw new RuntimeException(e);
        }
    }

    protected boolean rangeCheck(Range orig, Range range) {
        return rangeCheck(orig.getStart().getLine(), orig.getStart().getCharacter(), orig.getEnd().getLine(), orig.getEnd().getCharacter(), range);
    }

    protected boolean rangeCheck(int startLine, int startColumn, int endLine, int endColumn, Range range) {
        return startLine == range.getStart().getLine() && startColumn == range.getStart().getCharacter() && endLine == range.getEnd().getLine() && endColumn == range.getEnd().getCharacter();
    }
}
