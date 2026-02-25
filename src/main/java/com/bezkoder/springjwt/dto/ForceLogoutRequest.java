package com.bezkoder.springjwt.dto;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ForceLogoutRequest {
    private Long   userId;
    private String currentDeviceId;
}