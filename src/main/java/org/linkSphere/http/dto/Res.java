package org.linkSphere.http.dto;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.linkSphere.exceptions.notFoundException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.nio.file.Files;
import java.util.ArrayList;

public class Res {
    private Headers responseHeaders;
    private HttpExchange exchange;
    private ArrayList<String> cookies;

    public Res(HttpExchange exchange) {
        this.exchange = exchange;
        this.responseHeaders = exchange.getResponseHeaders();
    }

    public Res addHeader(String name, String value) {
        responseHeaders.add(name, value);
        return this;
    }

    private void addDefaultResponseHeaders() {
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseHeaders.add("Access-Control-Allow-Credentials", "true");
        responseHeaders.add("Access-Control-Allow-Methods", "GET, POST");
    }

    public void send(int statusCode, String response) {
        send(statusCode, response, "application/json");
    }

    public void send(int statusCode, String response, String contentType) throws RuntimeException {
        addDefaultResponseHeaders();
        responseHeaders.add("Content-Type", contentType);

        if (cookies != null) {
            for (String cookie : cookies) {
                responseHeaders.add("Set-Cookie", cookie);
            }
        }

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

    public void sendError(int statusCode, String message) throws RuntimeException {
        send(statusCode, String.format("{\"code\": %d, \"message\": \"%s\"}", statusCode, message));
    }

    public void sendMessage(String message) throws RuntimeException {
        sendError(200, message);
    }

    public void sendFile(File file, String contentType) throws notFoundException, IOException {
        if (!file.exists()) {
            throw new notFoundException("File Not Found");
        }
        addDefaultResponseHeaders();
        responseHeaders.add("Content-Type", contentType);

        try {
            exchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            // TODO: add logger to log this exception as critical for user
            throw new RuntimeException(e);
        }

        OutputStream outputStream = exchange.getResponseBody();
        try {
            Files.copy(file.toPath(), outputStream);
        } catch (IOException e) {
            throw e;
        }
        outputStream.close();
    }

    public void redirect(String newLocation) {
        exchange.getResponseHeaders().set("Location", newLocation);
        try {
            exchange.sendResponseHeaders(302, -1); // 302 Found for redirection
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCookie(String name, String value) {
        addCookie(name, value, false);
    }

    public void addCookie(String name, String value, boolean secure) {
        if (cookies == null) {
            cookies = new ArrayList<>();
        }

        HttpCookie cookie = new HttpCookie(name, value);
        cookie.setPath("/");
        cookie.setSecure(secure);
        cookie.setHttpOnly(secure);
        cookies.add(cookie.toString());
    }
}