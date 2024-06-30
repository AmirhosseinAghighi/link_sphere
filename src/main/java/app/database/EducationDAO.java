package app.database;

import app.database.schema.Education;
import app.database.schema.Job;
import app.database.schema.Profile;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;

import java.util.List;
import java.util.NoSuchElementException;

@useDAO
@Dependency(name = "educationDAO")
public class EducationDAO {
    private static DAO dao;
    private static SessionFactory sessionFactory;

    public EducationDAO() {
        sessionFactory = dao.getSessionFactory();
    }

    public void registerNewEducation(long userID, Education education) throws ConstraintViolationException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Profile profile = session.get(Profile.class, userID);
            if (profile == null)
                throw new NoSuchElementException("User not found.");
            education.setProfile(profile);
            session.persist(education);
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void updateExistingEducation(long userID, long id, Education educationData) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Education education = session.get(Education.class, id);

            if (education == null) {
                session.getTransaction().rollback();
                throw new NoSuchElementException("Education not found.");
            }

            if (education.getProfile().getUser().getId() != userID) {
                session.getTransaction().rollback();
                throw new IllegalAccessError("Not allowed to update this education.");
            }

            if (educationData.getInstitutionName() != null) {
                education.setInstitutionName(educationData.getInstitutionName());
            }

            if (educationData.getDegree() != null) {
                education.setDegree(educationData.getDegree());
            }

            if (educationData.getFieldOfStudy() != null) {
                education.setFieldOfStudy(educationData.getFieldOfStudy());
            }

            if (educationData.getStartDate() != null) {
                if ((education.getEndDate() != null && educationData.getStartDate() >= education.getEndDate())) {
                    session.getTransaction().rollback();
                    throw new IllegalArgumentException("Not valid end date");
                }
                education.setStartDate(educationData.getStartDate());
            }

            if (educationData.getEndDate() != null) {
                if (educationData.getEndDate() <= education.getStartDate()) {
                    session.getTransaction().rollback();
                    throw new IllegalArgumentException("Not valid end date");
                }
                education.setEndDate(educationData.getEndDate());
            }

            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Education> getUserEducations(long userID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Education> educations = session.createQuery("From Education education WHERE education.profile.user.id = :userID").setParameter("userID", userID).list();
            session.getTransaction().commit();
            return educations;
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
