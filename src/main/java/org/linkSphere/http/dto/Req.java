package org.linkSphere.http.dto;

import com.sun.net.httpserver.HttpExchange;
import org.linkSphere.http.RequestMethodTypes;

import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Req {
    private RequestMethodTypes method;
    private String url;
    private int port;
    private String protocolVersion;
    private String ip;
    private Map<String, List<String>> headers;
    private String query;
    private String requestBody;
    private Map<String, String> cookies;
    private String userAgent;

    public Req(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET": {
                this.method = RequestMethodTypes.GET;
                break;
            }
            case "POST": {
                this.method = RequestMethodTypes.POST;
                break;
            }
            case "PUT": {
                this.method = RequestMethodTypes.PUT;
                break;
            }
            case "DELETE": {
                this.method = RequestMethodTypes.DELETE;
                break;
            }
            case "HEAD": {
                this.method = RequestMethodTypes.HEAD;
                break;
            }
        }

        this.url = exchange.getRequestURI().getPath();
        this.port = exchange.getRequestURI().getPort();
        this.protocolVersion = exchange.getProtocol();
        this.ip = exchange.getRemoteAddress().getAddress().getHostAddress();
        this.headers = exchange.getRequestHeaders();
        this.query = exchange.getRequestURI().getQuery();

        Scanner scanner = new Scanner(exchange.getRequestBody(), StandardCharsets.UTF_8.name());
        scanner.useDelimiter("\\A");
        this.requestBody = scanner.hasNext() ? scanner.next() : "";

        this.cookies = parseCookies(exchange.getRequestHeaders().getFirst("Cookie"));
        this.userAgent = exchange.getRequestHeaders().getFirst("User-Agent");
    }

    private Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader != null && !cookieHeader.isEmpty()) {
            String[] cookiePairs = cookieHeader.split("; ");
            for (String cookiePair : cookiePairs) {
                HttpCookie cookie = HttpCookie.parse(cookiePair).getFirst();
                cookies.put(cookie.getName(), cookie.getValue());
            }
        }
        return cookies;
    }

    public RequestMethodTypes getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getIp() {
        return ip;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getQuery() {
        return query;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
