package de.orat.math.graalvmlsp;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/** Runs only when a packaged server JAR is explicitly supplied by the developer. */
class GraalVMLSPStarterIntegrationTest {

    @Test
    void packagedServerAcceptsLoopbackConnections() throws Exception {
        Assumptions.assumeTrue(Boolean.getBoolean("dsl4ga.lsp.integration"));
        String configuredJar = System.getProperty("dsl4ga.lsp.serverJar");
        Assumptions.assumeTrue(configuredJar != null, "Set dsl4ga.lsp.serverJar to the packaged server JAR.");

        Path serverJar = Path.of(configuredJar);
        Assumptions.assumeTrue(Files.isRegularFile(serverJar), "Configured server JAR does not exist.");
        int port = findFreeLoopbackPort();
        Process process = new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-jar", serverJar.toString(), Integer.toString(port))
                .redirectErrorStream(true)
                .start();

        try {
            long deadline = System.nanoTime() + 15_000_000_000L;
            while (System.nanoTime() < deadline && process.isAlive()) {
                try (Socket socket = new Socket("127.0.0.1", port)) {
                    return;
                } catch (java.io.IOException ex) {
                    Thread.sleep(50);
                }
            }
            throw new AssertionError("Packaged GA language server did not accept a connection.");
        } finally {
            process.destroyForcibly();
        }
    }

    private static int findFreeLoopbackPort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))) {
            return socket.getLocalPort();
        }
    }
}
