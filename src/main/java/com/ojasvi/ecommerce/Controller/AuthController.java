package com.ojasvi.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ojasvi.ecommerce.DTO.LoginRequest;
import com.ojasvi.ecommerce.DTO.SignupRequest;
import com.ojasvi.ecommerce.DTO.VerifyOtpRequest;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.AuthService;
import com.ojasvi.ecommerce.Util.ResponseStructure;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseStructure<User>> signup(
            @Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }
 
    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<User>> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {

        ResponseStructure<User> response =
                authService.login(request);

        if (response.getData() != null) {

            session.setAttribute("user", response.getData());
        }

        return ResponseEntity.ok(response);
    }
 
    @PostMapping("/admin/send-otp")
    public ResponseEntity<ResponseStructure<String>> adminSendOtp(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminSendOtp(request));
    }
 
    @PostMapping("/admin/login")
    public ResponseEntity<ResponseStructure<User>> adminLogin(
            @Valid @RequestBody VerifyOtpRequest request,
            HttpSession session) {

        ResponseStructure<User> response =
                authService.verifyAdminOtp(request);

        if (response.getData() != null) {

            session.setAttribute("user", response.getData());
        }

        return ResponseEntity.ok(response);
    }
}