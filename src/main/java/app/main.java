package app;

import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.core.Sphere;
import org.linkSphere.database.DAO;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.util.Logger;

@UseLogger
@useDAO
public class main {
    private static Logger logger;
    private static DAO dao;
    public static void main(String[] args) {
        try {
            Sphere.setDebug(true);
            Sphere.start(3000, main.class);
        } catch (Exception e) {
            if (logger == null)
                System.out.println(e.getMessage());
            else
                logger.critical(e.getMessage());
        }
    }
}


/*
    API DOCUMENTATION

    1. Authentication
        - POST `/login`: login user
            |
            - Request body schema: {"username": "", "password": ""}
            |
            - Response: 2 jwt token as cookie with json response:
                | 200: {"code": 200, "message": "Logged in successfully"}
                | 401: {"code": 401, "message": "invalid credentials"}
                | 403: {"code": 403, "message": "You already logged in"}

        - POST `/signup`: signup new user
            |
            - Request body schema: {"username": "", "password": "", "mail": "", "firstname": ""}
            |
            - Response: json responses:
                | 200: {"code": 200, "message": "user signed up successfully"}
                | 400: {"code": 400, "message": "username or email already exist."}
                | 401: {} -- IN PROGRESS

       - POST `/refresh`: refresh access token ( in some cases it will refresh the refresh token )
            |
            - Request body schema: empty
            |
            - Response: refreshing tokens and json responses:
                | 200: {"code": 200, "message": "Access token renewed."}
                | 401: {"code": 401, "message": "Invalid refresh token"}
                | 403: {"code": 403, "message": "Access token has enough time"}

    *+*===========================================================================================*+*

    2. Profile
        - POST `/profile/photo`: updating user profile picture
            |
            - Request Body Schema: binary file ( "image" as key and image file as value )
            |
            - Response: json responses;
                | 403: {"code: 403", "Not authorized"} => if access token is invalid
                | 400: {"code: 403", "Bad Request"} => if uploaded file is not png or jpg
                | 403: {"code: 500", "Internal Server Error"} => if there is problem in saving file

 */