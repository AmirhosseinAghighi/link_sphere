package app;

import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
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

@Endpoint("/")
class hi {
    @Get
    public void get(Req req, Res res) {
        res.send(200, "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Hello World</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Hello World</h1>\n" +
                "<h2>Welcome to Sphere framework ;)</h2>\n" +
                "<h4>Quick start: </h4>\n" +
                "</body>\n" +
                "</html>", "text/html; charset=utf-8");
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
                | 500: {"code: 500", "Internal Server Error"} => if there is problem in saving file

        - GET `/profile/{userID}/photo`: Get user profile picture
            |
            - Request Body Schema: None
            |
            - Response: (200) Binary file ( profile picture in jpg or png format )

        - POST `/profile/banner`: updating user banner picture
            |
            - Request Body Schema: binary file ( "image" as key and image file as value )
            |
            - Response: json responses;
                | 403: {"code: 403", "Not authorized"} => if access token is invalid
                | 400: {"code: 403", "Bad Request"} => if uploaded file is not png or jpg
                | 500: {"code: 500", "Internal Server Error"} => if there is problem in saving file

        - GET `/profile/{userID}/banner`: Get user banner picture
            |
            - Request Body Schema: None
            |
            - Response: (200) Binary file ( banner picture in jpg or png format )

        - POST `/profile/update`: updating profile information such as firstname, lastname, nickname, country and bio
            |
            - Request Body Schema: {"firstName": "test", "lastName": "test", "nickname": "test", countryCode: 364, "bio": "test"}
                | firstName, lastName, nickname, countryCode are required for first time ( it will create the profile row in db )
            |
            - Response: json responses;
                | 400: {"code": 403, message: "Bad Request"} => if required field are null or empty, or they're exceeding maximum limit.
                | 404: {"code": 404, message: "User not found."} => if user not found ( why it should happen really ?!

        - POST `/profile/jobs`: create job on profile
            |
            - Request Body Schema:
                | if we want to create new one that we are currently working: {"title": "Front-end Developer", "company": 1, "startDate": 1593439663, "description": "test"}
                | if we want to create new one that we aren't currently working: {"title": "Front-end Developer", "company": 1, "startDate": 1593439663, "endDate": 589043279852, "description": "test"}
            |
            - Response: json responses:
                | 400: {"code": 400, message: Bad Request"}
                | 404: {"code": 404, message: "%s not found"} -> %s can be User (profile) or Company or job ( if we are editing )

        - PUT `/profiles/jobs/{jobID}`: update existing job by id
            |
            - Request Body Schema:
                | if we want to edit existing one ( only edited fields: {"title": "Back-end Developer", "description": "test", id = 321}
            |
            - Response: json responses:
                | 400: {"code": 400, message: Bad Request"}
                | 404: {"code": 404, message: "%s not found"} -> %s can be User (profile) or Company or job ( if we are editing )
 */