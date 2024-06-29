package app.views.auth;

import app.services.AuthService;
import app.exceptions.InvalidCredentialsException;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.util.Logger;

import java.util.NoSuchElementException;

@Endpoint("/login")
@useGson
@UseLogger
public class Login {
    private static final Log log = LogFactory.getLog(Login.class);
    private static Gson gson;
    private static Logger logger;

    @Post
    public void post(Req req, Res res) {
        try {
            if (AuthService.isAuthorized(req.getCookies())) {
                res.sendError(403, "You already logged in");
                return;
            }

            logger.debug("user wasn't logged in");

            var reqData = gson.fromJson(req.getRequestBody(), loginReqBody.class);
            String username = reqData.getUsername();
            String password = reqData.getPassword();

            if (username.isBlank() || password.isBlank()) {
                res.sendError(400, "Bad Request");
                return;
            }

            // TODO: SAVE JWT TOKEN TO DATA BASE ( we should add some new parameters in request body such as device information to save on this stage )
            // TODO: for saving jwt tokens, we should search for solutions to auto remove jwt token after expiration on database! Good Luck
            String[] tokens = AuthService.loginUser(username, password, req.getUserAgent(), req.getIp(), req.getCookies().get("refreshToken"));
            res.addCookie("accessToken", tokens[0], true);
            res.addCookie("refreshToken", tokens[1], true);
            res.sendMessage("Logged in successfully");

        } catch (NoSuchElementException e) {
            logger.debug("User not found: ", "ip: ", req.getIp());
            res.sendError(401, "invalid credentials");
        } catch (InvalidCredentialsException e) {
            res.sendError(401, "invalid credentials");
        } catch (Exception e) {
            logger.debug("New User Login Request Received And Ignored! | message: ", e.getMessage(), "ip: ", req.getIp());
            res.sendError(400, "Bad Request");
        }
    }
}

class loginReqBody {
    private String username;
    private String password;

    public loginReqBody(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
