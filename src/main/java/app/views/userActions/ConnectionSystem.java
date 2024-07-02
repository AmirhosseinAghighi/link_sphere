package app.views.userActions;

import app.database.schema.Connection;
import app.services.AuthService;
import app.services.UserService;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.linkSphere.annotations.http.Delete;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.exceptions.duplicateException;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.JWT;

import java.util.NoSuchElementException;

@Endpoint("/profile")
@useGson
public class ConnectionSystem {
    private static Gson gson;

    @Post("/connection/request")
    public void requestNewConnection(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        ConnectionRequest reqData = gson.fromJson(req.getRequestBody(), ConnectionRequest.class);

        try {
            UserService.requestNewConnection(userID, reqData.getConnectedID(), reqData.getNote());
            res.sendMessage("Connection request sent successful");
        } catch (duplicateException e) {
            res.sendError(409, e.getMessage());
        } catch (IllegalArgumentException e) {
            res.sendError(400, e.getMessage());
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        }
    }

    @Post("/connection/accept")
    public void acceptConnection(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        ConnectionRequest reqData = gson.fromJson(req.getRequestBody(), ConnectionRequest.class);

        try {
            UserService.responseToConnectionRequest(userID, reqData.getConnectedID(), true);
            res.sendMessage("Connection request accepted successful");
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            res.sendError(400, e.getMessage());
        }
    }

    @Post("/connection/decline")
    public void declineConnection(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        ConnectionRequest reqData = gson.fromJson(req.getRequestBody(), ConnectionRequest.class);

        try {
            UserService.responseToConnectionRequest(userID, reqData.getConnectedID(), false);
            res.sendMessage("Connection request declined successful");
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            res.sendError(400, e.getMessage());
        }
    }

    @Delete("/connection/remove")
    public void removeConnection(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        ConnectionRequest reqData = gson.fromJson(req.getRequestBody(), ConnectionRequest.class);


        try {
            UserService.removeConnection(userID, reqData.getConnectedID());
            res.sendMessage("Connection removed successful");
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            res.sendError(400, e.getMessage());
        }
    }
}

class ConnectionRequest {
    private Long connectedID;
    private Long connectionID;
    private String note;

    public Long getConnectedID() {
        return connectedID;
    }

    public void setConnectedID(Long connectedID) {
        this.connectedID = connectedID;
    }

    public Long getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(Long connectionID) {
        this.connectionID = connectionID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}