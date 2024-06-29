package app.views.profile;

import app.controllers.Auth;
import app.controllers.UserController;
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
        if (!Auth.isAuthorized(req.getCookies())) {
            res.sendError(403, "Authentication required");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        ProfileInformationJson data = gson.fromJson(req.getRequestBody(), ProfileInformationJson.class);
        String firstName = data.getFirstName();
        String lastName = data.getLastName();
        String nickName = data.getNickName();
        int countryCode = data.getCountry();
        if ((firstName == null || firstName.length() > 20) || (lastName == null || lastName.length() > 40) || (nickName == null || nickName.length() > 40) || (CountryCode.getByCode(countryCode) == null)) {
            res.sendError(400, "Bad Request");
            return;
        }

        try {
            UserController.updateUserInformation(userID, firstName, lastName, nickName, countryCode);
            res.sendMessage("Successfully updated profile");
            logger.debug("Updating profile");
        } catch (NoSuchElementException e) {
            res.sendError(404, "User not found");
        }
    }
}


class ProfileInformationJson {
    private String firstName;
    private String lastName;
    private String nickName;
    private int country;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public int getCountry() {
        return country;
    }
}