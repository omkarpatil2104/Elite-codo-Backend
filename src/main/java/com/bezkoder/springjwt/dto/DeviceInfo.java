package com.bezkoder.springjwt.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DeviceInfo {
    private String deviceId;      // front-end generated UUID
    private String userAgent;     // navigator.userAgent
    private String ipAddress;     // leave blank; server will infer if needed
}