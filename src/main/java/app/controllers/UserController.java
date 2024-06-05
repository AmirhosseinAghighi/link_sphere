package app.controllers;


import app.database.UserDAO;
import org.linkSphere.annotations.Inject;
import org.linkSphere.database.DAO;

public class UserController {
    @Inject(dependency = "userDAO")
    private static UserDAO userDao;
    public static boolean doesUserExist(long userID) {
        return userDao.doesUserExist(userID);
    }
}
