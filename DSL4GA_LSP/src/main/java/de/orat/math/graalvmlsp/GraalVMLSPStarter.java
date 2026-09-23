package de.orat.math.graalvmlsp;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import java.util.concurrent.CountDownLatch;
import org.graalvm.polyglot.Context;

/**
 * Starts the GraalVM language server in its own JVM.
 *
 * <p>The NetBeans module connects to the loopback socket after this process has
 * started. Keeping GraalVM here avoids mixing its service providers with the
 * NetBeans module classloaders.</p>
 */
public final class GraalVMLSPStarter {

    static final String EDITOR_ANALYSIS_PROPERTY = "polyglot.ga.editorAnalysis";

    private GraalVMLSPStarter() {
    }

    public static void main(String[] args) throws InterruptedException {
        int port = parsePort(args);

		// The NetBeans module retains this output for startup diagnostics and logs it.
        System.out.println("[GA-LSP] Java: " + System.getProperty("java.version"));
		System.out.println("[GA-LSP] java.home: " + System.getProperty("java.home"));

        try (Context context = createContext(port)) {
            context.initialize(GeomAlgeLang.LANGUAGE_ID);
            context.getEngine().getLanguages().forEach((id, language)
				-> System.out.println("[GA-LSP] Language " + id + ": " + language.getName()));
            context.getEngine().getInstruments().forEach((id, instrument)
				-> System.out.println("[GA-LSP] Instrument " + id + ": " + instrument.getName()));

            // NetBeans owns this process and terminates it when the LSP binding closes.
            new CountDownLatch(1).await();
        }
    }

    /** Shared by the standalone server and direct LSP protocol tests. */
    static Context createContext(int port) {
        // Graal LSP creates its own engine and context; Context.Builder options
        // below do not reach it. A JVM property is read by both engines in this
        // dedicated server process.
        System.setProperty(EDITOR_ANALYSIS_PROPERTY, "true");
        return Context.newBuilder(GeomAlgeLang.LANGUAGE_ID)
                .allowAllAccess(true)
                .allowExperimentalOptions(true)
                .option("lsp", "127.0.0.1:" + port)
                .build();
    }

    static int parsePort(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected exactly one LSP port argument.");
        }

        final int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid LSP port: " + args[0], ex);
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Invalid LSP port: " + port);
        }
        return port;
    }
}
