package org.linkSphere.core.startupHandlers;

import org.linkSphere.util.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {
    public static List<Class<?>> findClasses(String directoryPath, String packageName) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(directoryPath);

        List<Class<?>> classes = new ArrayList<>();
        File directory = null;
        try {
            System.out.println(directoryPath);
            System.out.println(resource.toURI());
            if (resource != null)
                directory = new File(resource.toURI());
        } catch (URISyntaxException e) {
            Logger.getLogger().warning("NO WAY");
            return classes;
        }

        if (directory == null || !directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(packageName.replace(".", "/") + "/" + file.getName(), packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }

        return classes;
    }
}
