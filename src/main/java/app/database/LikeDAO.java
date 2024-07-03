package app.database;

import app.database.schema.Like;
import app.database.schema.Post;
import app.database.schema.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;
import org.linkSphere.exceptions.duplicateException;

import java.util.List;
import java.util.NoSuchElementException;

@useDAO
@Dependency(name = "likeDAO")
public class LikeDAO {
    private static DAO dao;
    private SessionFactory sessionFactory;

    public LikeDAO() {
        sessionFactory = dao.getSessionFactory();
    }

    public void toggleLikePost(long userID, long postID) throws NoSuchElementException, ConstraintViolationException {
        Like currentLike = getLikeByUserOnPost(userID, postID);
        Session session = sessionFactory.getCurrentSession();
        try {
            session.getTransaction().begin();
            if (currentLike != null) {
                session.remove(currentLike);
            } else {
                User user = session.get(User.class, userID);
                Post post = session.get(Post.class, postID);
                if (user == null)
                    throw new NoSuchElementException("user not found.");
                if (post == null)
                    throw new NoSuchElementException("Post not found.");
                Like newLike = new Like(post, user);
                session.persist(newLike);
            }
            session.getTransaction().commit();
        } catch (NoSuchElementException | ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public boolean isPostLikedByUser(long userID, long postID) {
        return getLikeByUserOnPost(userID, postID) != null;
    }

    private Like getLikeByUserOnPost(long userID, long postID) throws NoSuchElementException, ConstraintViolationException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.getTransaction().begin();
            List<Like> likes = session.createQuery("FROM Like l where l.user.id = :userID AND l.post.id = :postID", Like.class)
                    .setParameter("userID", userID)
                    .setParameter("postID", postID)
                    .list();
            if (likes.isEmpty()) {
                session.getTransaction().commit();
                return null;
            }
            session.getTransaction().commit();
            return likes.get(0);
        } catch (NoSuchElementException | ConstraintViolationException e) {
            session.getTransaction().rollback();
            return null;
        }
    }

    public List<Like> getLikesOnPost(long postID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.getTransaction().begin();
            List<Like> likes = session.createQuery("FROM Like l where l.post.id = :postID", Like.class)
                    .setParameter("postID", postID)
                    .list();
            session.getTransaction().commit();
            return likes;
        } catch (NoSuchElementException | ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
}
