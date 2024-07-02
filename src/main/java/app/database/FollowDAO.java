package app.database;

import app.database.schema.Follow;
import app.database.schema.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;

import java.util.Date;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.NoSuchElementException;

@Dependency(name = "followDAO")
@useDAO
public class FollowDAO {
    private static DAO dao;
    private static SessionFactory sessionFactory;

    public FollowDAO() {
        sessionFactory = dao.getSessionFactory();
    }

    public void registerNewFollowing(long userID, long followingID) throws NoSuchElementException, DuplicateFormatFlagsException, DuplicateFormatFlagsException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Follow> follows = (List<Follow>) session.createQuery("FROM Follow follow WHERE follow.user.id = :userID and follow.followingUser.id = :followingID", Follow.class)
                    .setParameter("userID", userID)
                    .setParameter("followingID", followingID)
                    .list();
            if (follows != null && !follows.isEmpty()) {
                session.getTransaction().rollback();
                throw new DuplicateFormatFlagsException("Following is already registered");
            }

            User user = session.load(User.class, userID);
            User followingUser = session.load(User.class, followingID);
            Follow follow = new Follow(user, followingUser, new Date().getTime());
            session.persist(follow);
            session.getTransaction().commit();
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
            throw new NoSuchElementException("User not found");
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void removeFollowing(long id, long userID, long followingID) throws NoSuchElementException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            if (id != 0) {
                Follow follow = session.get(Follow.class, id);
                if (follow == null) {
                    session.getTransaction().rollback();
                    throw new NoSuchElementException("User is not following any one with this id");
                }
                session.remove(follow);
            } else {
                List<Follow> follows = (List<Follow>) session.createQuery("FROM Follow follow WHERE follow.user.id = :userID and follow.followingUser.id = :followingID", Follow.class)
                        .setParameter("userID", userID)
                        .setParameter("followingID", followingID)
                        .list();

                if (follows == null || follows.isEmpty()) {
                    session.getTransaction().rollback();
                    throw new NoSuchElementException("User is not following any one with this id");
                }
                session.remove(follows.get(0));
            }
            session.getTransaction().commit();
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
            throw new NoSuchElementException("User not found");
        } finally {
            session.close();
        }
    }
}
