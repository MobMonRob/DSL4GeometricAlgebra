package de.orat.math.graalvmlsp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.shadowed.org.json.JSONArray;
import org.graalvm.shadowed.org.json.JSONObject;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Opt-in protocol checks for value-prefix completion and strict normal parsing.
 *
 * <p>From the parent project, run it with {@code mvn -o -q -pl DSL4GA_LSP -am
 * -Dtest=GaCompletionLspTest -Dsurefire.failIfNoSpecifiedTests=false
 * -Ddsl4ga.lsp.integration=true test}. The reactor includes current Truffle
 * classes without assembling the LSP JAR.</p>
 */
class GaCompletionLspTest {

    private static final String SOURCE = """
            #algebra pga

            fn main(position) {
                c = position
                c
            }
            """;
    private static final int ASSIGNMENT_LINE = 3;
    private static final int VALUE_START = 8;

    @TempDir
    Path directory;

    @Test
    void completesValuePrefixAfterIncrementalEdit() throws Exception {
        requireOptIn();
        Path file = directory.resolve("changed.ga");
        Files.writeString(file, SOURCE);
        String uri = file.toUri().toString();

        try (LspTestClient client = LspTestClient.start()) {
            client.initialize(directory.toUri().toString());
            client.didOpen(uri, SOURCE);
            client.didChange(uri, 2, ASSIGNMENT_LINE, VALUE_START,
                    VALUE_START + "position".length(), "p");
            assertPositionCompletion(client, uri);
        }
    }

    @Test
    void completesValuePrefixOnOpen() throws Exception {
        requireOptIn();
        String source = SOURCE.replace("c = position", "c = p");
        Path file = directory.resolve("opened.ga");
        Files.writeString(file, source);
        String uri = file.toUri().toString();

        try (LspTestClient client = LspTestClient.start()) {
            client.initialize(directory.toUri().toString());
            client.didOpen(uri, source);
            assertPositionCompletion(client, uri);
        }
    }

    @Test
    void ordinaryParsingStillRejectsUnknownValue() throws Exception {
        requireOptIn();
        String source = SOURCE.replace("c = position", "c = p");
        try (Context context = Context.newBuilder("ga").allowAllAccess(true).build()) {
            PolyglotException exception = assertThrows(PolyglotException.class,
                    () -> context.parse(Source.newBuilder("ga", source, "strict.ga").build()));
            assertTrue(exception.getMessage().contains("Variable or function \"p\""),
                    exception::getMessage);
        }
    }

    private static void requireOptIn() {
        Assumptions.assumeTrue(Boolean.getBoolean("dsl4ga.lsp.integration"),
                "Enable with -Ddsl4ga.lsp.integration=true.");
    }

    private static void assertPositionCompletion(LspTestClient client, String uri) throws Exception {
        JSONObject response = client.completion(uri, ASSIGNMENT_LINE, VALUE_START + 1);
        assertTrue(!response.has("error"), () -> "LSP error: " + response + "\n" + client.received());

        Object result = response.opt("result");
        JSONArray items = result instanceof JSONArray array ? array
                : result instanceof JSONObject object ? object.optJSONArray("items") : null;
        List<String> labels = new ArrayList<>();
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                labels.add(items.getJSONObject(i).getString("label"));
            }
        }
        assertTrue(labels.contains("position"),
                () -> "Expected position for 'c = p', got " + response + "\n" + client.received());
    }
}
