package app.views.profile;

import app.controllers.Auth;
import app.controllers.UserController;
import org.linkSphere.annotations.UseLogger;
import org.linkSphere.annotations.http.Endpoint;
import org.linkSphere.annotations.http.Get;
import org.linkSphere.annotations.http.Post;
import org.linkSphere.exceptions.notFoundException;
import org.linkSphere.http.dto.Req;
import org.linkSphere.http.dto.Res;
import org.linkSphere.security.JWT;
import org.linkSphere.util.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;

@Endpoint("/profile")
@UseLogger
public class ProfileMedia {
    private static Logger logger;
    // as we want only accept these two format, we should check the binary file that we get
    // PNG signature
    private static final byte[] PNG_SIGNATURE =
            new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    // JPG signature (common case)
    private static final byte[] JPG_SIGNATURE =
            new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};


    @Get("/{userID}/photo")
    public void getPhoto(Req req, Res res) throws IOException, notFoundException {
        String id = req.getDynamicParameters().get("userID");
        System.out.println(UserController.doesUserExist(Long.parseLong(id)));
        if (!UserController.doesUserExist(Long.parseLong(id))) {
            res.sendError(404, "User not found");
            return;
        }

        String path = "src/main/java/app/assets/users/" + req.getDynamicParameters().get("userID") + "/profiles";
        File directory = new File(path);
        if (!directory.exists() || (directory.listFiles() != null && directory.listFiles().length == 0)) {
            File image = new File("src/main/java/app/assets/users/default/profiles/1.png");
            try {
                res.sendFile(image, "image/png");
            } catch (notFoundException e) {
                res.sendError(500, "Internal Server Error");
                throw e;
            }
        } else {
            File[] imageFiles = directory.listFiles();
            Arrays.sort(imageFiles, Comparator.comparingInt(file -> {
                String fileName = file.getName();
                String numericPart = fileName.replaceAll("[^0-9]", ""); // Extract numeric part
                return Integer.parseInt(numericPart);
            }));

            try {
                res.sendFile(imageFiles[imageFiles.length - 1], "image/png");
            } catch (notFoundException e) {
                res.sendError(500, "Internal Server Error");
                throw e;
            }
        }
    }


    @Post("/photo")
    public void post(Req req, Res res) throws IOException {
        String token = req.getCookies().get("accessToken");
        if (!Auth.isAuthorized(req.getCookies())) {
            res.sendError(403, "Not authorized");
            return;
        }

        byte[] file = req.getRequestBodyAsByteArray();
        String fileFormat = getImageType(file);

        if (fileFormat.equals("none")) {
            res.sendError(400, "Bad Request");
        }

        BufferedImage buf = ImageIO.read(new ByteArrayInputStream(file));
        double fileSize = (double) file.length / 1024;
        logger.debug("image received for profile photo with ", buf.getWidth() + "x" + buf.getHeight(), " resolution and ", fileSize);
        if (fileSize > 1024) {
            res.sendError(400, "Uploaded image size exceeds the maximum limit of 2MB.");
            return;
        } else if (buf.getWidth() > 512 || buf.getHeight() > 512) {
            res.sendError(400, "Uploaded image width or height exceeds the maximum limit");
            return;
        }

        String userID = JWT.parseToken(token).getSubject();
        // profile photos path: /assets/users/{userID}/profiles/file_count + 1.format
        String path = "src/main/java/app/assets/users/" + userID + "/profiles";
        File directory = new File(path);
        if (directory.exists()) {
            path += String.format("/%s.%s", directory.list().length + 1, fileFormat);
        } else {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                res.sendError(500, "Internal Server Error");
                throw e;
            }
            path += String.format("/1.%s", fileFormat);
        }

        try {
            Files.copy(new ByteArrayInputStream(file), Path.of(path), StandardCopyOption.REPLACE_EXISTING);
            res.sendMessage("Profile photo changed");
        } catch (IOException e) {
            res.sendError(500, "Internal Server Error");
            throw new RuntimeException(e);
        }
    }

    @Get("/{userID}/banner")
    public void getBanner(Req req, Res res) throws IOException, notFoundException {
        String id = req.getDynamicParameters().get("userID");
        System.out.println(UserController.doesUserExist(Long.parseLong(id)));
        if (!UserController.doesUserExist(Long.parseLong(id))) {
            res.sendError(404, "User not found");
            return;
        }

        String path = "src/main/java/app/assets/users/" + req.getDynamicParameters().get("userID") + "/banner/image.png";
        File directory = new File(path);
        if (!directory.exists() || (directory.listFiles() != null && directory.listFiles().length == 0)) {
            path = "src/main/java/app/assets/users/default/banner/image.png";
        }


        File image = new File(path);
        try {
            res.sendFile(image, "image/png");
        } catch (notFoundException e) {
            res.sendError(500, "Internal Server Error");
            throw e;
        }
    }

    @Post("/banner")
    public void postBanner(Req req, Res res) throws IOException {
        String token = req.getCookies().get("accessToken");
        if (!Auth.isAuthorized(req.getCookies())) {
            res.sendError(403, "Not authorized");
            return;
        }

        byte[] file = req.getRequestBodyAsByteArray();
        String fileFormat = getImageType(file);

        if (fileFormat.equals("none")) {
            res.sendError(400, "Bad Request");
        }

        BufferedImage buf = ImageIO.read(new ByteArrayInputStream(file));
        double fileSize = (double) file.length / 1024;
        logger.debug("image received for banner with ", buf.getWidth() + "x" + buf.getHeight(), " resolution and ", fileSize);
        if (fileSize > 2048) {
            res.sendError(400, "Uploaded image size exceeds the maximum limit of 2MB.");
            return;
        } else if (buf.getWidth() > 2560 || buf.getHeight() > 1440) {
            res.sendError(400, "Uploaded image width or height exceeds the maximum limit");
            return;
        }


        String userID = JWT.parseToken(token).getSubject();
        // profile photos path: /assets/users/{userID}/profiles/image.format
        String path = "src/main/java/app/assets/users/" + userID + "/banner";
        File directory = new File(path);
        if (!directory.exists()) {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                res.sendError(500, "Internal Server Error");
                throw e;
            }
        }

        path += String.format("/image.%s", fileFormat);

        try {
            Files.copy(new ByteArrayInputStream(file), Path.of(path), StandardCopyOption.REPLACE_EXISTING);
            res.sendMessage("Profile banner changed");
        } catch (IOException e) {
            res.sendError(500, "Internal Server Error");
            throw new RuntimeException(e);
        }
    }

    private String getImageType(byte[] file) {
        if ((file.length >= PNG_SIGNATURE.length && Arrays.equals(Arrays.copyOf(file, PNG_SIGNATURE.length), PNG_SIGNATURE))) {
            return "png";
        } else if (file.length >= JPG_SIGNATURE.length && Arrays.equals(Arrays.copyOf(file, JPG_SIGNATURE.length), JPG_SIGNATURE)) {
            return "jpg";
        } else {
            return "none";
        }
    }
}
