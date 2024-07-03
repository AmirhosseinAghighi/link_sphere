package app.views.userActions;

import app.database.schema.Like;
import app.services.AuthService;
import app.services.UserService;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.hibernate.exception.ConstraintViolationException;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.annotations.useGson;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.JWT;

import java.util.List;
import java.util.NoSuchElementException;

@Endpoint("/posts")
@useGson
public class PostsSystem {
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
            UserService.createNewPost(userID, post.getText());
            res.sendMessage("Post created");
        } catch (NoSuchElementException e) {
            res.sendError(404, e.getMessage());
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            res.sendError(400, e.getMessage());
        }
    }

    @Post("/remove")
    public void removePost(Req req, Res res) {
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(401, "Unauthorized");
            return;
        }

        Claims refreshTokenClaims = JWT.parseToken(req.getCookies().get("accessToken"));

        app.database.schema.Post post = gson.fromJson(req.getRequestBody(), app.database.schema.Post.class);

        try {
            UserService.removePost(post.getId());
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
