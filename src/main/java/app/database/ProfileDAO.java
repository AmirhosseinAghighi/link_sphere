package app.database;

import app.database.schema.Profile;
import app.database.schema.User;
import app.global.settingsEnum.birthdayView;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Dependency;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.database.DAO;

import java.util.NoSuchElementException;

@useDAO
@Dependency(name = "profileDAO")
public class ProfileDAO {
    private static DAO dao;
    private SessionFactory sessionFactory;

    public ProfileDAO() {
        sessionFactory = dao.getSessionFactory();
    }

    public void createUserProfile(long userID, String firstName, String lastName, String nickname, int countryCode) {
        Session session = sessionFactory.getCurrentSession();
        Profile profile = new Profile(firstName, lastName, nickname, countryCode);
        try {
            session.beginTransaction();
            User user = (User) session.load(User.class, userID);
            if (user == null) {
                throw new NoSuchElementException("User not found.");
            }
            profile.setUser(user);
            session.persist(profile);
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            throw e;
        } finally {
            session.close();
        }
    }

    public boolean doesUserHaveProfile(long userID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            long count = (long) (session.createQuery("SELECT COUNT(*) FROM Profile profile WHERE profile.user.id = :userID").setParameter("userID", userID).uniqueResult());
            return count != 0;
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return false;
    }

    public void UpdateUserInformation(long userID, String firstName, String lastName, String nickname, int countryCode, Long birthday, birthdayView birthdaySetting, String phoneNumber, String bio) throws NoSuchElementException {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Profile profile = (Profile) session.get(Profile.class, userID);

            if (firstName != null && !firstName.isBlank()) {
                profile.setFirstName(firstName);
            }

            if (lastName != null && !lastName.isBlank()) {
                profile.setLastName(lastName);
            }

            if (nickname != null && !nickname.isBlank()) {
                profile.setNickName(nickname);
            }

            if (countryCode != 0) {
                profile.setCountryCode(countryCode);
            }

            if (bio != null && !bio.isBlank()) {
                profile.setBio(bio);
            }

            if (birthday != null) {
                profile.setBirthday(birthday);
            }

            if (birthdaySetting != null) {
                profile.setBirthdaySetting(birthdaySetting);
            }

            if (phoneNumber != null) {
                profile.setPhoneNumber(phoneNumber);
            }

            session.getTransaction().commit();
        } catch (NoSuchElementException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
