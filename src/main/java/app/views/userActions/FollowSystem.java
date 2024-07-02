package app.views.userActions;

import app.services.AuthService;
import app.services.UserService;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.linkSphere.annotations.http.Delete;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.JWT;

import java.util.DuplicateFormatFlagsException;
import java.util.HashMap;
import java.util.NoSuchElementException;

@Endpoint("/profile")
@useGson
public class FollowSystem {
    private static Gson gson;

    @Post("/follow/{userID}")
    public void followUser(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());
        String followingID = req.getDynamicParameters().get("userID");

        if (followingID == null || followingID.isBlank()) {
            res.sendError(400, "Bad Request");
            return;
        }

        try {
            UserService.registerNewFollowing(userID, Long.parseLong(followingID));
            res.sendMessage("Successfully followed user " + userID);
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            res.sendError(400, "Bad Request");
        }
    }

    @Delete("/unfollow/{userID}")
    public void unfollowUser(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());
        String followingID = req.getDynamicParameters().get("userID");
        HashMap<String, String> queries = req.getQueryParameters();
        boolean byFollowID = queries.containsKey("followID");

        if (followingID == null || followingID.isBlank()) {
            res.sendError(400, "Bad Request");
            return;
        }


        try {
            if (!byFollowID || queries.get("followID").equals("false")) {
                UserService.removeFollowing(userID, Long.parseLong(followingID));
            } else {
                UserService.removeFollowing(Long.parseLong(followingID)); // followingID is follow id here... if you know better naming, suggest...
            }
            res.sendMessage("Successfully unfollowed user");
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e instanceof DuplicateFormatFlagsException) {
                res.sendError(409, e.getMessage());
            } else {
                res.sendError(400, "Bad Request: " + e.getMessage());
            }
        }
    }
}
