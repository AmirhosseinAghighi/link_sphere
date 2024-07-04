package app.views;

import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;

@Endpoint("/404")
public class notFound {
    @Get
    public void get(Req req, Res res) {
        res.send(404, "{\"code\":404,\"message\":\"404 - Custom Not Found Page\"}");
    }
}