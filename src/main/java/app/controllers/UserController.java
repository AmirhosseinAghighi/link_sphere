package app.controllers;


import app.database.UserDAO;
import org.linkSphere.annotations.Inject;
import org.linkSphere.database.DAO;

import java.util.NoSuchElementException;

public class UserController {
    @Inject(dependency = "userDAO")
    private static UserDAO userDao;
    public static boolean doesUserExist(long userID) {
        return userDao.doesUserExist(userID);
    }

    public static void updateUserInformation(long userID, String firstName, String lastName, String nickname, int countryCode) throws NoSuchElementException {
        userDao.UpdateUserInformation(userID, firstName, lastName, nickname, countryCode);
    }
}
