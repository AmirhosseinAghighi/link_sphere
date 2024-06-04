package org.linkSphere.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DAO {
    private static boolean running = false;
    private static DAO instance;
    private SessionFactory sessionFactory;

    private DAO() {
        sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
    }

    public static DAO getInstance() {
        if (instance == null) {
            instance = new DAO();
        }
        return instance;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
