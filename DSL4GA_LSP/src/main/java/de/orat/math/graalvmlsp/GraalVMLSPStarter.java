package de.orat.math.graalvmlsp;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingServiceProvider;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graalvm.polyglot.Context;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GraalVMLSPStarter {

    //TODO
    // die Argumente auslesen und die zu verwendende language also z.B. "ga"
    // aber dann muss die Sprache auch hier als jar mit als dependency verfügbar gemacht werden
    // auslesen und weiterreichen
    // port auslesen und default port überschreiben
    // --stdio um Socket auf stdin/out umzuleiten
    // derzeit ist default Umleitung nach std
    public static void main(String[] args) {
        
        // socket reconnect
        //TODO
        // https://stackoverflow.com/questions/18616740/how-to-make-client-socket-wait-for-server-socket
        int port = 8123;
        int MAX_CONNECTIONS = 10;
        int reconnections;
        
        OutputStream lspServerOut = null;
        try {
            String logFileName = "graalvmLSP.log";
            lspServerOut = new FileOutputStream(logFileName);
            try {
                // .out(System.err/*lspServerOut*/)
                Context context = Context.newBuilder().allowAllAccess(true)
                        .allowExperimentalOptions(true).option("lsp", "true").out(/*System.err*/lspServerOut).build();//) {
                // https://www.graalvm.org/latest/reference-manual/embed-languages/#dependency-setup
                
                //WORKAROUND aus package de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Program
                ParsingServiceProvider.setParsingService(ParsingService.instance());

                // https://github.com/oracle/graal/blob/master/tools/src/org.graalvm.tools.lsp.test/src/org/graalvm/tools/lsp/test/server/TruffleLSPTest.java
                // da läßt sich vermutlich ableiten wie der LSP verwendet werden kann ohne einen Socket-Server zu starten
                // dann könnte ich den LSP möglicherweise doch innerhalb der IDE starten?
                
                // hier muss ich vermutlich nicht enter aufrufen da ich ja von context
                // keine excutes aufrufe, oder? muss ich das auch dann aufrufen, wenn über
                // den LSP executes kommen?
                //context.enter();
                
                //TODO
                // wie kann ich den port setzen?
                
                System.err.println("Context created!");
                //[Graal LSP] Starting server and listening on localhost/127.0.0.1:8123
                /*Set<String> languages = context.getEngine().getLanguages().keySet();
                for (String id : languages) {
                System.out.println("found \""+id+"\"!");
                }
                if (languages.isEmpty()){
                System.out.println("No languages found!");
                }*/
                // bleibt hier hängen - warum?
                // a non-daemon thread is started, this will prevent the program from exiting.
                // Steuerung-C
                // Waiting for the language client to disconnect...
                
                // abwarten, bis der LSP-Server gestartet wurde
                //TODO
                // [Graal LSP] Starting server and listening on localhost/127.0.0.1:8123
                // darauf warten, dass obige Textzeile ausgegeben wurde
                
                // gibts eine Möglichkeit via context herauszufinden ob der SocketServer
                // bereits verfügbar ist?
                //TODO
                
                
                //WORKAROUND
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex){}
                
                
                //System.out.println(InetAddress.getByName("localhost").getHostAddress());
                
                // Redirection to stdio
                if (args.length > 1 && args[1].equals("--stdio")){
                    System.out.println("Redirection to stdio...");
                    try (Socket socket = new Socket(InetAddress.getByName("localhost"), port)) {
                    //try (Socket socket = new Socket()) {
                        //socket.connect(new InetSocketAddress("localhost", port),3000); // timeout in [ms]
                        System.out.println("Socket for redirection to stdio created!");
                        // [Graal LSP] Client connected on /127.0.0.1:48294

                        OutputStream os = socket.getOutputStream();
                        InputStream is = socket.getInputStream();
                        
                        // zu testzwecken alles loggen
                        os = new CopyOutput(os, lspServerOut);
                        is = new CopyInput(is, lspServerOut);

                        //TODO
                        // InputStream/Printstream sind das auch InputStreamReader, PrintWriter?
                        // Buffered Versionen verwenden?
                        
                        // Socket mit System.in/out verbinden
                        System.setOut(new PrintStream(os, true));
                        System.setIn(is);
                    } catch (ConnectException e){
                        System.err.println("Error while connecting: "+e.getMessage());
                        tryToReconnect();
                    } catch (SocketTimeoutException e){
                        System.err.println("Connection: "+e.getMessage());
                        tryToReconnect();
                    } catch (UnknownHostException uhe) {
                        System.err.println("UnknownHostException:");
                        uhe.printStackTrace(System.err);
                    } catch (IOException ioe) {
                        System.err.println("IOException: "+ioe.getMessage());
                        ioe.printStackTrace(System.err);
                    }
                } else {
                    System.out.println("No redirection to stdio!");
                }
            } catch (Exception e){
                System.err.println("Exception:");
                e.printStackTrace(System.err);
                System.err.println(e.getMessage());
            }
            
        } catch (FileNotFoundException ex){
            Logger.getLogger(GraalVMLSPStarter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                lspServerOut.close();
            } catch (IOException ex) {
                Logger.getLogger(GraalVMLSPStarter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // um das hart zu beended:
        // System.exit();
    }
    
    private static void tryToReconnect(){
        //socket.disconnect();
    }
    
    private static class CopyInput extends InputStream {

        private final InputStream delegate;
        private final OutputStream log;

        public CopyInput(InputStream delegate, OutputStream log) {
            this.delegate = delegate;
            this.log = log;
        }

        @Override
        public int read() throws IOException {
            int read = delegate.read();
            log.write(read);
            return read;
        }
    }

    private static class CopyOutput extends OutputStream {

        private final OutputStream delegate;
        private final OutputStream log;

        public CopyOutput(OutputStream delegate, OutputStream log) {
            this.delegate = delegate;
            this.log = log;
        }

        @Override
        public void write(int b) throws IOException {
            delegate.write(b);
            log.write(b);
            log.flush();
        }

        @Override
        public void flush() throws IOException {
            delegate.flush();
            log.flush();
        }
    }
}
