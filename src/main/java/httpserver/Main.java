package httpserver;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Class to run the Server.
 */
public class Main {
    /**
     * Base URI the Grizzly HTTP server will listen on.
     */
    public static final String BASE_URI = "http://localhost:8080/server/";

    /**
     * Starts and returns the server object.
     * @return the server object.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in Server class
        final ResourceConfig rc = new ResourceConfig(Server.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Running method.
     * @param args the arguments.
     * @throws IOException an IOException.
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        System.out.println(String.format("Jersey app started with WADL available at " + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        System.in.read();
        server.shutdownNow();
    }
}
