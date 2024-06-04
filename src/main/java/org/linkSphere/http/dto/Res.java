package org.linkSphere.http.dto;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.linkSphere.annotations.useGson;
import org.linkSphere.core.Sphere;
import org.linkSphere.exceptions.NoStatusCodeException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public void send(int statusCode, String response) throws RuntimeException {
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
        responseHeaders.add("Access-Control-Allow-Credentials", "true");
        responseHeaders.add("Access-Control-Allow-Methods", "GET, POST");
        responseHeaders.add("Content-Type", "application/json");

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