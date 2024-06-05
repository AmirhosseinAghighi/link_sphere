package org.linkSphere.http;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.core.InstanceCreator;
import org.linkSphere.exceptions.duplicateException;
import org.linkSphere.exceptions.notFoundException;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.util.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UseLogger
public class RequestHandler implements HttpHandler{
    private HashMap<String, RequestHandler> paths = new HashMap<String, RequestHandler>();
    private HashMap<RequestMethodTypes, Method> methods = new HashMap<RequestMethodTypes, Method>();
    private String dynamicRegex = "\\{\\w+\\}";
    private Pattern dynamicPattern = Pattern.compile(dynamicRegex);
    private String dynamicKey = "";
    private Logger logger = Logger.getLogger();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Req req = new Req(httpExchange);
        Res res = new Res(httpExchange);
        logger.debug("Request received: {}", req.getUrl());
        try {
            runPath(req.getUrl(), req.getMethod(),req, res);
        } catch (notFoundException e) {
            logger.error("Request not found: " + req.getUrl());
            try {
                runPath("/404", RequestMethodTypes.GET, req, res);
            } catch (notFoundException ex) {
                logger.info("There's not any registered 404 path, sending default page");
                res.send(404, "404 - Not Found");
            }
        }
    }

    public RequestHandler() {
        Logger.getLogger().debug("RequestHandler Created.");
    }

    public void setDynamicKey(String dynamicKey) {
        this.dynamicKey = dynamicKey;
    }

    public String getDynamicKey() {
        return dynamicKey;
    }

    public Method getMethod(RequestMethodTypes type) {
        return methods.get(type);
    }

    public void addPath(String path, RequestMethodTypes methodType, Method method) throws duplicateException { // , RequestMethodTypes methodType,Runnable method
        if (path.isEmpty()) {
            method.setAccessible(true);
            methods.put(methodType, method);
            return;
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        int partIndex = path.indexOf("/");
        String pathOption;
        if (partIndex == -1)  {
            pathOption = path;
            partIndex = path.length() - 1;
        } else {
             pathOption = path.substring(0, partIndex);
        }

        path = path.substring(partIndex + 1);

        String dynamicKey = "";
        if (dynamicPattern.matcher(pathOption).find()) {
            dynamicKey = pathOption.substring(1, pathOption.length() - 1);
            pathOption = "{}";
        }

        var currentPath = paths.get(pathOption);
        if (currentPath == null) {
            currentPath = new RequestHandler();
            currentPath.setDynamicKey(dynamicKey);
            paths.put(pathOption, currentPath);
            currentPath.addPath(path, methodType, method);
            return;
        } else if (path.isEmpty()) {
            if (getMethod(methodType) != null)
                throw new duplicateException("duplicated path Found");
        }

        try {
            currentPath.addPath(path, methodType, method);
        } catch (duplicateException e) {
            throw new duplicateException("duplicated path Found: " + path);
        }
    }

    public void runPath(String path, RequestMethodTypes methodType, Req req, Res res) throws notFoundException{
        try {
            runPath(path, methodType, new HashMap<String, String>(), req, res);
        } catch (notFoundException e) {
            throw e;
        }
    }

    private void runPath(String path, RequestMethodTypes methodType, HashMap<String, String> params, Req req, Res res) throws notFoundException {
//        if (path.isEmpty()) {
//            var currentPath = paths.get(path);
//        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        int partIndex = path.indexOf("/");
        String pathOption;
        if (partIndex == -1)  {
            pathOption = path;
            partIndex = path.length() - 1;
        } else {
            pathOption = path.substring(0, partIndex);
        }

        path = path.substring(partIndex + 1);

        var currentPath = paths.get(pathOption);
        if (currentPath == null) {
            currentPath = paths.get("{}");
            req.addDynamicParameter(currentPath.getDynamicKey(), pathOption);
            if (currentPath == null)
                throw new notFoundException("Path does not registered.");
        }

        if (path.isEmpty()) {
            Method method = currentPath.getMethod(methodType);
            if (method != null) {
                Class clazz = method.getDeclaringClass();
                try {
                    method.invoke(InstanceCreator.getInstanceCreator().getInstance(clazz), req, res);
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message == null) {
                        logger.critical(e.getCause().getMessage());
                    } else {
                        logger.critical(e.getMessage());
                    }
                }
                return;
            } else {
                throw new notFoundException("Path does not registered.");
            }
        }

        try {
            params.put(currentPath.getDynamicKey(), pathOption);
            currentPath.runPath(path, methodType, params, req, res);
        } catch (notFoundException e) {
            throw e;
        }
    }
}
