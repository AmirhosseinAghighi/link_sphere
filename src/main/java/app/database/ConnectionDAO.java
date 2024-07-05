package app.database;

import app.database.schema.Connection;
import app.database.schema.User;
import app.global.settingsEnum.ConnectionState;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;
import org.linkSphere.exceptions.duplicateException;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@useDAO
@Dependency(name = "connectionDAO")
public class ConnectionDAO {
    private static DAO dao;
    private static SessionFactory sessionFactory;

    public ConnectionDAO() {
        sessionFactory = dao.getSessionFactory();
    }

    // userID: the user that sent connection request to connectedUser
    // connectedUser: the user that is going to get connection request
    public void requestNewConnection(long userID, long connectedUser, String note) throws NoSuchElementException, duplicateException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            if (haveAnyStateOfConnectionWith(userID, connectedUser)) {
                throw new duplicateException("User requested or have connection already");
            }
            User user = session.get(User.class, userID);
            User connectionWithUser = session.get(User.class, connectedUser);
            Connection newConnection = new Connection(user, connectionWithUser, note, new Date().getTime());
            session.persist(newConnection);
            session.getTransaction().commit();
        } catch (NoSuchElementException | ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } catch (duplicateException e) {
            session.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            System.out.println("salam");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // userID: the user that got request
    // requestedUser: the user that sent connection request to userID
    public void responseToConnectionRequest(long userID, long requestedUser, boolean accepted) throws NoSuchElementException, ConstraintViolationException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Connection> connections = session.createQuery("FROM Connection c WHERE c.user.id = :userID AND c.connectedUser.id = :connectionWith AND c.state = :state", Connection.class)
                    .setParameter("userID", requestedUser)
                    .setParameter("connectionWith", userID)
                    .setParameter("state", ConnectionState.PENDING)
                    .list();

            if (connections.isEmpty()) {
                session.getTransaction().rollback();
                throw new NoSuchElementException("There isn't any connection request to accept.");
            }

            Connection connection = connections.get(0);
            if (accepted) {
                connection.setState(ConnectionState.ACCEPTED);
            } else {
                connection.setState(ConnectionState.DECLINED);
            }
            connection.setResponseDate(new Date().getTime());
            session.getTransaction().commit();
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
            throw e;
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private boolean haveAnyStateOfConnectionWith(long userID, long connectionID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            List<Connection> connections = session.createQuery("FROM Connection c WHERE ((c.user.id = :userID AND c.connectedUser.id = :connectionWith) OR " +
                            "(c.user.id = :connectionWith AND c.connectedUser.id = :userID)) AND c.state != :state", Connection.class)
                    .setParameter("userID", userID)
                    .setParameter("connectionWith", connectionID)
                    .setParameter("state", ConnectionState.DECLINED)
                    .list();

            if (connections.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (ConstraintViolationException e) {
            throw e;
        }
    }

    public boolean haveConnectionWith(long userID, long connectionID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Connection> connections = session.createQuery("FROM Connection c WHERE (c.user.id = :userID AND c.connectedUser.id = :connectionWith) OR " +
                            "(c.user.id = :connectionWith AND c.connectedUser.id = :userID) AND c.state = :state", Connection.class)
                    .setParameter("userID", userID)
                    .setParameter("connectionWith", connectionID)
                    .setParameter("state", ConnectionState.ACCEPTED)
                    .list();

            if (connections.isEmpty()) {
                session.getTransaction().rollback();
                session.getTransaction().commit();
                return false;
            } else {
                session.getTransaction().commit();
                return true;
            }
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void removeConnection(long userID, long connectionID) throws NoSuchElementException, ConstraintViolationException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Connection> connections = session.createQuery("FROM Connection c WHERE (c.user.id = :userID AND c.connectedUser.id = :connectionWith) OR " +
                            "(c.user.id = :connectionWith AND c.connectedUser.id = :userID) AND c.state = :state", Connection.class)
                    .setParameter("userID", userID)
                    .setParameter("connectionWith", connectionID)
                    .setParameter("state", ConnectionState.ACCEPTED)
                    .list();

            if (connections.isEmpty()) {
                session.getTransaction().rollback();
                throw new NoSuchElementException("There isn't any connection to remove it.");
            }

            session.remove(connections.get(0));
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public Long countConnections(Long userID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Long connectionsCount = session.createQuery("SELECT COUNT(*) FROM Connection c WHERE (c.user.id = :userID OR " +
                            "c.connectedUser.id = :userID) AND c.state != :state", Long.class)
                    .setParameter("userID", userID)
                    .setParameter("state", ConnectionState.ACCEPTED)
                    .uniqueResult();
            session.getTransaction().commit();
            return connectionsCount;
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
