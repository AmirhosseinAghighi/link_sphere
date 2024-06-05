package app.views;

import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.exceptions.notFoundException;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;

import java.io.File;
import java.io.IOException;

@Endpoint("/public/[path]")
class publicHandler {
    @Get
    public void get(Req req, Res res) throws notFoundException, IOException {
        String publicPath = req.getDynamicParameters().get("path");
        String path = "src/main/java/app/public/" + (publicPath.substring(0, publicPath.length() - 1));
        File file = new File(path);
        if (!file.exists()) {
            res.sendError(404, "not found");
            return;
        }
        String[] fileNameParts = file.getName().split("\\.");
        String fileExtention = fileNameParts[fileNameParts.length - 1];
        String conentType = "";
        if (fileExtention.equals("png")) {
            conentType = "image/png";
        } else if (fileExtention.equals("jpg")) {
            conentType = "image/jpg";
        } else if (fileExtention.equals("ico")) {
            conentType = "image/vnd.microsoft.icon";
        } else {
            res.sendError(404, "not found");
            return;
        }

        try {
            res.sendFile(file, conentType);
        } catch (notFoundException e) {
            res.sendError(500, "Internal Server Error");
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }
}


@Endpoint("/favicon.ico")
class favHandler {
    @Get
    public void get(Req req, Res res) {
        res.redirect("/public/favicon.ico");
    }
}