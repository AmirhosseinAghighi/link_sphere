package app.views.profile;

import app.database.schema.Skill;
import app.services.AuthService;
import app.services.UserService;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.JWT;
import org.linkSphere.util.Logger;

import java.util.List;

@Endpoint("/profile")
@useGson
@UseLogger
public class Skills {
    private static Gson gson;
    private static Logger logger;

    @Post("/skills")
    public void registerNewSkill(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(403, "Authentication required");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        var skillData = gson.fromJson(req.getRequestBody(), Skill.class);

        try {
            UserService.registerNewSkill(userID, skillData);
            res.sendMessage("Successfully registered new skill");
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            res.sendError(400, "Bad Request");
        }
    }

    @Get("/{userID}/skills")
    public void getSkills(Req req, Res res) {
        long userID = Long.parseLong(req.getDynamicParameters().get("userID"));
        if (!UserService.doesUserExist(userID)) {
            res.sendError(404, "User not found.");
            return;
        }

        List<Skill> skills = UserService.getUserSkillsById(userID);
        res.send(200, "{\"code\": 200, \"skills\": " + skills.toString() + "}");
        // TODO: Find out why gson.toJson(jobs) will throw null exception ( an exception with null message )
    }

    @Post("/skills/{skillID}/remove")
    public void removeNewSkill(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(403, "Authentication required");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());
        String skillID = req.getDynamicParameters().get("skillID");

        if (skillID.isBlank()) {
            res.sendError(400, "Bad Request");
            return;
        }

        try {
            UserService.removeSkill(userID, Long.parseLong(skillID));
            res.sendMessage("Successfully removed skill");
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            res.sendError(400, "Bad Request");
        }
    }
}
