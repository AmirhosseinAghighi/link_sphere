package app.views.profile;

import app.database.schema.*;
import app.global.settingsEnum.BirthdayView;
import app.services.AuthService;
import app.services.UserService;
import app.global.CountryCode;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.http.dto.Res;
import org.linkSphere.http.dto.Req;
import org.linkSphere.security.JWT;
import org.linkSphere.util.Logger;

import java.util.List;
import java.util.NoSuchElementException;

@Endpoint("/profile")
@UseLogger
@useGson
public class ProfileInformation {
    private static Gson gson;
    private static Logger logger;

    @Get("/{userID}")
    public void getProfile(Req req, Res res) {
        long userID = Long.parseLong(req.getDynamicParameters().get("userID"));
        if (!UserService.doesUserExist(userID)) {
            res.sendError(404, "User not found.");
            return;
        }

        if (!UserService.doesUserHaveProfile(userID)) {
            res.sendError(404, "User Profile not found.");
            return;
        }

        List<Job> jobs = UserService.getUserJobsById(userID);
        List<Education> educations = UserService.getUserEducationsById(userID);
        List<Skill> skills = UserService.getUserSkillsById(userID);

        // TODO: this option should be limited to loged in users to handle birthday and ... settings
        Profile profile = UserService.getUserProfileById(userID);

        res.send(200, "{\"code\": 200" +
                ", \"jobs\": " + jobs.toString() +
                ", \"educations\": "+ educations.toString() +
                ", \"skills\": " + skills.toString() +
                ", \"profile\": " + profile.toString() +
                "}");
    }

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
        Long birthday = data.getBirthday();
        BirthdayView birthdaySetting = data.getBirthdaySetting();
        String phoneNumber = data.getPhoneNumber();
        String bio = data.getBio();
        if ((firstName != null && firstName.length() > 20) || (lastName != null && lastName.length() > 40) || (nickName != null && nickName.length() > 40) || (countryCode != 0 && CountryCode.getByCode(countryCode) == null) || (bio != null && bio.length() > 220) || (phoneNumber != null && !phoneNumber.matches("[0-9]+"))) {
            res.sendError(400, "Bad Request");
            return;
        }

        try {
            UserService.updateUserInformation(userID, firstName, lastName, nickName, countryCode, birthday, birthdaySetting, phoneNumber, bio);
            res.sendMessage("Successfully updated profile");
            logger.debug("Updating profile");
        } catch (NoSuchElementException e) {
            res.sendError(404, "User not found");
        } catch (IllegalArgumentException e) {
            res.sendError(400, "Bad Request");
        }
    }
}