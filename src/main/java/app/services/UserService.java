package app.services;


import app.database.EducationDAO;
import app.database.JobDAO;
import app.database.ProfileDAO;
import app.database.UserDAO;
import app.database.schema.Education;
import app.database.schema.Job;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Inject;

import java.util.IllformedLocaleException;
import java.util.List;
import java.util.NoSuchElementException;

public class UserService {
    @Inject(dependency = "userDAO")
    private static UserDAO userDao;

    @Inject(dependency = "jobDAO")
    private static JobDAO jobDao;

    @Inject(dependency = "profileDAO")
    private static ProfileDAO profileDao;

    @Inject(dependency = "educationDAO")
    private static EducationDAO educationDao;

    public static boolean doesUserExist(long userID) {
        return userDao.doesUserExist(userID);
    }

    public static List<Job> getUserJobsById(long userID) {
        return jobDao.getUserJobs(userID);
    }

    public static List<Education> getUserEducationsById(long userID) {
        return educationDao.getUserEducations(userID);
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

    public static void updateExistingJob(Long userID, Long id, Job jobData) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException, IllegalAccessError {
        String title = jobData.getTitle();
        String description = jobData.getDescription();
        Long company = jobData.getCompany();
        Long startDate = jobData.getStartDate();
        Long endDate = jobData.getEndDate();

        if ((title == null && description == null && company == null && startDate == null && endDate == null)) {
            throw new IllegalArgumentException();
        }
        jobDao.updateExistingJobByID(userID, id, jobData);
    }

    public static void registerNewEducation(long userID, Education education) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException {
        String institutionName = education.getInstitutionName();
        String degree = education.getDegree();
        String fieldOfStudy = education.getFieldOfStudy();
        Long startDate = education.getStartDate();
        Long endDate = education.getEndDate();

        boolean endDateCheckup = startDate != null && endDate != null && startDate >= endDate;
        if (institutionName == null || degree == null || fieldOfStudy == null || startDate == null || endDateCheckup) {
            throw new IllegalArgumentException("Required fields not set");
        }

        educationDao.registerNewEducation(userID, education);
    }

    public static void updateExistingEducation(long userID, long id, Education education) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException {
        String institutionName = education.getInstitutionName();
        String degree = education.getDegree();
        String fieldOfStudy = education.getFieldOfStudy();
        Long startDate = education.getStartDate();
        Long endDate = education.getEndDate();

        if (institutionName == null && degree == null && fieldOfStudy == null && startDate == null && endDate == null) {
            throw new IllegalArgumentException("at lease one field shouldn't be null.");
        }

        educationDao.updateExistingEducation(userID, id, education);
    }
}
