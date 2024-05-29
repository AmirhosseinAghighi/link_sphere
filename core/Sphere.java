package org.linkSphere.core;

import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.core.startupHandlers.ClassScanner;
import org.linkSphere.core.startupHandlers.Injector;
import org.linkSphere.exceptions.criticalException;
import org.linkSphere.exceptions.duplicateException;
import org.linkSphere.http.HttpHandler;
import org.linkSphere.http.RequestHandler;
import org.linkSphere.http.RequestMethodTypes;
import org.linkSphere.util.Logger;

import java.lang.reflect.Method;
import java.util.List;

public class Sphere {
    private static boolean running = false;
    private static Logger logger = Logger.getLogger();
    private static boolean debug = false;
    private RequestHandler requestHandler;
    private HttpHandler server;

    public static void setDebug(boolean debug) {
        Sphere.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static Sphere start(int port, Class mainClass) throws duplicateException, criticalException {
        if (running) {
            throw new duplicateException("Sphere is running already.");
        } else {
            running = true;
            Sphere sphere =  new Sphere(port, mainClass);
            logger.info("Sphere started listening on https://localhost:" + port);
            return sphere;
        }

    }

    public Sphere(int port, Class mainClass) throws duplicateException {
        requestHandler = new RequestHandler();
        server = new HttpHandler(port, requestHandler);
        AnnotationProcessor(mainClass.getPackage().getName().replace(".", "/"), mainClass.getPackage().getName());
    }



    private void AnnotationProcessor(String directory, String packageName) {
        try {
            List<Class<?>> classes = ClassScanner.findClasses(directory, packageName);
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(UseLogger.class)) {
                    Injector.injectLogger(clazz);
                }
                if (clazz.isAnnotationPresent(Endpoint.class)) {
                    resolveEndpoints(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            logger.criticalException("Failed to startup: Annotation Processing Failed with error: ", e.getMessage());
        } catch (NoSuchFieldException e) {
            logger.criticalException("Failed to startup: Injecting Field Failed: ", e.getMessage());
        } catch (IllegalAccessException e) {
            logger.criticalException("Failed to startup: Injecting Field Failed: ", e.getMessage());
        }
    }

    private void resolveEndpoints(Class clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Get.class)) {
                if (method.getParameterCount() != 2) continue;
                String path = ((Endpoint) clazz.getAnnotation(Endpoint.class)).value();
                try {
                    logger.debug("Resolving endpoint method ", method.getName(), " with path ", path);
                    requestHandler.addPath(path, RequestMethodTypes.GET, method);
                } catch (duplicateException e) {
                    logger.warning("Ignoring duplicated path: ", path);
                }
            }
        }
    }
}
