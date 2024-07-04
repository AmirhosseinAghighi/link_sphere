package app.views.auth;

import app.database.UserDAO;
import com.google.gson.Gson;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.Inject;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.annotations.UseLogger;
import app.database.schema.User;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.util.Logger;
import org.mindrot.jbcrypt.BCrypt;

@Endpoint("/signup")
@useGson
@UseLogger
public class Signup {
    private static Gson gson;
    private static Logger logger;
    @Inject(dependency = "userDAO")
    private static UserDAO userDAO;

    // TODO: ADD EMAIL CONFIRMATION WITH OPT CODE SENT TO MAIL WITH HTML TEMPLATE.
    @Post
    public void post(Req req, Res res) {
        try {
            var reqData = gson.fromJson(req.getRequestBody(), signUpUser.class);
            String username = reqData.getUsername();
            String mail = reqData.getMail();
            String password = reqData.getPassword();
            String firstname = reqData.getFirstname();
            String lastname = reqData.getLastname();

            logger.debug("New User Sign Up Request Received {", username, " ", mail, " ", password, "}");

            if (username.isBlank() || mail.isBlank() || password.isBlank() || firstname.isBlank() || password.length() < 8) { // check form validation
                logger.debug("New User Sign Up Request Received And Ignored! | message: a field was blank!", "ip: ", req.getIp());
                res.sendError(400, "Bad Request");
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); // hash password to make it secure to save
            var user = new User(username, mail, hashedPassword, firstname, lastname, 0); // create user instance to save it in database.

            try { // put it in try catch to catch exception and send it to client
                userDAO.createNewUser(user); // create new one and save it.
            } catch (ConstraintViolationException error) {
                if (error.getKind() == ConstraintViolationException.ConstraintKind.UNIQUE) { // catch duplicated username or mail
                    res.sendError(400, "username or email already exist."); // send error with 400 status code ( Bad Request )
                    return; // return to avoid sending another response
                }
            }
            res.sendMessage("user signed up successfully"); // if passed the user creation, send the result to client. TODO: login and pass the jwt token to user
            logger.info("New user signed up successfully");
        } catch (Exception e) { // catch any error received like username or mail or password or first name were null.
            logger.debug("New User Sign Up Request Received And Ignored! | message: ", e.getMessage(), "ip: ", req.getIp());
            res.sendError(409, "Bad Request");
        }
    }

    @Get
    public void get(Req req, Res res) {
        res.send(200, "Hi! \nThis is the Sign Up Page :)");
    } // currently, nothing to send
}

class signUpUser {
    private String username;
    private String mail;
    private String password;
    private String firstname;
    private String lastname;

    public String getUsername() {
        return username;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}