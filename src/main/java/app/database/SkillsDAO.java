package app.database;

import app.database.schema.Education;
import app.database.schema.Profile;
import app.database.schema.Skill;
import jakarta.persistence.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;

import java.util.List;
import java.util.NoSuchElementException;

@useDAO
@Dependency(name = "skillsDAO")
public class SkillsDAO {
    private static DAO dao;
    private SessionFactory sessionFactory;

    public SkillsDAO() {
        sessionFactory = dao.getSessionFactory();
    }

    public void registerNewSkill(long userID, Skill skill) throws ConstraintViolationException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Profile profile = session.get(Profile.class, userID);
            if (profile == null)
                throw new NoSuchElementException("User not found.");
            skill.setProfile(profile);
            session.persist(skill);
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void removeSkill(long userID, long id) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Skill skill = session.get(Skill.class, id);

            if (skill == null) {
                session.getTransaction().rollback();
                throw new NoSuchElementException("Skill not found.");
            }

            if (skill.getProfile().getUser().getId() != userID) {
                session.getTransaction().rollback();
                throw new IllegalAccessError("Not allowed to remove this skill.");
            }

            session.remove(skill);

            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Skill> getUserSkills(long userID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Skill> skills = session.createQuery("From Skill skill WHERE skill.profile.user.id = :userID").setParameter("userID", userID).list();
            session.getTransaction().commit();
            return skills;
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
