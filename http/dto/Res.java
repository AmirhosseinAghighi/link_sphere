package org.linkSphere.http.dto;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class Res {
    private Headers responseHeaders;
    private HttpExchange exchange;
    public Res(HttpExchange exchange) {
        this.exchange = exchange;
        this.responseHeaders = exchange.getResponseHeaders();
    }

    public void send(int statusCode, String response) throws RuntimeException {
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
        responseHeaders.add("Access-Control-Allow-Credentials", "true");
        responseHeaders.add("Access-Control-Allow-Methods", "GET, POST");

        //Sending back response to the client
        try {
            exchange.sendResponseHeaders(statusCode, response.length());
        } catch (IOException e) {
            // TODO: add logger to log this exception as critical for user
            throw new RuntimeException(e);
        }

        try (OutputStream outStream = exchange.getResponseBody()) {
            outStream.write(response.getBytes());
        } catch (IOException e) {
            // TODO: add logger to log this exception as critical for user
            throw new RuntimeException(e);
        }
    }
}
