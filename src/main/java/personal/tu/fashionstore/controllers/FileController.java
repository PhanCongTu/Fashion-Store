package personal.tu.fashionstore.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import personal.tu.fashionstore.exceptions.NotFoundException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@RestController
//@ApiPrefixController("files")
@RequestMapping("/files")
@CrossOrigin("*")
public class FileController {
    @Autowired
    private Cloudinary cloudinary;

    @PostMapping(value = "/cloud/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFileCloud(@RequestPart("file") MultipartFile file) throws IOException {
        // Kiểm tra nếu file là ảnh
        boolean isImage = file.getContentType().startsWith("image/");
        if (isImage) {
            // Đọc dữ liệu của file vào một mảng byte
            byte[] fileData = file.getBytes();
            // Tạo một đối tượng BufferedImage từ dữ liệu của file
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileData));
            // Thiết lập kích thước mới cho ảnh (full HD)
            int newWidth = 1920;
            int newHeight = 1080;
            // Kiểm tra kích thước của ảnh
            boolean isLargeImage = originalImage != null && originalImage.getWidth() > newWidth && originalImage.getHeight() > newHeight;
            // Nếu ảnh lớn hơn kích thước cho phép, resize ảnh
            if (isLargeImage) {
                // Tạo một đối tượng BufferedImage mới với kích thước mới và vẽ ảnh gốc lên đó
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();

                // Chuyển đổi ảnh mới thành mảng byte
                ByteArrayOutputStream newImageBytes = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", newImageBytes);
                fileData = newImageBytes.toByteArray();
            }
            // Upload file lên Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(fileData, ObjectUtils.emptyMap());
            // Trả về URL của file đã upload
            String fileUrl = (String) uploadResult.get("url");
            return new ResponseEntity<>(fileUrl, HttpStatus.OK);
        } else {
            // Trường hợp file không phải là ảnh, upload file thẳng lên Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String fileUrl = (String) uploadResult.get("url");
            return new ResponseEntity<>(fileUrl, HttpStatus.OK);
        }
    }

    @DeleteMapping("/cloud/delete/{publicId}")
    public ResponseEntity<String> deleteFileCloud(@PathVariable String publicId) {
        try {
            if (publicId == null || publicId.trim().equals(""))
                throw new NotFoundException("Not found publicId");
            Map<?, ?> publicIdresult = cloudinary.api().deleteResources(Arrays.asList(publicId), ObjectUtils.emptyMap());
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/local/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFileLocal(HttpServletRequest request, @RequestPart("file") MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");
        String fileName = System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        // Kiểm tra nếu file là ảnh
        boolean isImage = file.getContentType().startsWith("image/");
        if (isImage) {
            // Đọc dữ liệu của file vào một mảng byte
            byte[] fileData = file.getBytes();
            // Tạo một đối tượng BufferedImage từ dữ liệu của file
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileData));
            // Thiết lập kích thước mới cho ảnh (full HD)
            int newWidth = 1920;
            int newHeight = 1080;
            // Kiểm tra kích thước của ảnh
            boolean isLargeImage = originalImage != null && originalImage.getWidth() > newWidth && originalImage.getHeight() > newHeight;
            // Nếu ảnh lớn hơn kích thước cho phép, resize ảnh
            if (isLargeImage) {
                // Tạo một đối tượng BufferedImage mới với kích thước mới và vẽ ảnh gốc lên đó
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();

                // Chuyển đổi ảnh mới thành mảng byte
                ByteArrayOutputStream newImageBytes = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", newImageBytes);
                fileData = newImageBytes.toByteArray();
            }
            // Upload file lên Cloudinary
            // Trả về URL của file đã upload

            Path filePath = Paths.get(uploadPath.toString(), fileName);
            Files.write(filePath, fileData);
            return new ResponseEntity<>(((request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1") ? "localhost" : request.getRemoteAddr()) + ":" + request.getLocalPort() + "/uploads/" + fileName), HttpStatus.OK);
        } else {
            // Trường hợp file không phải là ảnh, upload file thẳng lên Cloudinary
            Path filePath = Paths.get(uploadPath.toString(), fileName);
            Files.write(filePath, file.getBytes());
            return new ResponseEntity<>(((request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1") ? "localhost" : request.getRemoteAddr()) + ":" + request.getLocalPort() + "/uploads/" + fileName), HttpStatus.OK);
        }
    }

    @DeleteMapping("/local/delete/{fileName}")
    public ResponseEntity<String> deleteFileLocal(@PathVariable("fileName") String fileName) {
        try {
            if (fileName == null || fileName.trim() == "")
                throw new NotFoundException("Not found publicId");
            File file = new File(System.getProperty("user.dir"), "uploads/" + fileName);
            if (file.delete()) {
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                throw new RuntimeException("Some thing went wrong!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
