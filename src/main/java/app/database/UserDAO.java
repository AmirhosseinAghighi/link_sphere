package app.database;

import app.database.schema.Token;
import app.database.schema.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;

import java.util.List;
import java.util.NoSuchElementException;

@useDAO
@Dependency(name = "userDAO")
public class UserDAO {
    private static DAO dao;
    private SessionFactory sessionFactory;

    public UserDAO() {
        sessionFactory = dao.getSessionFactory();
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

    public User getUserByUsername(String username) throws NoSuchElementException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List result = session.createQuery("from User where username = :username").setParameter("username", username).list();
            return (User) result.getFirst();
        } catch (NoSuchElementException e) {
            throw e;
        } finally {
            session.close();
        }
    }

    public String getUserPasswordByUsername(String username) throws NoSuchElementException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List result = session.createQuery("SELECT password FROM User s WHERE s.username = :username").setParameter("username", username).list();
            session.getTransaction().commit();
            return (String) result.getFirst();
        } catch (NoSuchElementException e) {
            throw e;
        } finally {
            session.close();
        }
    }

    public void RegisterNewRefreshToken(String refreshToken, String userAgent, String ip, User user) {
        Session session = sessionFactory.getCurrentSession();
        Token token = new Token(user, refreshToken, userAgent, ip);
        try {
            session.beginTransaction();
            session.persist(token);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void UpdateUserRefreshToken(long userID, String refreshToken, String userAgent, String ip, String oldRefreshToken) throws NoSuchElementException {
        // TODO: search for better way to update ( using single query and not select it at first )
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Token token = (Token) session.createQuery("from Token token where token.user.id = :userID and token.token = :refreshToken")
                    .setParameter("userID", userID).setParameter("refreshToken", oldRefreshToken).list().getFirst();
            token.setToken(refreshToken);
            token.setIp(ip);
            token.setUserAgent(userAgent);
            session.getTransaction().commit();
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public boolean doesUserExist(long userID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            User userObject = (User) session.get(User.class, userID);
            return userObject != null;
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return false;
    }

    public String getUsernameByID(long userID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            User userObject = (User) session.get(User.class, userID);
            if (userObject != null)
                return userObject.getUsername();
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return "";
    }

//    public boolean doesUserExist(String username) {
//
//    }
}
