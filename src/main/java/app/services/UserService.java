package app.services;


import app.database.JobDao;
import app.database.ProfileDAO;
import app.database.UserDAO;
import app.database.schema.Job;
import app.database.schema.User;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Inject;

import java.util.List;
import java.util.NoSuchElementException;

public class UserService {
    @Inject(dependency = "userDAO")
    private static UserDAO userDao;

    @Inject(dependency = "jobDAO")
    private static JobDao jobDao;

    @Inject(dependency = "profileDAO")
    private static ProfileDAO profileDao;

    public static boolean doesUserExist(long userID) {
        return userDao.doesUserExist(userID);
    }

    public static List<Job> getUserJobsById(long userID) {
        return jobDao.getUserJobs(userID);
    }

    private static boolean doesUserHaveProfile(long userID) {
        return profileDao.doesUserHaveProfile(userID);
    }

    public static void updateUserInformation(long userID, String firstName, String lastName, String nickname, int countryCode, String bio) throws NoSuchElementException {
        if (doesUserHaveProfile(userID)) {
            profileDao.UpdateUserInformation(userID, firstName, lastName, nickname, countryCode, bio);
        } else {
            if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank() || nickname == null || nickname.isBlank() || countryCode == 0) {
                throw new IllegalArgumentException("First name and Last name and nickname and country code can not be null or 0");
            }
            profileDao.createUserProfile(userID, firstName, lastName, nickname, countryCode);
        }
    }

    public static void registerNewJobForUser(long userID, long companyID, Job jobData) throws NoSuchElementException, ConstraintViolationException {
        jobDao.createNewJob(userID, companyID, jobData);
    }

    public static void updateExistingJob(Long id, Job jobData) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException {
        jobDao.updateExistingJobByID(id, jobData);
    }
}
