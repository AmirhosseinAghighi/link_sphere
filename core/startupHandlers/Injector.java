package org.linkSphere.core.startupHandlers;

import org.linkSphere.util.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Injector {
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
}
