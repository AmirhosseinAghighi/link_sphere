package app.controllers;

import app.database.UserDAO;
import app.database.schema.User;
import app.exceptions.InvalidCredentialsException;
import org.linkSphere.annotations.Inject;
import org.linkSphere.security.JWT;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;
import java.util.NoSuchElementException;


public class Auth {
    @Inject(dependency = "userDAO")
    private static UserDAO userDAO;

    public static String[] loginUser(String username, String password, String userAgent, String ip, String oldRefreshToken) throws InvalidCredentialsException {
        if (!checkCredentials(username, password)) {
            throw new InvalidCredentialsException();
        }

        User userData = userDAO.getUserByUsername(username);
        String[] tokens = {generateAccessToken(userData.getId()), generateRefreshToken(userData.getId(), userData.getUsername())};
        if (oldRefreshToken == null) {
            userDAO.RegisterNewRefreshToken(tokens[1], userAgent, ip, userData);
        } else {
            try {
                userDAO.UpdateUserRefreshToken(userData.getId(), tokens[1], userAgent, ip, oldRefreshToken);
            } catch (NoSuchElementException e) {
                userDAO.RegisterNewRefreshToken(tokens[1], userAgent, ip, userData);
            }
        }

        return tokens;
    }

    public static void updateUserRefreshToken(long userID, String userAgent, String ip, String oldRefreshToken, String newRefreshToken) throws NoSuchElementException {
        userDAO.UpdateUserRefreshToken(userID, newRefreshToken, userAgent, ip, oldRefreshToken);
    }

    private static boolean checkCredentials(String username, String password) {
        String dbPassword = userDAO.getUserPasswordByUsername(username);
        return BCrypt.checkpw(password, dbPassword);
    }

    public static String generateAccessToken(long userID) {
        return JWT.generateAccessTokenByUser(userID);
    }

    public static String generateRefreshToken(long userID, String username) {
        return JWT.generateRefreshTokenByUser(userID, username);
    }

    public static boolean isAuthorized(Map<String, String> cookies) {
        String accessToken = cookies.get("accessToken");
        if (accessToken == null) return false;
        boolean res = JWT.verifyToken(accessToken);
        return res;
    }

    public static boolean isRefreshTokenValid(Map<String, String> cookies) {
        String refreshToken = cookies.get("refreshToken");
        if (refreshToken == null) return false;
        boolean res = JWT.verifyToken(refreshToken);
        return res;
    }
}

class Cookies {
    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

