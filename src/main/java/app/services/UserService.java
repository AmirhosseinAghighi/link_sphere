package app.services;


import app.database.*;
import app.database.schema.*;
import app.global.settingsEnum.birthdayView;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Inject;

import java.util.List;
import java.util.NoSuchElementException;

public class UserService {
    @Inject(dependency = "userDAO")
    private static UserDAO userDAO;

    @Inject(dependency = "jobDAO")
    private static JobDAO jobDAO;

    @Inject(dependency = "profileDAO")
    private static ProfileDAO profileDAO;

    @Inject(dependency = "educationDAO")
    private static EducationDAO educationDAO;

    @Inject(dependency = "skillsDAO")
    private static SkillsDAO skillsDAO;

    @Inject(dependency = "contactDAO")
    private static ContactDAO contactDAO;

    public static boolean doesUserExist(long userID) {
        return userDAO.doesUserExist(userID);
    }

    public static List<Job> getUserJobsById(long userID) {
        return jobDAO.getUserJobs(userID);
    }

    public static List<Education> getUserEducationsById(long userID) {
        return educationDAO.getUserEducations(userID);
    }

    public static List<Skill> getUserSkillsById(long userID) {
        return skillsDAO.getUserSkills(userID);
    }

    public static Profile getUserProfileById(long userID) {
        return profileDAO.getUserProfile(userID);
    }

    private static boolean doesUserHaveProfile(long userID) {
        return profileDAO.doesUserHaveProfile(userID);
    }

    public static void updateUserInformation(long userID, String firstName, String lastName, String nickname, int countryCode, Long birthday, birthdayView birthdaySetting, String phoneNumber, String bio) throws NoSuchElementException {
        if (doesUserHaveProfile(userID)) {
            if (birthday != null && birthdaySetting == null) {
                birthdaySetting = birthdayView.MY_CONNECTIONS;
            }
            profileDAO.UpdateUserInformation(userID, firstName, lastName, nickname, countryCode, birthday, birthdaySetting, phoneNumber, bio);
        } else {
            if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank() || nickname == null || nickname.isBlank() || countryCode == 0) {
                throw new IllegalArgumentException("First name and Last name and nickname and country code can not be null or 0");
            }
            profileDAO.createUserProfile(userID, firstName, lastName, nickname, countryCode);
        }
    }

    public static void registerNewJobForUser(long userID, long companyID, Job jobData) throws NoSuchElementException, ConstraintViolationException {
        jobDAO.createNewJob(userID, companyID, jobData);
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
        jobDAO.updateExistingJobByID(userID, id, jobData);
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

        educationDAO.registerNewEducation(userID, education);
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

        educationDAO.updateExistingEducation(userID, id, education);
    }

    public static void registerNewSkill(long userID, Skill skill) throws IllegalArgumentException, ConstraintViolationException {
        String skillName = skill.getSkillName();
        Integer skillLevel = skill.getSkillLevel();

        if (skillName == null || skillLevel == null) {
            throw new IllegalArgumentException("Required fields not set");
        }

        skillsDAO.registerNewSkill(userID, skill);
    }

    public static void removeSkill(long userID, Long id) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException {
        skillsDAO.removeSkill(userID, id);
    }

    public static void registerNewContact(long userID, Contact contact) throws IllegalArgumentException, ConstraintViolationException {
        String type = contact.getType();
        String url = contact.getUrl();
        if (type == null || url == null) {
            throw new IllegalArgumentException("Required fields not set");
        }
        contactDAO.registerNewContact(userID, contact);
    }

    public static void removeContact(long userID, long id) throws NoSuchElementException, ConstraintViolationException, IllegalArgumentException {
        contactDAO.removeContact(userID, id);
    }
}
