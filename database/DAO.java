package org.linkSphere.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.linkSphere.database.schema.User;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

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

    public void createNewUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.persist(user);
        session.getTransaction().commit();
        session.close();
    }
}
