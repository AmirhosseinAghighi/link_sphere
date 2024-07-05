package app.database;

import app.database.schema.Post;
import app.database.schema.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;

import java.util.NoSuchElementException;

@useDAO
@Dependency(name = "postDAO")
public class PostDAO {
    private static DAO dao;
    private SessionFactory sessionFactory;

    public PostDAO() {
        sessionFactory = dao.getSessionFactory();
    }

    public Long createNewPost(long userID, String text) throws ConstraintViolationException, NoSuchElementException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            User user = (User) session.get(User.class, userID);
            if (user == null) {
                session.getTransaction().rollback();
                throw new NoSuchElementException("User not found");
            }
            Post post = new Post(user, text);
            session.persist(post);
            session.getTransaction().commit();
            return post.getId();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public Post getPost(long postID) throws NoSuchElementException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Post post = (Post) session.get(Post.class, postID);
            if (post == null) {
                session.getTransaction().rollback();
                throw new NoSuchElementException("Post not found");
            }
            session.getTransaction().commit();
            return post;
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void removePost(long postID) throws NoSuchElementException, ConstraintViolationException {
        Post post = getPost(postID);
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.remove(post);
            session.getTransaction().commit();
        } catch (ConstraintViolationException | NoSuchElementException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
