package com.ojasvi.ecommerce.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.DTO.LoginRequest;
import com.ojasvi.ecommerce.DTO.SignupRequest;
import com.ojasvi.ecommerce.DTO.VerifyOtpRequest;
import com.ojasvi.ecommerce.Entity.Role;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Repository.RoleRepository;
import com.ojasvi.ecommerce.Repository.UserRepository;
import com.ojasvi.ecommerce.Util.ResponseStructure;

@Service
public class AuthService {
 
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private RoleRepository roleRepository;
 
    @Autowired
    private OtpService otpService;
 
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    public ResponseStructure<User> signup(SignupRequest request) {
 
        ResponseStructure<User> response = new ResponseStructure<>();
 
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            response.setStatus(400);
            response.setMessage("Email already registered");
            response.setData(null);
            return response;
        }
 
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
 
        Role role = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));
 
        user.setRole(role);
 
        User saved = userRepository.save(user);
 
        response.setStatus(200);
        response.setMessage("Signup successful! Welcome to Ojasvi.");
        response.setData(saved);
        return response;
    }
 
    public ResponseStructure<User> login(LoginRequest request) {
 
        ResponseStructure<User> response = new ResponseStructure<>();
 
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
 
        if (optionalUser.isEmpty()) {
            response.setStatus(404);
            response.setMessage("No account found with this email");
            return response;
        }
 
        User user = optionalUser.get();
 
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setStatus(401);
            response.setMessage("Incorrect password");
            return response;
        }
 
        if (!user.getRole().getRoleName().equalsIgnoreCase("CUSTOMER")) {
            response.setStatus(403);
            response.setMessage("This account is not a customer account");
            return response;
        }
 
        response.setStatus(200);
        response.setMessage("Login successful");
        response.setData(user);
        return response;
    }
 
    public ResponseStructure<String> adminSendOtp(LoginRequest request) {
 
        ResponseStructure<String> response = new ResponseStructure<>();
 
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
 
        if (optionalUser.isEmpty()) {
            response.setStatus(404);
            response.setMessage("Admin account not found");
            return response;
        }
 
        User user = optionalUser.get();
 
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setStatus(401);
            response.setMessage("Invalid password");
            return response;
        }
 
        if (!user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            response.setStatus(403);
            response.setMessage("Access denied — not an admin account");
            return response;
        }
 
        if (otpService.hasValidOtp(user.getEmail())) {
            response.setStatus(429);
            response.setMessage("An OTP was already sent. Please wait before requesting a new one.");
            return response;
        }
 
        String otp = otpService.generateAndSaveOtp(user.getEmail()); // UPDATED method name
        otpService.sendOtpEmail(user.getEmail(), otp);
 
        response.setStatus(200);
        response.setMessage("OTP sent successfully to your registered email");
        response.setData("OTP_SENT");
        return response;
    }
 
    public ResponseStructure<User> verifyAdminOtp(VerifyOtpRequest request) {
 
        ResponseStructure<User> response = new ResponseStructure<>();
 
        if (request.getEmail() == null || request.getOtp() == null) {
            response.setStatus(400);
            response.setMessage("Email and OTP are required");
            return response;
        }
 
        boolean isValid = otpService.verifyOtp(
                request.getEmail().trim(),
                request.getOtp().trim()
        );
 
        if (!isValid) {
            response.setStatus(401);
            response.setMessage("Invalid or expired OTP. Please request a new one.");
            return response;
        }
 
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail().trim());
 
        if (optionalUser.isEmpty()) {
            response.setStatus(404);
            response.setMessage("Admin not found");
            return response;
        }
 
        User user = optionalUser.get();
 
        if (!user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            response.setStatus(403);
            response.setMessage("Access denied");
            return response;
        }
 
        otpService.clearOtp(request.getEmail().trim());
 
        response.setStatus(200);
        response.setMessage("Admin login successful");
        response.setData(user);
        return response;
    }
}