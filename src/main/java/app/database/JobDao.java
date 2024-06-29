package app.database;

import app.database.schema.Company;
import app.database.schema.Job;
import app.database.schema.Profile;
import app.database.schema.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@useDAO
@Dependency(name = "jobDAO")
public class JobDao {
    private static DAO dao;
    private SessionFactory sessionFactory;

    public JobDao() {
        sessionFactory = dao.getSessionFactory();
    }

    public void createNewJob(long userID, long companyID, Job job) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Profile profile = session.get(Profile.class, userID);
            if (profile == null)
                throw new NoSuchElementException("User not found.");
            job.setProfile(profile);
            Company company = session.get(Company.class, companyID);
            if (company == null)
                throw new NoSuchElementException("Company not found.");
            job.setCompanyObject(company);
            session.persist(job);
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void updateExistingJobByID(long id, Job jobData) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Job job = session.get(Job.class, id);

            if (job == null) {
                session.getTransaction().rollback();
                throw new NoSuchElementException("Job not found.");
            }

            if (jobData.getTitle() != null) {
                job.setTitle(jobData.getTitle());
            }

            if (jobData.getDescription() != null) {
                job.setDescription(jobData.getDescription());
            }

            if (jobData.getStartDate() != null) {
                if ((job.getEndDate() != null && jobData.getStartDate() >= job.getEndDate())) {
                    session.getTransaction().rollback();
                    throw new IllegalArgumentException("Not valid end date");
                }
                job.setStartDate(jobData.getStartDate());
            }

            if (jobData.getEndDate() != null) {
                if (jobData.getEndDate() <= job.getStartDate()) {
                    session.getTransaction().rollback();
                    throw new IllegalArgumentException("Not valid end date");
                }
                job.setEndDate(jobData.getEndDate());
            }

            if (jobData.getCompany() != null ) {
                Company newCompany = (Company) session.get(Company.class, jobData.getCompany()); // idk how to check if found with load method
                if (newCompany == null) {
                    session.getTransaction().rollback();
                    throw new NoSuchElementException("Company not found.");
                }
            }

            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Job> getUserJobs(long userID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Job> jobs = session.createQuery("From Job job WHERE job.profile.user.id = :userID").setParameter("userID", userID).list();
            for (Job job : jobs) {
                Hibernate.initialize(job.getProfile().getUser().getToken());
            }
            session.getTransaction().commit();
            return jobs;
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}