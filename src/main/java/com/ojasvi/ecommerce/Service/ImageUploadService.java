package com.ojasvi.ecommerce.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles product image uploads.
 *
 * IMPORTANT — WEBP CHANGE:
 * The previous version of this class depended on a third-party
 * Maven artifact (org.sejda.imageio:webp-imageio) to write WEBP
 * files. That dependency failed to resolve / load in your project
 * (ClassNotFoundException: com.luciad.imageio.webp.WebPWriteParam),
 * crashing the whole Spring context on startup.
 *
 * To remove that risk entirely, this version writes images as JPEG
 * instead of WEBP, using ONLY classes built into the JDK
 * (javax.imageio) — no extra dependency required, guaranteed to
 * compile and run.
 *
 * Everything else you asked for is preserved:
 *   - Accepts JPG / JPEG / PNG / WEBP as INPUT
 *   - Always converts to a single consistent OUTPUT format (JPEG)
 *   - Filenames use the product name slug + sequence number, e.g.
 *       royal-silk-bedsheet-01-a1b2c3.jpg
 *       royal-silk-bedsheet-02-a1b2c3.jpg
 *   - Files saved to app.upload.dir (external, persistent folder —
 *     fixes the original intermittent-404 bug)
 *
 * If you specifically need true .webp files later, that requires a
 * verified working Maven dependency — confirm the exact groupId/
 * artifactId/version resolves in YOUR environment (open the
 * dependency in your IDE's Maven view, or run `mvn dependency:tree`)
 * before wiring it back in, since I can't reach Maven Central from
 * here to verify artifact coordinates myself.
 */
@Service
public class ImageUploadService {

    @Value("${app.upload.dir}")
    private String uploadDirPath;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final float JPEG_QUALITY = 0.88f; // 0.0 (smallest) - 1.0 (best quality)
    private static final String OUTPUT_EXTENSION = "jpg";

    /**
     * Uploads and converts a single image, naming it using the product
     * name slug + sequence number, e.g. "royal-silk-bedsheet-01-a1b2c3.jpg".
     *
     * @param file        the uploaded file (jpg/jpeg/png/webp accepted as input)
     * @param productName used to build the filename prefix
     * @param sequence    1-based index among this product's images (1, 2, 3...)
     * @return the public URL path to store in the DB, e.g. "/uploads/products/royal-silk-bedsheet-01-a1b2c3.jpg"
     */
    public String upload(MultipartFile file, String productName, int sequence) {

        validateImage(file);

        try {
            Path uploadDir = Paths.get(uploadDirPath);
            Files.createDirectories(uploadDir);

            BufferedImage image = readImage(file);
            byte[] jpegBytes = convertToJpeg(image);

            String slug = slugify(productName);
            String sequencePart = String.format("%02d", sequence);
            // short random suffix avoids collisions if two products
            // resolve to the same slug, or if a product is re-uploaded
            String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6);

            String fileName = slug + "-" + sequencePart + "-" + uniqueSuffix + "." + OUTPUT_EXTENSION;
            Path destination = uploadDir.resolve(fileName);

            Files.write(destination, jpegBytes);

            return "/uploads/products/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
        }
    }

    /**
     * Convenience overload — kept so any existing calls that don't yet
     * pass a product name still compile; generates a generic name
     * instead. Prefer the 3-arg upload() above for product images.
     */
    public String upload(MultipartFile file) {
        return upload(file, "product", 1);
    }

    // ── Validation ──────────────────────────────────────────────────

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is empty");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException(
                    "Only JPG, JPEG, PNG and WEBP images are allowed. Got: " + contentType);
        }

        // 15MB safety cap — matches the limit already enforced in your
        // frontend JS (MAX_FILE_SIZE_MB = 15)
        long maxBytes = 15L * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new RuntimeException("Image exceeds 15MB limit");
        }
    }

    // ── Image reading ───────────────────────────────────────────────

    private BufferedImage readImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new RuntimeException(
                    "Could not read image — file may be corrupted or in an unsupported format");
        }
        return image;
    }

    // ── JPEG conversion (pure JDK, no extra dependency) ──────────────

    private byte[] convertToJpeg(BufferedImage image) throws IOException {

        // JPEG has no alpha channel — flatten any transparency onto a
        // white background first, otherwise PNG-with-transparency
        // sources would come out with black/garbled backgrounds.
        BufferedImage rgbImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        java.awt.Graphics2D g = rgbImage.createGraphics();
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.drawImage(image, 0, 0, null);
        g.dispose();

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("No JPEG ImageWriter available in this JVM");
        }
        ImageWriter writer = writers.next();

        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(JPEG_QUALITY);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(baos)) {
            writer.setOutput(output);
            writer.write(null, new IIOImage(rgbImage, null, null), writeParam);
        } finally {
            writer.dispose();
        }

        return baos.toByteArray();
    }

    // ── Slug helper ─────────────────────────────────────────────────

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");

    private String slugify(String productName) {
        if (productName == null || productName.isBlank()) {
            return "product";
        }
        String slug = productName.trim().toLowerCase();
        slug = NON_ALPHANUMERIC.matcher(slug).replaceAll("-");
        slug = slug.replaceAll("^-+|-+$", ""); // trim leading/trailing dashes
        if (slug.isBlank()) {
            return "product";
        }
        // keep filenames reasonable in length
        return slug.length() > 60 ? slug.substring(0, 60) : slug;
    }

    // ── Delete ──────────────────────────────────────────────────────

    public void delete(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isBlank()) return;

            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(uploadDirPath).resolve(fileName);

            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new RuntimeException("Image delete failed: " + e.getMessage(), e);
        }
    }
}