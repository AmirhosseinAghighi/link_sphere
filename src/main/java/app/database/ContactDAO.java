package app.database;

import app.database.schema.Contact;
import app.database.schema.Profile;
import app.database.schema.Skill;
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
@Dependency(name = "contactDAO")
public class ContactDAO {
    private static DAO dao;
    private static SessionFactory sessionFactory;

    public ContactDAO() {
        sessionFactory = dao.getSessionFactory();
    }

    public void registerNewContact(long userID, Contact contact) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            User user = session.get(User.class, userID);
            if (user == null)
                throw new NoSuchElementException("User not found.");
            contact.setUser(user);
            session.persist(contact);
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void removeContact(long userID, long id) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Contact contact = session.get(Contact.class, id);

            if (contact == null) {
                session.getTransaction().rollback();
                throw new NoSuchElementException("Contact not found.");
            }

            if (contact.getUser().getId() != userID) {
                session.getTransaction().rollback();
                throw new IllegalAccessError("Not allowed to remove this contact.");
            }

            session.remove(contact);

            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Contact> getUserContacts(long userID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Contact> contacts = session.createQuery("From Contact contacts WHERE contacts.user.id = :userID").setParameter("userID", userID).list();
            session.getTransaction().commit();
            return contacts;
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}