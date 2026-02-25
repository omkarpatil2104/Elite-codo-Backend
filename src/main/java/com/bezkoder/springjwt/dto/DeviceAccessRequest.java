package com.bezkoder.springjwt.dto;

import com.bezkoder.springjwt.models.ERole;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DeviceAccessRequest {
    private Long        userId;     // DB PK of User
    private ERole       role;       // ROLE_ADMIN, ROLE_STUDENT, …
    private DeviceInfo  deviceInfo; // above DTO
}