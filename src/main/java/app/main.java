package app;

import org.linkSphere.annotations.UseLogger;
import org.linkSphere.core.Sphere;
import org.linkSphere.util.Logger;

@UseLogger
public class main {
    private static Logger logger;
    public static void main(String[] args) {
        try {
            Sphere.setDebug(true);
            Sphere.start(3000, main.class);
        } catch (Exception e) {
            if (logger == null)
                System.out.println(e.getMessage());
            else
                logger.critical(e.getMessage());
        }
    }
}


