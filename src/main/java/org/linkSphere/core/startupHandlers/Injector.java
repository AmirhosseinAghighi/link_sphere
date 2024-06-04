package org.linkSphere.core.startupHandlers;

import org.linkSphere.core.Sphere;
import org.linkSphere.database.DAO;
import org.linkSphere.util.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Injector {
    private static final HashMap<String, Object> dependencies = new HashMap<>();

    public static void createInstance(String name, Class clazz) {
        try {
            dependencies.put(name, clazz.newInstance());
        } catch (Exception e) {
            Logger.getLogger().critical(e.getMessage());
        }
    }

    public static void injectDependencies(Field field, String key) throws IllegalAccessException {
        field.setAccessible(true);
        try {
            field.set(field.getClass(), dependencies.get(key));
        } catch (IllegalAccessException e) {
            throw e;
        }
    }



    public static void injectLogger(Class clazz) throws NoSuchFieldException, IllegalAccessException {
        Field loggerField = null;
        try {
            loggerField = clazz.getDeclaredField("logger");
            loggerField.setAccessible(true);
            loggerField.set(clazz, Logger.getLogger());
        } catch (NoSuchFieldException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        }
    }

    public static void injectGson(Class clazz) throws NoSuchFieldException, IllegalAccessException {
        Field gsonField = null;
        try {
            gsonField = clazz.getDeclaredField("gson");
            gsonField.setAccessible(true);
            gsonField.set(clazz, Sphere.getGson());
        } catch (NoSuchFieldException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        }
    }

    public static void injectDAO(Class clazz) throws NoSuchFieldException, IllegalAccessException {
        Field daoField = null;
        try {
            daoField = clazz.getDeclaredField("dao");
            daoField.setAccessible(true);
            daoField.set(clazz, DAO.getInstance());
        } catch (NoSuchFieldException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        }
    }


}
