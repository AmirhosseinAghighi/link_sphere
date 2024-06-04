package app;

import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.core.Sphere;
import org.linkSphere.database.DAO;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.util.Logger;

@UseLogger
@useDAO
public class main {
    private static Logger logger;
    private static DAO dao;
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