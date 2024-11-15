package com.booking.thirdeye.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordReq {
    private String email;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
