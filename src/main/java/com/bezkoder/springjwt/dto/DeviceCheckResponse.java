package com.bezkoder.springjwt.dto;


import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCheckResponse {
    private boolean registered;
    private boolean canLogin;
    private String message;
    private LocalDateTime lastActive;
}
