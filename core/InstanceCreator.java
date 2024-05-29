package org.linkSphere.core;

import org.linkSphere.util.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class InstanceCreator {
    private static InstanceCreator self = null;
    private HashMap<Class, Object> instances = new HashMap<>();
    private Logger logger = Logger.getLogger();

    public Object getInstance(Class clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!instances.containsKey(clazz)) {
            try {
                Constructor constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object newObj = constructor.newInstance();
                instances.put(clazz, newObj);
            } catch (NoSuchMethodException e) {
                logger.error(e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw e;
            }
        }

        return instances.get(clazz);
    }

    public static InstanceCreator getInstanceCreator() {
        if (self == null) {
            self = new InstanceCreator();
        }
        return self;
    }
}
