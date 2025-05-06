package de.dhbw.rahmlab.dsl4ga.impl.truffle;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Instrument;
import org.graalvm.polyglot.io.IOAccess;
import org.graalvm.tools.lsp.exceptions.DiagnosticsNotification;
import org.graalvm.tools.lsp.instrument.EnvironmentProvider;
import org.graalvm.tools.lsp.server.ContextAwareExecutor;
import org.graalvm.tools.lsp.server.LSPFileSystem;
import org.graalvm.tools.lsp.server.TruffleAdapter;
import org.graalvm.tools.lsp.server.types.Hover;
import org.graalvm.tools.lsp.server.types.Range;

public class _GaHoverTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		System.setProperty("truffle.instrumentation.trace", "true");

		var hover = new _GaHoverTest();
		hover.setUp();
		hover.hoverExtreme();
		hover.tearDown();
	}

	public void hoverExtreme() throws InterruptedException, ExecutionException {
		URI uri = createDummyFileUriForGA();

		Future<?> futureOpen = truffleAdapter.parse(PROG_OBJ, GeomAlgeLang.LANGUAGE_ID, uri);
		futureOpen.get();

		int i = 0;
		for (int column = 0; column < 11; ++column) {
			for (int line = 0; line < 13; ++line) {
				Future<Hover> future = truffleAdapter.hover(uri, line, column);
				Hover hover = future.get();
				var hoverRange = hover.getRange();
				if (hoverRange != null) {
					System.out.println("hoooover" + i++);
					return;
				}
			}
		}
	}

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
	// @BeforeEach
	public void setUp() {
		engine = Engine.newBuilder(GeomAlgeLang.LANGUAGE_ID).allowExperimentalOptions(true).build();
		Instrument instrument = engine.getInstruments().get("lsp");

		EnvironmentProvider envProvider = instrument.lookup(EnvironmentProvider.class);
		var environment = envProvider.getEnvironment();

		// This class delegates LSP requests of LanguageServerImpl to specific
		// implementations of AbstractRequestHandler.
		// It is responsible for wrapping requests into tasks for an instance of
		// ContextAwareExecutor, so that these tasks are executed by a Thread which
		// has entered a org.graalvm.polyglot.Context.
		// https://github.com/oracle/graal/blob/master/tools/src/org.graalvm.tools.lsp/src/org/graalvm/tools/lsp/server/TruffleAdapter.java
		// mit dem Aufruf des Konstruktors werden nur die übergebenen Objekte gespeichert
		truffleAdapter = new TruffleAdapter(environment, true);

		Context.Builder contextBuilder = Context.newBuilder();
		contextBuilder.allowAllAccess(true);
		contextBuilder.allowIO(IOAccess.newBuilder().fileSystem(LSPFileSystem.newReadOnlyFileSystem(truffleAdapter)).build());
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
					newContext.initialize(GeomAlgeLang.LANGUAGE_ID);
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
	// @AfterEach
	public void tearDown() {
		context.leave();
		context.close();
	}

	public URI createDummyFileUriForGA() {
		try {
			File dummy = File.createTempFile("truffle-lsp-test-file-", "." + GeomAlgeLang.LANGUAGE_ID);
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
