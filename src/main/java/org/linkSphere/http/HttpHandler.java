package org.linkSphere.http;
import com.sun.net.httpserver.HttpServer;
import org.linkSphere.exceptions.criticalException;
import org.linkSphere.exceptions.duplicateException;
import org.linkSphere.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpHandler {
    public HttpHandler(int port, RequestHandler reqHandler) throws duplicateException {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new duplicateException("A critical error happened in HttpHandler start method: " + e.getMessage());
        }
//        server.createContext("/", reqHandler);
//        System.out.println(reqHandler);
        server.createContext("/", reqHandler);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool()); // set executor to be parallel
        server.start();
        Logger.getLogger().debug("HttpHandler started on port {}", port);
    }
}
