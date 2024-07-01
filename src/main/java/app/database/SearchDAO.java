package app.database;

import app.global.CountryCode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;

import java.util.List;

@useDAO
@Dependency(name = "searchDAO")
public class SearchDAO {
    private static DAO dao;
    private SessionFactory sessionFactory;

    public SearchDAO() {
        sessionFactory = dao.getSessionFactory();
    }

    public List<Object[]> SearchUsers(String query, int limit) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            List<Object[]> result = session.createQuery("SELECT DISTINCT profile.nickName, profile.user.id FROM Profile profile LEFT JOIN profile.jobs job " +
                    "LEFT JOIN profile.educations education " +
                    "LEFT JOIN profile.skills skill WHERE profile.nickName LIKE :query " +
                    "OR profile.firstName LIKE :query " +
                    "OR profile.lastName LIKE :query " +
//                    "OR job.companyObject.name LIKE :query " +
                    "OR job.title LIKE :query " +
                    "OR education.degree LIKE :query " +
                    "OR education.fieldOfStudy LIKE :query " +
                    "OR education.institutionName LIKE :query " +
                    "OR skill.skillName LIKE :query " +
                    "ORDER BY " +
                    "profile.nickName, profile.user.id LIMIT :limit", Object[].class).setParameter("query", "%" + query + "%").setParameter("limit", limit).list();
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Object[]> SearchCompanies(String query, int limit) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            int countryCodeQuery = 0;
            if (query.matches("[+0-9]")) {
                countryCodeQuery = Integer.parseInt(query);
            }
            List<Object[]> result = session.createQuery("SELECT DISTINCT company.name, company.id FROM Company company " +
                    "WHERE company.name LIKE :query " +
                    "OR company.description LIKE :query " +
                    "OR company.location = :countryCodeQuery " +
                    "ORDER BY " +
                    "company.name, company.id LIMIT :limit", Object[].class).setParameter("query", "%" + query + "%").setParameter("countryCodeQuery", countryCodeQuery).setParameter("limit", limit).list();
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
