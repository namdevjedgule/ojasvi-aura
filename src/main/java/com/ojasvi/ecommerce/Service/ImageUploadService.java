package com.ojasvi.ecommerce.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageUploadService {

	private static final Path UPLOAD_DIR = Paths.get(
            System.getProperty("user.dir"),
            "uploads",
            "products"
    );

    private static final Set<String> ALLOWED_TYPES = Set.of(
    	    "image/jpeg",
    	    "image/jpg",
    	    "image/png",
    	    "image/webp"
    	);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    public String upload(MultipartFile file) {

        validateImage(file);

        try {

            Files.createDirectories(UPLOAD_DIR);

            String extension = getSafeExtension(file);

            String fileName = UUID.randomUUID() + "." + extension;

            Path destination = UPLOAD_DIR.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/uploads/products/" + fileName;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Image upload failed: " + e.getMessage(),
                    e
            );
        }
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is empty");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_TYPES.contains(contentType.toLowerCase())) {

            throw new RuntimeException(
                    "Only JPG, PNG, WEBP and GIF images are allowed"
            );
        }

        String extension = StringUtils.getFilenameExtension(
                file.getOriginalFilename()
        );

        if (extension == null ||
                !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {

            throw new RuntimeException(
                    "Unsupported image extension"
            );
        }
    }

    private String getSafeExtension(MultipartFile file) {

        String extension = StringUtils.getFilenameExtension(
                file.getOriginalFilename()
        );

        if (extension == null) {
            return "jpg";
        }

        extension = extension.toLowerCase();

        return switch (extension) {
            case "jpeg" -> "jpg";
            default -> extension;
        };
    }

    public void delete(String imageUrl) {

        try {

            Path filePath = Path.of(
                    "src",
                    "main",
                    "resources",
                    "static" + imageUrl
            );

            Files.deleteIfExists(filePath);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Image delete failed: " + e.getMessage(),
                    e
            );
        }
    }
}
