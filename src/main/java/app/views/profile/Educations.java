package app.views.profile;

import app.database.schema.Education;
import app.database.schema.Job;
import app.services.AuthService;
import app.services.UserService;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.http.Put;
import org.linkSphere.annotations.useGson;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.JWT;
import org.linkSphere.util.Logger;

import java.util.List;
import java.util.NoSuchElementException;

@Endpoint("/profile")
@useGson
@UseLogger
public class Educations {
    private static final Log log = LogFactory.getLog(Educations.class);
    private static Gson gson;
    private static Logger logger;

    @Post("/educations")
    public void registerNewEducation(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());
        Education education = gson.fromJson(req.getRequestBody(), Education.class);

        try {
            UserService.registerNewEducation(userID, education);
            res.sendMessage("Successfully registered new education");
        } catch (IllegalArgumentException | ConstraintViolationException e) {
            res.sendError(400, "Bad Request");
        } catch (NoSuchElementException e) {
            res.sendError(401, "User profile is not completed.");
        }
    }

    @Get("/{userID}/educations")
    public void getEducations(Req req, Res res) {
        long userID = Long.parseLong(req.getDynamicParameters().get("userID"));
        if (!UserService.doesUserExist(userID)) {
            res.sendError(404, "User not found.");
            return;
        }

        List<Education> educations = UserService.getUserEducationsById(userID);
        res.send(200, "{\"code\": 200, \"educations\": " + educations.toString() + "}");
    }

    @Put("/educations/{educationID}")
    public void educationUpdate(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        var educationData = gson.fromJson(req.getRequestBody(), Education.class);
        String id = req.getDynamicParameters().get("educationID");

        if (id.isBlank()) {
            res.sendError(400, "Bad Request");
            return;
        }

        try {
            UserService.updateExistingEducation(userID, Long.parseLong(id), educationData);
            res.sendMessage("Successfully updated education");
        } catch (IllegalArgumentException | ConstraintViolationException e) {
            res.sendError(400, "Bad Request");
        } catch (NoSuchElementException e) {
            res.sendError(401, e.getMessage());
        }
    }
}
