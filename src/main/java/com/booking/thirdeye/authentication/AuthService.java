package com.booking.thirdeye.authentication;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.booking.thirdeye.authentication.dto.AuthRequest;
import com.booking.thirdeye.authentication.dto.AuthResponse;
import com.booking.thirdeye.authentication.dto.ChangePasswordReq;
import com.booking.thirdeye.authentication.dto.RegisterReq;
import com.booking.thirdeye.authentication.dto.ResetPasswordReq;
import com.booking.thirdeye.configuration.JwtService;
import com.booking.thirdeye.entity.Role;
import com.booking.thirdeye.entity.User;
import com.booking.thirdeye.exception.MasterException;
import com.booking.thirdeye.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        private final AuthenticationManager authenticationManager;

        public AuthResponse register(RegisterReq request) throws MasterException {
                var user = User.builder()
                                .userName(request.getUserName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .createdAt(LocalDateTime.now())
                                .isVerified(false)
                                .role(Role.USER)
                                .build();
                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return AuthResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        public AuthResponse authenticate(AuthRequest request) throws MasterException {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow();
                var jwtToken = jwtService.generateToken(user);
                return AuthResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        public void changePassword(ChangePasswordReq request) throws MasterException {
                User user = repository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new MasterException("User not found"));

                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        throw new MasterException("Current password is incorrect");
                }

                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                        throw new MasterException("New password and confirm password do not match");
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                repository.save(user);
        }

        public void resetPassword(ResetPasswordReq request) throws MasterException {
                User user = repository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new MasterException("User not found"));

                if (!sendVerifyEmail(user.getEmail())) {
                        throw new MasterException("Failed to send reset password email");
                }
        }

        public boolean sendVerifyEmail(String email) {
                return true;
        }

}
