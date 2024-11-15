package com.booking.thirdeye.responddto;

import com.booking.thirdeye.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Integer userId;
    private String userName;
    private String email;
    private String password;
    private Boolean isVerified;
    private Role role;
}
