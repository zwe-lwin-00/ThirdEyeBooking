package com.booking.thirdeye.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.thirdeye.authentication.dto.AuthRequest;
import com.booking.thirdeye.authentication.dto.AuthResponse;
import com.booking.thirdeye.authentication.dto.ChangePasswordReq;
import com.booking.thirdeye.authentication.dto.RegisterReq;
import com.booking.thirdeye.authentication.dto.ResetPasswordReq;
import com.booking.thirdeye.exception.MasterException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterReq request) throws MasterException {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request) throws MasterException {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordReq request) throws MasterException {
        service.changePassword(request);
        return ResponseEntity.ok("Password successfully changed.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordReq request) throws MasterException {
        service.resetPassword(request);
        return ResponseEntity.ok("Password reset instructions have been sent.");
    }
}
