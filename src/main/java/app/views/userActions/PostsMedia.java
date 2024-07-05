package app.views.userActions;

import app.services.AuthService;
import app.services.UserService;
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;

@Endpoint("/posts")
@UseLogger
public class PostsMedia {
    private static Logger logger;

    private static final byte[] PNG_SIGNATURE =
            new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] JPG_SIGNATURE =
            new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] MP4_SIGNATURE =
            new byte[]{0x66, 0x74, 0x79, 0x70}; // "ftyp"

    @Post("/{postID}/file")
    public void postFile(Req req, Res res) throws IOException, notFoundException {
        String token = req.getCookies().get("accessToken");
        if (!AuthService.isAuthorized(req.getCookies())) {
            res.sendError(403, "Not authorized");
            return;
        }
        String userID = JWT.parseToken(token).getSubject();

        byte[] file = req.getRequestBodyAsByteArray();
        String fileFormat = getFileType(file);

        if ("none".equals(fileFormat)) {
            res.sendError(400, "Unsupported file type.");
            return;
        }

        String postIDStr = req.getDynamicParameters().get("postID");
        if (postIDStr == null || postIDStr.isEmpty()) {
            res.sendError(400, "Invalid postID");
            return;
        }
        Long postID = Long.parseLong(postIDStr);

        app.database.schema.Post post = UserService.getPost(postID);
        if (!post.getOwner().getId().equals(Long.parseLong(userID))) {
            res.sendError(401, "Not allowed to do this");
            return;
        }

        double fileSize = (double) file.length / 1024; // File size in KB
        if (fileSize > 4096) { // 4MB limit
            res.sendError(400, "Uploaded file size exceeds the maximum limit of 4MB.");
            return;
        }

        if ("png".equals(fileFormat) || "jpg".equals(fileFormat)) {
            handleImageFile(file, res, userID, postID, fileFormat);
        } else if ("mp4".equals(fileFormat)) {
            handleVideoFile(file, res, userID, postID, fileFormat);
        }
    }

    @Get("/{postID}/file")
    public void getMedia(Req req, Res res) throws IOException, notFoundException {
        String postIDStr = req.getDynamicParameters().get("postID");
        if (postIDStr == null || postIDStr.isEmpty()) {
            res.sendError(400, "Invalid postID");
            return;
        }
        Long postID = Long.parseLong(postIDStr);

        String path = "src/main/java/app/assets/posts/" + postID;
        File directory = new File(path);
        if (!directory.exists() || (directory.listFiles() != null && directory.listFiles().length == 0)) {
            res.sendError(404, "Not found");
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.startsWith("file."));
        if (files == null || files.length == 0) {
            res.sendError(404, "Not found");
            return;
        }

        File mediaFile = files[0];
        String mimeType = Files.probeContentType(mediaFile.toPath());

        try {
            res.sendFile(mediaFile, mimeType);
        } catch (IOException e) {
            res.sendError(500, "Internal Server Error");
            throw e;
        }
    }


    private void handleImageFile(byte[] file, Res res, String userID, Long postID, String fileFormat) throws IOException {
        BufferedImage buf = ImageIO.read(new ByteArrayInputStream(file));
        if (buf == null) {
            res.sendError(400, "Uploaded image could not be processed.");
            return;
        }

        logger.debug("Image received for new post with resolution: {}x{}, size: {} KB", buf.getWidth(), buf.getHeight(), (double) file.length / 1024);

        if (buf.getWidth() > 2048 || buf.getHeight() > 2048) {
            res.sendError(400, "Uploaded image width or height exceeds the maximum limit.");
            return;
        }

        saveFile(file, res, userID, postID, fileFormat);
    }

    private void handleVideoFile(byte[] file, Res res, String userID, Long postID, String fileFormat) throws IOException {
        logger.debug("Video received for new post with size: {} KB", (double) file.length / 1024);

        // TODO: Add some additional video validations if necessary

        saveFile(file, res, userID, postID, fileFormat);
    }

    private void saveFile(byte[] file, Res res, String userID, Long postID, String fileFormat) throws IOException {
        String path = "src/main/java/app/assets/posts/" + postID;
        File directory = new File(path);
        if (directory.exists()) {
            path += String.format("/file.%s", fileFormat);
        } else {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                res.sendError(500, "Internal Server Error");
                throw e;
            }
            path += String.format("/file.%s", fileFormat);
        }

        try {
            Files.copy(new ByteArrayInputStream(file), Path.of(path), StandardCopyOption.REPLACE_EXISTING);
            res.sendMessage("Media Uploaded Successfully");
        } catch (IOException e) {
            res.sendError(500, "Internal Server Error");
            throw new RuntimeException(e);
        }
    }

    private String getFileType(byte[] file) {
        if (file.length >= PNG_SIGNATURE.length &&
                Arrays.equals(Arrays.copyOf(file, PNG_SIGNATURE.length), PNG_SIGNATURE)) {
            return "png";
        } else if (file.length >= JPG_SIGNATURE.length &&
                Arrays.equals(Arrays.copyOf(file, JPG_SIGNATURE.length), JPG_SIGNATURE)) {
            return "jpg";
        } else if (file.length >= 8 + MP4_SIGNATURE.length &&
                Arrays.equals(Arrays.copyOfRange(file, 4, 4 + MP4_SIGNATURE.length), MP4_SIGNATURE)) {
            return "mp4";
        } else {
            return "none";
        }
    }
}
