package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class NewPasswordRequest {
    private Long id;
    private String newPassword;
    private String confirmPassword;
}
