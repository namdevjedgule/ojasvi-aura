package com.ojasvi.ecommerce.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Repository.UserRepository;
import com.ojasvi.ecommerce.Util.RoleConstants;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    private static final String UPLOAD_DIR =
            "src/main/resources/static/uploads/user/";

	public long countCustomers() {
	    return userRepository.countByRole_IdAndIsActiveTrue(
	            RoleConstants.CUSTOMER_ID);
	}

    public List<User> findAllCustomers() {
        return userRepository.findByRole_IdAndIsActiveTrue(
                RoleConstants.CUSTOMER_ID);
    }
    
    public User findById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    public void updateProfile(Long userId,
                              String fullName,
                              String email,
                              String mobile) {

        User user = findById(userId);

        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobile(mobile);

        userRepository.save(user);
    }

    public void changePassword(Long userId,
                               String currentPassword,
                               String newPassword) {

        User user = findById(userId);

        boolean matches =
                passwordEncoder.matches(
                        currentPassword,
                        user.getPassword());

        if (!matches) {
            throw new RuntimeException(
                    "Current password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }

    public void uploadProfileImage(Long userId,
                                   MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Please select image");
        }

        User user = findById(userId);

        try {

            Files.createDirectories(
                    Paths.get(UPLOAD_DIR));

            String fileName =
                    UUID.randomUUID() + "_" +
                    image.getOriginalFilename();

            Path path =
                    Paths.get(UPLOAD_DIR + fileName);

            Files.copy(
                    image.getInputStream(),
                    path,
                    StandardCopyOption.REPLACE_EXISTING);

            user.setProfileImage(
                    "/uploads/user/" + fileName);

            userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Image upload failed");
        }
    }

    public void removeProfileImage(Long userId) {

        User user = findById(userId);

        String imagePath = user.getProfileImage();

        if (imagePath != null &&
            !imagePath.isBlank()) {

            try {

                String filePath =
                        "src/main/resources/static"
                                + imagePath;

                Files.deleteIfExists(
                        Paths.get(filePath));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        user.setProfileImage(null);

        userRepository.save(user);
    }

}
