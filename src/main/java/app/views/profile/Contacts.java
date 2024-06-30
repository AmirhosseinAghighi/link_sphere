package app.views.profile;

import app.database.schema.Contact;
import app.services.AuthService;
import app.services.UserService;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Delete;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.JWT;
import org.linkSphere.util.Logger;

@Endpoint("/profile")
@useGson
@UseLogger
public class Contacts {
    private static Gson gson;
    private static Logger logger;

    @Post("/contacts")
    public void registerNewContact(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(403, "Authentication required");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        var contactData = gson.fromJson(req.getRequestBody(), Contact.class);

        try {
            UserService.registerNewContact(userID, contactData);
            res.sendMessage("Successfully registered new contact");
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            res.sendError(400, "Bad Request");
        }
    }

    @Delete("/contacts/{contactID}")
    public void deleteContact(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(403, "Authentication required");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());
        String contactID = req.getDynamicParameters().get("contactID");

        if (contactID.isBlank()) {
            res.sendError(400, "Bad Request");
            return;
        }

        try {
            UserService.removeContact(userID, Long.parseLong(contactID));
            res.sendMessage("Successfully removed contact");
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            res.sendError(400, "Bad Request");
        }
    }
}
