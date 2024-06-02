package org.linkSphere.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
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

    public void createNewUser(User user) throws ConstraintViolationException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            throw e;
        } finally {
            session.close();
        }
    }
}
