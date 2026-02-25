package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private Long id;
    private String oldPassword;
    private String newPassword;
}
