package de.orat.math.graalvmlsp;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.graalvm.polyglot.Context;
import org.graalvm.shadowed.org.json.JSONArray;
import org.graalvm.shadowed.org.json.JSONObject;

/** A small, reusable client for testing the actual Graal LSP socket protocol. */
final class LspTestClient implements AutoCloseable {

    private static final int TIMEOUT_MILLIS = 10_000;
    private final Context context;
    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;
    private final List<String> received = new ArrayList<>();
    private int nextId = 1;

    private LspTestClient(Context context, Socket socket) throws IOException {
        this.context = context;
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
    }

    static LspTestClient start() throws Exception {
        int port;
        try (ServerSocket reservation = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))) {
            port = reservation.getLocalPort();
        }

        Context context = GraalVMLSPStarter.createContext(port);
        try {
            context.initialize(GeomAlgeLang.LANGUAGE_ID);
            long deadline = System.nanoTime() + TIMEOUT_MILLIS * 1_000_000L;
            IOException lastFailure = null;
            while (System.nanoTime() < deadline) {
                Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress("127.0.0.1", port), 250);
                    socket.setSoTimeout(TIMEOUT_MILLIS);
                    return new LspTestClient(context, socket);
                } catch (IOException ex) {
                    lastFailure = ex;
                    socket.close();
                    Thread.sleep(50);
                }
            }
            throw new IOException("The GA LSP did not listen on port " + port, lastFailure);
        } catch (Exception | Error ex) {
            context.close();
            throw ex;
        }
    }

    void initialize(String rootUri) throws IOException {
        JSONObject params = new JSONObject()
                .put("processId", JSONObject.NULL)
                .put("rootUri", rootUri)
                .put("capabilities", new JSONObject());
        JSONObject response = request("initialize", params);
        if (response.has("error")) {
            throw new IOException("LSP initialize failed: " + response + "\n" + received());
        }
        notify("initialized", new JSONObject());
    }

    void didOpen(String uri, String text) throws IOException {
        notify("textDocument/didOpen", new JSONObject().put("textDocument", new JSONObject()
                .put("uri", uri)
                .put("languageId", GeomAlgeLang.LANGUAGE_ID)
                .put("version", 1)
                .put("text", text)));
    }

    void didChange(String uri, int version, int line, int start, int end, String replacement)
            throws IOException {
        JSONObject range = new JSONObject()
                .put("start", position(line, start))
                .put("end", position(line, end));
        notify("textDocument/didChange", new JSONObject()
                .put("textDocument", new JSONObject().put("uri", uri).put("version", version))
                .put("contentChanges", new JSONArray().put(new JSONObject()
                        .put("range", range)
                        .put("text", replacement))));
    }

    JSONObject completion(String uri, int line, int character) throws IOException {
        return request("textDocument/completion", new JSONObject()
                .put("textDocument", new JSONObject().put("uri", uri))
                .put("position", position(line, character)));
    }

    String received() {
        return String.join("\n", received);
    }

    private static JSONObject position(int line, int character) {
        return new JSONObject().put("line", line).put("character", character);
    }

    JSONObject request(String method, JSONObject params) throws IOException {
        int id = nextId++;
        send(new JSONObject().put("jsonrpc", "2.0").put("id", id)
                .put("method", method).put("params", params));
        long deadline = System.nanoTime() + TIMEOUT_MILLIS * 1_000_000L;
        while (System.nanoTime() < deadline) {
            socket.setSoTimeout(Math.max(1, (int) ((deadline - System.nanoTime()) / 1_000_000L)));
            try {
                JSONObject message = read();
                if (received.size() == 30) {
                    received.remove(0);
                }
                received.add(message.toString());
                if (message.optInt("id", -1) == id) {
                    return message;
                }
            } catch (SocketTimeoutException ex) {
                break;
            }
        }
        throw new IOException("Timed out waiting for " + method + ". Received:\n" + received());
    }

    void notify(String method, JSONObject params) throws IOException {
        send(new JSONObject().put("jsonrpc", "2.0").put("method", method).put("params", params));
    }

    private void send(JSONObject message) throws IOException {
        byte[] body = message.toString().getBytes(StandardCharsets.UTF_8);
        output.write(("Content-Length: " + body.length + "\r\n\r\n")
                .getBytes(StandardCharsets.US_ASCII));
        output.write(body);
        output.flush();
    }

    private JSONObject read() throws IOException {
        int contentLength = -1;
        String header;
        while (!(header = readHeaderLine()).isEmpty()) {
            if (header.regionMatches(true, 0, "Content-Length:", 0, 15)) {
                contentLength = Integer.parseInt(header.substring(15).trim());
            }
        }
        if (contentLength < 0 || contentLength > 10_000_000) {
            throw new IOException("Invalid LSP Content-Length: " + contentLength);
        }
        byte[] body = input.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new IOException("Unexpected end of LSP message.");
        }
        return new JSONObject(new String(body, StandardCharsets.UTF_8));
    }

    private String readHeaderLine() throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int value;
        while ((value = input.read()) != -1) {
            if (value == '\n') {
                byte[] bytes = line.toByteArray();
                int end = bytes.length > 0 && bytes[bytes.length - 1] == '\r'
                        ? bytes.length - 1 : bytes.length;
                return new String(bytes, 0, end, StandardCharsets.US_ASCII);
            }
            line.write(value);
            if (line.size() > 8192) {
                throw new IOException("LSP header line is too long.");
            }
        }
        throw new IOException("LSP connection closed while reading a header.");
    }

    @Override
    public void close() throws Exception {
        try {
            socket.close();
        } finally {
            context.close();
        }
    }
}
