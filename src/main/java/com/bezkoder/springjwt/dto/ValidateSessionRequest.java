package com.bezkoder.springjwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ValidateSessionRequest {
    private Long   userId;
    private String deviceId;
    private String role;      // e.g. "ROLE_ADMIN"
}