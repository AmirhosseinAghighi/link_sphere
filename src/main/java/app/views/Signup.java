package app.views;

import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useDAO;
import org.linkSphere.annotations.useGson;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.database.DAO;
import org.linkSphere.database.schema.User;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.Session;
import org.linkSphere.util.Logger;
import org.mindrot.jbcrypt.BCrypt;

@Endpoint("/signup")
@useGson
@UseLogger
@useDAO
public class Signup {
    private static Gson gson;
    private static Logger logger;
    private static DAO dao;


    @Post
    public void post(Req req, Res res) {
        try {
            var reqData = gson.fromJson(req.getRequestBody(), signUpUser.class);
            String username = reqData.getUsername();
            String mail = reqData.getMail();
            String password = reqData.getPassword();
            String firstname = reqData.getFirstname();
            String lastname = reqData.getLastname();

            logger.info("New User Sign Up Request Received {", username, " ", mail, " ", password, "}");

            if (username.isBlank() || mail.isBlank() || password.isBlank() || firstname.isBlank() || password.length() < 8) {
                logger.info("New User Sign Up Request Received And Ignored! | message: a field was blank!", "ip: ", req.getIp());
                res.sendError(400, "Bad Request");
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            var user = new User(username, mail, hashedPassword, firstname, lastname);

            try {
                dao.createNewUser(user);
            } catch (ConstraintViolationException error) {
                if (error.getKind() == ConstraintViolationException.ConstraintKind.UNIQUE) {
                    res.sendError(400, "username or email already exist.");
                    return;
                }
            }
            res.sendMessage("user signed up successfully");
            logger.info("New user signed up successfully");
        } catch (Exception e) {
            logger.info("New User Sign Up Request Received And Ignored! | message: ", e.getMessage(), "ip: ", req.getIp());
            res.sendError(400, "Bad Request");
        }
    }

    @Get
    public void get(Req req, Res res) {
        res.send(200, "Hi! \nThis is the Sign Up Page :)");
    }
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