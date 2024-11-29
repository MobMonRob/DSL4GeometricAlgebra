package de.orat.math.graalvmlsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GraalVMLSPStarterTest {
    
    BufferedReader in;
    PrintWriter out;
    Socket clientSocket;
    
    public GraalVMLSPStarterTest() {
        
        /*try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(GraalVMLSPStarterTest.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void test() {
        int port = 8123;
        try {
           
           File workingDirectory = new File("/home/oliver/JAVA/PROJECTS/DSL4GeometricAlgebra/target");
           ProcessBuilder pb = new ProcessBuilder("./ga2")
                   .directory(workingDirectory); 
           // error des subproess auf die err-Ausgabe von NB umleiten
           pb.redirectError(ProcessBuilder.Redirect.INHERIT);
           pb.redirectOutput(ProcessBuilder.Redirect.
                   appendTo(new File(workingDirectory, "server.log")));
           Process p = pb.start();
           
           //WORKAROUND
           try {
                Thread.sleep(3000);
           } catch (InterruptedException ex){}
                
           try (Socket socket = new Socket(InetAddress.getByName("localhost"), port)) {
                System.out.println("Socket created!");

                // zu testzwecken alles loggen
                out = new PrintWriter(new CopyOutput(socket.getOutputStream(), System.err));
                in = new BufferedReader(new InputStreamReader(new CopyInput(socket.getInputStream(), System.err)));

                processMessages();
                
            } catch (ConnectException e){
                System.err.println("Error while connecting: "+e.getMessage());
            } catch (SocketTimeoutException e){
                System.err.println("Connection: "+e.getMessage());
            } catch (UnknownHostException uhe) {
                System.err.println("UnknownHostException:");
                uhe.printStackTrace(System.err);
            } catch (IOException ioe) {
                System.err.println("IOException: "+ioe.getMessage());
                ioe.printStackTrace(System.err);
            } catch (Exception e){
                System.err.println("Exception: "+e.getMessage());
                e.printStackTrace(System.err);
            }
       } catch (IOException ex) {
           ex.printStackTrace(System.err);
       }
    }
    
  /**
   * Processes incoming messages from the server
   */
  private void processMessages() throws Exception {
    // All messages start with a header
    String header;
    while ((header = in.readLine()) != null) {
      // Read all the headers and extract the message content from them
      int contentLength = -1;
      while (!header.equals("")) {
        System.out.println("Header: " + header);
        if (isContentLengthHeader(header)) {
          contentLength = getContentLength(header);
        }
        header = in.readLine();
      }

      System.out.println("Reading body");
      // Read the body
      if (contentLength == -1) {
        throw new RuntimeException("Unexpected content length in message");
      }
      char[] messageChars = new char[contentLength];
      in.read(messageChars, 0, contentLength);
      handleMessage(String.valueOf(messageChars));
    }
  }

  private void handleMessage(String message) {
    System.out.println("Message: " + message);

    if (message.contains("PM Main thread is waiting")) {
      System.out.println("Server is ready. Initializing");
      sendIntialize();
    }
  }

  private void sendIntialize() {
    System.out.println("Sending initialize message");

    String initializeMessage = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{\"clientInfo\":{\"name\":\"MyTestClient\",\"version\":\"1\"}}}";
    sendMessage(initializeMessage);
  }

  private void sendMessage(String body) {
    String header = "Content-Length: " + body.getBytes().length + "\r\n";
    final String message = header + "\r\n" + body;
    out.println(message);
  }


  private boolean isContentLengthHeader(String header) {
    return header.toLowerCase().contains("content-length");
  }

  private int getContentLength(String header) {
    return Integer.parseInt(header.split(" ")[1]);
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
