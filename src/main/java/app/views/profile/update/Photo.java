package app.views.profile.update;

import app.controllers.Auth;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;

@Endpoint("/profile/update/photo")
class Photo {
    @Post
    public void post(Req req, Res res) {
        String token = req.getCookies().get("accessToken");
        if (Auth.isAuthorized(req.getCookies())) {
            res.sendError(403, "Not authorized");
            return;
        }
    }
}
