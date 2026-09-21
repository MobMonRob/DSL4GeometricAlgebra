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

    private GraalVMLSPStarter() {
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected exactly one LSP port argument.");
        }

        int port = Integer.parseInt(args[0]);
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Invalid LSP port: " + port);
        }

        // The NetBeans module retains this output for startup diagnostics and
        // logs it only at FINE level during normal operation.
        System.err.println("[GA-LSP] Java: " + System.getProperty("java.version"));
        System.err.println("[GA-LSP] java.home: " + System.getProperty("java.home"));

        try (Context context = Context.newBuilder(GeomAlgeLang.LANGUAGE_ID)
                .allowAllAccess(true)
                .allowExperimentalOptions(true)
                .option("lsp", "127.0.0.1:" + port)
                .build()) {
            context.initialize(GeomAlgeLang.LANGUAGE_ID);
            context.getEngine().getLanguages().forEach((id, language)
                    -> System.err.println("[GA-LSP] Language " + id + ": " + language.getName()));
            context.getEngine().getInstruments().forEach((id, instrument)
                    -> System.err.println("[GA-LSP] Instrument " + id + ": " + instrument.getName()));

            // NetBeans owns this process and terminates it when the LSP binding closes.
            new CountDownLatch(1).await();
        }
    }
}
