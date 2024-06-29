package app.views.profile;

import app.services.AuthService;
import app.services.UserService;
import app.database.schema.Profile;
import app.global.CountryCode;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.http.dto.Res;
import org.linkSphere.http.dto.Req;
import org.linkSphere.security.JWT;
import org.linkSphere.util.Logger;

import java.util.NoSuchElementException;

@Endpoint("/profile")
@UseLogger
@useGson
public class ProfileInformation {
    private static Gson gson;
    private static Logger logger;
    @Post("/update")
    public void updateProfile(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(403, "Authentication required");
            return;
        }

        Claims accessToken = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(accessToken.getSubject());

        Profile data = gson.fromJson(req.getRequestBody(), Profile.class);
        String firstName = data.getFirstName();
        String lastName = data.getLastName();
        String nickName = data.getNickName();
        int countryCode = data.getCountryCode();
        String bio = data.getBio();
        if ((firstName != null && firstName.length() > 20) || (lastName != null && lastName.length() > 40) || (nickName != null && nickName.length() > 40) || (countryCode != 0 && CountryCode.getByCode(countryCode) == null) || (bio != null && bio.length() > 220)) {
            res.sendError(400, "Bad Request");
            return;
        }

        try {
            UserService.updateUserInformation(userID, firstName, lastName, nickName, countryCode, bio);
            res.sendMessage("Successfully updated profile");
            logger.debug("Updating profile");
        } catch (NoSuchElementException e) {
            res.sendError(404, "User not found");
        } catch (IllegalArgumentException e) {
            res.sendError(400, "Bad Request");
        }
    }
}