package org.linkSphere.http.dto;

import com.sun.net.httpserver.HttpExchange;
import org.linkSphere.exceptions.criticalException;
import org.linkSphere.exceptions.notFoundException;
import org.linkSphere.http.RequestMethodTypes;

import java.io.*;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Req {
    private RequestMethodTypes method;
    private String url;
    private int port;
    private String protocolVersion;
    private String ip;
    private Map<String, List<String>> headers;
    private String query;
    private byte[] rquestBodyByteArray;
    private String requestBody;
    private Map<String, String> cookies;
    private String userAgent;
    private HashMap<String, String> dynamicParameters;
    private Pattern fileNamePattern = Pattern.compile("filename=\"(.+?)\"");
    private Pattern fileFormatPattern = Pattern.compile("Content-Type: (.+)");


    public Req(HttpExchange exchange) throws IOException {
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

        this.rquestBodyByteArray = readInputStream(exchange.getRequestBody());
        this.requestBody = new String(rquestBodyByteArray, StandardCharsets.UTF_8);

        this.cookies = parseCookies(exchange.getRequestHeaders().getFirst("Cookie"));
        this.userAgent = exchange.getRequestHeaders().getFirst("User-Agent");
        this.dynamicParameters = new HashMap<>();
    }

    public void addDynamicParameter(String key, String parameter) {
        dynamicParameters.put(key, parameter);
    }

    private byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
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

    public int getUploadedFilesCount() {
        int count = 0;
        Matcher matcher = fileNamePattern.matcher(requestBody);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    public String getUploadedFileName(int key) {
        String fileName = null;
        Matcher matcher = fileNamePattern.matcher(requestBody);
        for (int i = 0; i < key; i++) {
            if (matcher.find()) {
                fileName = matcher.group(1);
            } else {
                return null;
            }
        }
        return fileName;
    }

    public String getUploadedFileFormat(int key) {
        String fileFormat = null;
        Matcher matcher = fileFormatPattern.matcher(requestBody);
        for (int i = 0; i < key; i++) {
            if (matcher.find()) {
                fileFormat = matcher.group(1);
            } else {
                return null;
            }
        }
        return fileFormat;
    }

    public boolean isMultipartRequest() {
        List<String> headerValue = headers.get("Content-Type");
        return headerValue != null && headerValue.getFirst() != null && headerValue.getFirst().startsWith("multipart/form-data");
    }

    private String getMultipartBoundary() throws criticalException {
        if (isMultipartRequest()) {
            String headerValue = headers.get("Content-Type").getFirst();
            return headerValue.split("boundary=")[1];
        } else {
            throw new criticalException("This Request is not multipart/form-data request");
        }
    }


    public void saveUploadedFile() throws notFoundException, criticalException, IOException {
        // TODO: fix this fucked up code for multipart/form-data requests :/
        InputStream inputStream = new ByteArrayInputStream(rquestBodyByteArray);
        String boundary = getMultipartBoundary();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        byte[] buffer = new byte[1264640];
        int bytesCount;
        boolean found = false;
        ByteArrayOutputStream fileContent = new ByteArrayOutputStream();


        Path filePath = Paths.get("uploads/test.jpg");
        Files.createDirectories(filePath.getParent()); // Ensure the directory exists
        try (FileOutputStream outputStream = new FileOutputStream("src/main/java/app/assets/t.jpg")) {
            while ((bytesCount = inputStream.read(buffer)) != -1) {
                String asString = new String(buffer, 0, bytesCount);
                if (asString.contains(boundary)) {
                    String test = "Content-Type: image/jpeg";
//                    System.out.println();
                    byte[] t = asString.substring(asString.indexOf(test) + test.length()).getBytes(StandardCharsets.UTF_8);
                    outputStream.write(buffer, 0, bytesCount);
                }
            }
        }
    }

    public HashMap<String, String> getQueryParameters() {
        HashMap<String, String> queryParameters = new HashMap<>();
        String[] queries = getQuery().split("&");
        for (String queryParameter : queries) {
            String[] keyValue = queryParameter.split("=");
            queryParameters.put(keyValue[0], keyValue[1]);
        }
        return queryParameters;
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

    public HashMap<String, String> getDynamicParameters() {
        return dynamicParameters;
    }

    public byte[] getRequestBodyAsByteArray() {
        return rquestBodyByteArray;
    }
}
