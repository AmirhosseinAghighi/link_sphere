package app.views.userActions;

import app.database.schema.Like;
import app.services.AuthService;
import app.services.UserService;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.exceptions.notFoundException;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.JWT;
import org.linkSphere.util.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Endpoint("/posts")
@useGson
@UseLogger
public class PostsSystem {
    private static Logger logger;
    private static Gson gson;


    @Post("/new")
    public void newPost(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());

        app.database.schema.Post post = gson.fromJson(req.getRequestBody(), app.database.schema.Post.class);

        try {
            Long id = UserService.createNewPost(userID, post.getText());
            res.send(200, "{\"code\": 200, \"message\": \"New post created\", \"id\": " + id + "}");
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            res.sendError(400, e.getMessage());
        }
    }

    @Post("/{postID}/remove")
    public void removePost(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));


        String postID = req.getDynamicParameters().get("postID");
        if (postID == null || postID.isEmpty()) {
            res.sendError(400, "Invalid postID");
            return;
        }
        Long PostID = Long.parseLong(postID);

        try {
            UserService.removePost(PostID);
            res.sendMessage("Post removed");
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        } catch (ConstraintViolationException e) {
            res.sendError(400, e.getMessage());
        }
    }

    @Post("/{postID}/like")
    public void likePost(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));
        long userID = Long.parseLong(refreshTokenClaims.getSubject());
        String postID = req.getDynamicParameters().get("postID");
        if (postID == null || postID.isEmpty() || !postID.matches("^[0-9]*$")) {
            res.sendError(400, "Invalid post ID");
            return;
        }

        try {
            UserService.toggleLikePost(userID, Long.parseLong(postID));
            res.sendMessage("Post like toggled successful");
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        } catch (ConstraintViolationException e) {
            res.sendError(400, e.getMessage());
        }
    }

    @Get("/{postID}")
    public void getPost(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        String postID = req.getDynamicParameters().get("postID");
        if (postID == null || postID.isEmpty() || !postID.matches("^[0-9]*$")) {
            res.sendError(400, "Invalid post ID");
            return;
        }

        try {
            app.database.schema.Post post = UserService.getPost(Long.parseLong(postID));
            List<Like> likes = UserService.getLikes(Long.parseLong(postID));
            res.send(200, "{\"code\": 200, \"post\": " + post.toString() + ", \"likes\": " + likes.toString() + "}");
        } catch (IllegalArgumentException | ConstraintViolationException e) {
            res.sendError(400, e.getMessage());
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        }
    }

}
