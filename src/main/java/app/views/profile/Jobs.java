package app.views.profile;

import app.database.schema.User;
import app.services.AuthService;
import app.services.UserService;
import app.database.schema.Job;
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
public class Jobs {
    private static Gson gson;
    private static Logger logger;

    @Post("/jobs")
    public void registerJob(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(403, "Authentication required");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        var jobData = gson.fromJson(req.getRequestBody(), Job.class);
        String title = jobData.getTitle();
        Long company = jobData.getCompany();
        Long startDate = jobData.getStartDate();
        Long endDate = jobData.getEndDate();
        if (title == null || company == null || startDate == null || (endDate != null && endDate <= startDate)) {
            res.sendError(400, "Bad Request");
            return;
        }

        try {
            UserService.registerNewJobForUser(userID, company, jobData);
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
            return;
        } catch (ConstraintViolationException e) {
            res.sendError(400, "Bad Request");
            logger.critical("Unexpected ConstraintViolationException: ", e, " values: ", jobData);
            return;
        }
        res.sendMessage("Added Successfully");
    }

    @Get("/{userID}/jobs")
    public void getUserJobs(Req req, Res res) {
        long userID = Long.parseLong(req.getDynamicParameters().get("userID"));
        if (!UserService.doesUserExist(userID)) {
            res.sendError(404, "User not found.");
            return;
        }

        List<Job> jobs = UserService.getUserJobsById(userID);
        res.sendMessage(jobs.toString());
        // TODO: Find out why gson.toJson(jobs) will throw null exception ( an exception with null message )
    }

    @Put("/jobs/{jobID}")
    public void updateExistingJob(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(403, "Authentication required");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        var jobData = gson.fromJson(req.getRequestBody(), Job.class);
        String title = jobData.getTitle();
        String description = jobData.getDescription();
        Long company = jobData.getCompany();
        Long startDate = jobData.getStartDate();
        Long endDate = jobData.getEndDate();
        String id = req.getDynamicParameters().get("jobID");

        if ((title == null && description == null && company == null && startDate == null && endDate == null) || id.isBlank()) {
            res.sendError(400, "Bad Request");
            return;
        }

        try {
            UserService.updateExistingJob(Long.parseLong(req.getDynamicParameters().get("jobID")), jobData);
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
            return;
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            res.sendError(400, "Bad Request");
            return;
        }
        res.sendMessage("Updated Successfully");
    }

}