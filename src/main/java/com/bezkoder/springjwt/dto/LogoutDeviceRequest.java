package com.bezkoder.springjwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LogoutDeviceRequest {
    private Long   userId;     // numeric PK
    private String deviceId;   // UUID/identifier
}