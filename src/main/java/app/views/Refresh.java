package app.views;

import app.controllers.Auth;
import io.jsonwebtoken.Claims;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.JWT;

import java.util.Date;
import java.util.NoSuchElementException;

@Endpoint("/refresh")
public class Refresh {
    @Post
    public void post(Req req, Res res) {
        // TODO: checking is this refresh token in black list ?
        if (!Auth.isRefreshTokenValid(req.getCookies())) {
            res.sendError(403, "Invalid refresh token");
            return;
        }

        Date now = new Date();
        String refreshToken = req.getCookies().get("refreshToken");
        Claims refreshTokenClaims = JWT.parseToken(refreshToken);
        long userID = Long.parseLong(refreshTokenClaims.getSubject());
        if (refreshTokenClaims.getExpiration().getTime() - now.getTime() < 1 * 24 * 60 * 60 * 1000) {
            String newRefreshToken = Auth.generateRefreshToken(userID, (String) refreshTokenClaims.get("username"));
            res.addCookie("refreshToken", newRefreshToken, true);

            try {
                Auth.updateUserRefreshToken(userID, req.getUserAgent(), req.getIp(), refreshToken, newRefreshToken);
            } catch (NoSuchElementException e) {
                res.addCookie("refreshToken", null, true);
                res.addCookie("accessToken", null, true);
                res.sendError(403, "Invalid refresh token");
            }

        }

        Claims accessToken = JWT.parseToken(req.getCookies().get("accessToken"));
        Date accessTokenExp = accessToken.getExpiration();
        if (accessTokenExp.getTime() - now.getTime() < 60 * 1000) {
            String newAccessToken = Auth.generateAccessToken(userID);
            res.addCookie("accessToken", newAccessToken, true);
            res.sendMessage("Access token renewed.");
        } else {
            res.sendMessage("Access token has enough time");
        }
    }
}